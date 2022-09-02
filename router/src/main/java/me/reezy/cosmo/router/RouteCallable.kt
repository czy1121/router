package me.reezy.cosmo.router


interface RouteCallable {
    fun call(request: RouteRequest)
}