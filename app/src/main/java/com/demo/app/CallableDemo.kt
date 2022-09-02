package com.demo.app

import android.widget.Toast
import me.reezy.cosmo.router.RouteCallable
import me.reezy.cosmo.router.RouteRequest
import me.reezy.cosmo.router.annotation.Route

@Route("callable/demo")
class CallableDemo : RouteCallable {
    override fun call(request: RouteRequest) {
        Toast.makeText(request.context, "this is call demo", Toast.LENGTH_LONG).show()
    }
}
