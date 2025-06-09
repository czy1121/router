package me.reezy.cosmo.router.handler

import me.reezy.cosmo.router.RouteHandler
import me.reezy.cosmo.router.RouteRequest
import me.reezy.cosmo.router.Router

class OutHostHandler(private vararg val hosts: String) : RouteHandler {
    override fun handle(request: RouteRequest): Boolean {
        val uri = request.uri
        if (setOf("http", "https").contains(uri.scheme) && hosts.contains(uri.host)) {
            return Router.browse(request.context, uri)
        }
        return false
    }
}