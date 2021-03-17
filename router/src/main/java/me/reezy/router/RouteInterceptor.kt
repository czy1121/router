package me.reezy.router


interface RouteInterceptor {
    fun intercept(request: RouteRequest): Boolean
}