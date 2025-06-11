package me.reezy.cosmo.router

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
import me.reezy.cosmo.router.internal.Route
import me.reezy.cosmo.router.internal.RouteTable


object Router {
    private var TAG = "OoO.router"

    private val table = RouteTable()
    private val schemes: MutableSet<String> = mutableSetOf()
    private val handlers: MutableList<RouteHandler> = mutableListOf()
    private val namedInterceptors: MutableMap<String, RouteInterceptor> = mutableMapOf()

    fun init(context: Context) {
        val modules = context.meta("modules") ?: return
        init(context.packageName, modules.split(",").toTypedArray())
    }

    fun init(packageName: String, modules: Array<String>) {
        modules.map { it.replace("[^0-9a-zA-Z_]+".toRegex(), "") }.forEach {
            try {
                val loaderClass = Class.forName("$packageName.generated.RouteLoader_$it")
                val loader = loaderClass.newInstance()
                loaderClass.getMethod("load", RouteTable::class.java).invoke(loader, table)
            } catch (e: ClassNotFoundException) {
                Log.w(TAG, "There is no Loader in module: $it.")
            } catch (e: Exception) {
                Log.w(TAG, "${e.message}")
            }
        }
    }

    fun addSchemes(vararg schemes: String) {
        this.schemes.addAll(schemes)
    }

    fun addHandler(handler: RouteHandler) {
        this.handlers.add(handler)
    }

    fun addNamedInterceptor(name: String, handler: RouteInterceptor) {
        namedInterceptors[name] = handler
    }

    fun routeTo(context: Context, uri: Uri, params: Bundle = Bundle(), intent: Intent? = null, options: RouteOptions = RouteOptions()): Boolean {

        if (uri.path.isNullOrBlank() && uri.host.isNullOrBlank()) {
            return false
        }

        val request = RouteRequest(context, uri, params, intent, options.requestCode, options.enterAnim, options.exitAnim, options.activityOptions)

        if (uri.isRelative) {
            return route(request, uri.path!!)
        }
        if (schemes.contains(uri.scheme)) {
            return route(request, uri.host + uri.path)
        }

        for (handler in handlers) {
            if (handler.handle(request)) {
                return true
            }
        }


        return false
    }

    fun browse(context: Context, uri: Uri): Boolean {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = uri
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (_: Throwable) {

        }

        return true
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
            if (namedInterceptors[it]?.intercept(request) == true) {
                return true
            }
        }
        if (RouteCallable::class.java.isAssignableFrom(route.clazz)) {
            handleCallable(request, route)
            return true
        }
        if (Activity::class.java.isAssignableFrom(route.clazz)) {
            handleActivity(request, route)
            return true
        }
        return false
    }

    private fun handleCallable(request: RouteRequest, route: Route) {
        try {
            val call = route.clazz.newInstance() as RouteCallable
            call.call(request)
        } catch (e: ClassNotFoundException) {
            Log.w(TAG, "There is no handler in module: ${route.clazz}.")
        } catch (e: Exception) {
            Log.w(TAG, "${e.message}")
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
                ActivityCompat.startActivityForResult(context, intent, request.requestCode, request.activityOptions)
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


    internal fun log(message: String) {
        Log.d(TAG, message)
    }
}
