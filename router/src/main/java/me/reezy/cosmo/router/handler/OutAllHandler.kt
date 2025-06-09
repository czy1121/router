package me.reezy.cosmo.router.handler

import me.reezy.cosmo.router.RouteHandler
import me.reezy.cosmo.router.RouteRequest
import me.reezy.cosmo.router.Router

class OutAllHandler() : RouteHandler {
    override fun handle(request: RouteRequest): Boolean {
        return Router.browse(request.context, request.uri)
    }
}