package me.reezy.cosmo.router


interface RouteForwarder {
    fun forward(request: RouteRequest): Boolean
}