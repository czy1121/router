package me.reezy.router.interceptor

import android.os.Bundle
import me.reezy.router.RouteRequest
import me.reezy.router.RouteInterceptor
import me.reezy.router.routeTo
import me.reezy.router.schemesHttp

class WebViewInterceptor(private val route: String, private val domains: Set<String>) : RouteInterceptor {

    override fun intercept(request: RouteRequest): Boolean {
        val uri = request.uri
        if (!schemesHttp.contains(uri.scheme)) return false
        for (domain in domains) {
            if (uri.host == domain) {
                return request.context.routeTo(route) {
                    val bundle = Bundle()
                    bundle.putString("url", uri.toString())
                    params(bundle)
                }
            }
        }
        return false
    }
}