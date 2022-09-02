package me.reezy.cosmo.router.forwarder

import me.reezy.cosmo.router.RouteForwarder
import me.reezy.cosmo.router.RouteRequest
import me.reezy.cosmo.router.Router

class OutgoingForwarder(private val domains: Set<String> = setOf(), private val schemes: Set<String> = setOf()) : RouteForwarder {
    override fun forward(request: RouteRequest): Boolean {
        val uri = request.uri
        if (setOf("http", "https").contains(uri.scheme) && (domains.isEmpty() || domains.contains(uri.host))) {
            return Router.browse(request.context, uri)
        }
        if (schemes.isEmpty() || schemes.contains(uri.scheme)) {
            return Router.browse(request.context, uri)
        }
        return false
    }
}