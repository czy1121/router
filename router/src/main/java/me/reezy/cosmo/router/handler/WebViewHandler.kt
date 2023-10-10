package me.reezy.cosmo.router.handler

import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import me.reezy.cosmo.router.RouteHandler
import me.reezy.cosmo.router.RouteRequest
import me.reezy.cosmo.router.Router


/**
 * http/https to WebView
 * */
class WebViewHandler(private val route: String, private val domains: Set<String> = setOf()) : RouteHandler {

    override fun handle(request: RouteRequest): Boolean {
        val uri = request.uri
        if (setOf("http", "https").contains(uri.scheme) && domains.contains(uri.host)) {
            val bundle = Bundle()
            bundle.putString("url", uri.toString())
            return Router.routeTo(request.context, Uri.parse(route), bundle)
        }
        return false
    }
}