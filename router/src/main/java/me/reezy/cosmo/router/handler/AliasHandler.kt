package me.reezy.cosmo.router.handler

import androidx.core.net.toUri
import me.reezy.cosmo.router.RouteHandler
import me.reezy.cosmo.router.RouteRequest
import me.reezy.cosmo.router.Router

class AliasHandler(private val urls: Map<String, String?>) : RouteHandler {
    override fun handle(request: RouteRequest): Boolean {
        val uri = request.uri.toString()
        val prefix = "alias://"
        if (uri.startsWith(prefix)) {
            val url = urls[uri.substring(prefix.length)] ?: return false
            return Router.routeTo(request.context, url.toUri())
        }
        return false
    }
}