package me.reezy.router

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import me.reezy.router.internal.Route
import me.reezy.router.internal.RouteTable


object Router {

    private val table = RouteTable()

    private val schemesIn: MutableSet<String> = mutableSetOf()
    private val schemesOut: MutableSet<String> = mutableSetOf()
    private val httpDomainsIn: MutableSet<String> = mutableSetOf()
    private val httpDomainsOut: MutableSet<String> = mutableSetOf()
    private val interceptors: MutableList<RouteInterceptor> = mutableListOf()
    private val namedInterceptors: MutableMap<String, RouteInterceptor> = mutableMapOf()

    fun init(context: Context) {
        val modules = context.meta("modules") ?: return
        init(modules.split(",").toTypedArray())
    }

    fun init(modules: Array<String>) {
        modules.map { it.replace("[^0-9a-zA-Z_]+".toRegex(), "") }.forEach {
            try {
                val loaderClass = Class.forName("$PKG.generated.RouteLoader_$it")
                val loader = loaderClass.newInstance()
                loaderClass.getMethod("load", RouteTable::class.java).invoke(loader, table)
            } catch (e: ClassNotFoundException) {
                Log.w(PKG, "There is no Loader in module: $it.")
            } catch (e: Exception) {
                Log.w(PKG, "${e.message}")
            }
        }
    }

    fun addSchemesIn(vararg schemes: String) {
        schemesIn.addAll(schemes)
    }
    fun addSchemesOut(vararg schemes: String) {
        schemesOut.addAll(schemes)
    }
    fun addHttpDomainsIn(vararg domains: String) {
        httpDomainsIn.addAll(domains)
    }
    fun addHttpDomainsOut(vararg domains: String) {
        httpDomainsOut.addAll(domains)
    }

    fun addInterceptor(interceptor: RouteInterceptor) {
        interceptors.add(interceptor)
    }

    fun addNamedInterceptor(name: String, interceptor: RouteInterceptor) {
        namedInterceptors[name] = interceptor
    }

    fun routeTo(context: Context, uri: Uri, options: RouteOptions = RouteOptions()): Boolean {

        if (uri.path.isNullOrBlank() && uri.host.isNullOrBlank()) {
            return false
        }

        val request = RouteRequest(context, uri, options.params, options.intent, options.requestCode, options.enterAnim, options.exitAnim, options.activityOptions)


        for (interceptor in interceptors) {
            if (interceptor.intercept(request)) {
                return true
            }
        }

        if (uri.isRelative) {
            return route(request, uri.path!!)
        }
        if (schemesIn.contains(uri.scheme)) {
            return route(request, uri.host + uri.path)
        }
        if (schemesOut.contains(uri.scheme)) {
            return Router.browse(request.context, uri)
        }
        if (schemesHttp.contains(uri.scheme)) {
            if (httpDomainsIn.contains(uri.host)) {
                val path = uri.path?.removePrefix("/") ?: return false
                return route(request, path)
            }
            if (httpDomainsOut.contains(uri.host)) {
                return Router.browse(request.context, uri)
            }
        }
        return false
    }

    fun browse(context: Context, uri: Uri): Boolean {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = uri

        val resolved = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null
        if (resolved) {
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return true
        }
        return false
    }

    private fun route(request: RouteRequest, route: String): Boolean {
        table.match(route)?.let {
            it.second?.map { item ->
                request.params.putString(item.key, item.value)
            }
            return handle(request, it.first)
        }
        return false
    }

    private fun handle(request: RouteRequest, route: Route): Boolean {
        route.interceptors?.forEach {
            if (namedInterceptors[it]?.intercept(request)!!) {
                return true
            }
        }
        if (RouteCall::class.java.isAssignableFrom(route.clazz)) {
            handleCall(request, route)
            return true
        } else if (Activity::class.java.isAssignableFrom(route.clazz)) {
            handleActivity(request, route)
            return true
        }
        return false
    }

    private fun handleCall(request: RouteRequest, route: Route) {
        try {
            val call = route.clazz.newInstance() as RouteCall
            call.call(request)
        } catch (e: ClassNotFoundException) {
            Log.w(PKG, "There is no handler in module: ${route.clazz}.")
        } catch (e: Exception) {
            Log.w(PKG, "${e.message}")
        }
    }

    private fun handleActivity(request: RouteRequest, route: Route) {
        val intent = request.intent ?: Intent()
        val context = request.context
        val uri = request.uri

        intent.component = ComponentName(context, route.clazz)
        intent.putExtras(request.params)
        intent.putExtras(bundleQuery(uri))
        intent.addFlags(route.flags)

        if (context is Activity) {
            if (request.requestCode > 0) {
                ActivityCompat.startActivityForResult(context, intent, request.requestCode, request.activityOptions
                )
            } else {
                ActivityCompat.startActivity(context, intent, request.activityOptions)
            }
            if (request.enterAnim > 0 && request.exitAnim > 0) {
                context.overridePendingTransition(request.enterAnim, request.exitAnim)
            }
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(context, intent, request.activityOptions)
        }
    }


    private fun bundleQuery(uri: Uri): Bundle {
        val bundle = Bundle()

        for (key in uri.queryParameterNames) {
            val values = uri.getQueryParameters(key)
            if (values.size > 1) {
                bundle.putStringArray(key, values.toTypedArray())
            } else if (values.size == 1) {
                bundle.putString(key, values[0])
            }
        }
        return bundle
    }
}
