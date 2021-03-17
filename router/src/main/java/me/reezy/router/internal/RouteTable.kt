package me.reezy.router.internal

import android.util.Log
import me.reezy.router.PKG

class RouteTable {
    companion object {
        private val regexParamName = "/:\\w+".toRegex()
    }

    private val routes = mutableMapOf<String, Route>()
    private val matches = mutableListOf<RouteRegex>()


    fun add(clazz: Class<*>, paths: Array<String>, interceptors: Array<String>? = null, flags: Int = 0) {
        val interceptorsList = interceptors?.asList()

        paths.forEach { path ->
            if (!path.contains("/:")) {
                routes[path] = Route(path, clazz, interceptorsList, flags)
            } else if (regexParamName.containsMatchIn(path)) {
                val params = mutableListOf<String>()
                val regex = path.split("/").joinToString("/") {
                    if (it.startsWith(":")) {
                        params.add(it.substring(1))
                        "(\\w+)"
                    } else {
                        it
                    }
                }.toRegex()
                matches.add(RouteRegex(regex, params, Route(path, clazz, interceptorsList, flags)))
            } else {
                Log.w(PKG, "Route contains invalid character: $path.")
            }
        }
    }

    internal fun match(route: String): Pair<Route, Map<String, String>?>? {
        routes[route]?.let {
            return it to null
        }
        matches.forEach {
            it.regex.matchEntire(route)?.let { result ->
                val values = result.groupValues.subList(1, result.groupValues.size)
                return it.route to it.params.zip(values).toMap()
            }
        }
        return  null
    }


}