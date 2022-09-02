package me.reezy.cosmo.router


interface RouteInterceptor {
    fun intercept(request: RouteRequest): Boolean
}