package me.reezy.demo.router

import android.widget.Toast
import me.reezy.router.RouteRequest
import me.reezy.router.RouteCall

import me.reezy.router.annotation.Route

@Route("call/demo")
class CallDemo : RouteCall {
    override fun call(request: RouteRequest) {
        Toast.makeText(request.context, "this is call demo", Toast.LENGTH_LONG).show()
    }
}
