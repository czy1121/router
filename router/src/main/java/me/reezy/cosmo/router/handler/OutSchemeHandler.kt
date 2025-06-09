package me.reezy.cosmo.router.handler

import me.reezy.cosmo.router.RouteHandler
import me.reezy.cosmo.router.RouteRequest
import me.reezy.cosmo.router.Router

class OutSchemeHandler(private vararg val schemes: String) : RouteHandler {
    override fun handle(request: RouteRequest): Boolean {
        val scheme = request.uri.scheme
        if (scheme.isNullOrEmpty() || schemes.isEmpty()) {
            return false
        }
        if (schemes.contains(scheme)) {
            return Router.browse(request.context, request.uri)
        }
        return false
    }
}