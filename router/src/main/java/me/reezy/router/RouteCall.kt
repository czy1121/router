package me.reezy.router


interface RouteCall {
    fun call(request: RouteRequest)
}