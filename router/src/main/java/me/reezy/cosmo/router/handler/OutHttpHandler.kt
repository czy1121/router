package me.reezy.cosmo.router.handler

import androidx.core.net.toUri
import me.reezy.cosmo.router.RouteHandler
import me.reezy.cosmo.router.RouteRequest
import me.reezy.cosmo.router.Router

class OutHttpHandler : RouteHandler {
    override fun handle(request: RouteRequest): Boolean {
        val uri = request.uri
        if (setOf("out-http", "out-https").contains(uri.scheme)) {
            return Router.browse(request.context, uri.toString().substring(4).toUri())
        }
        return false
    }
}