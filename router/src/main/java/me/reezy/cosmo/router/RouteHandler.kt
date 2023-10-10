package me.reezy.cosmo.router


interface RouteHandler {
    fun handle(request: RouteRequest): Boolean
}