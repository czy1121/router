package me.reezy.cosmo.router.internal


internal data class RouteRegex(val regex: Regex, val params: List<String>, val route: Route)