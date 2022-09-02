package me.reezy.cosmo.router.forwarder

import android.net.Uri
import android.os.Bundle
import me.reezy.cosmo.router.RouteForwarder
import me.reezy.cosmo.router.RouteRequest
import me.reezy.cosmo.router.Router

class WebViewForwarder(private val route: String, private val domains: Set<String> = setOf()) : RouteForwarder {

    override fun forward(request: RouteRequest): Boolean {
        val uri = request.uri
        if (setOf("http", "https").contains(uri.scheme) && domains.contains(uri.host)) {
            val bundle = Bundle()
            bundle.putString("url", uri.toString())
            return Router.routeTo(request.context, Uri.parse(route), bundle)
        }
        return false
    }
}