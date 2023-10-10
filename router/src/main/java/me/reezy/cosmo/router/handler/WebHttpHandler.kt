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
class WebHttpHandler(private val route: String) : RouteHandler {
    override fun handle(request: RouteRequest): Boolean {
        val uri = request.uri
        if (setOf("web-http", "web-https").contains(uri.scheme)) {
            val bundle = Bundle()
            bundle.putString("url", uri.toString().substring(4))
            return Router.routeTo(request.context, Uri.parse(route), bundle)
        }
        return false
    }
}