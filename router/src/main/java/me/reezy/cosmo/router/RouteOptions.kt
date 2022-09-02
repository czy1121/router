package me.reezy.cosmo.router

import android.os.Bundle

class RouteOptions {
    internal var requestCode: Int = 0
    internal var enterAnim: Int = 0
    internal var exitAnim: Int = 0
    internal var activityOptions: Bundle? = null

    fun requestCode(code: Int): RouteOptions {
        this.requestCode = code
        return this
    }

    fun transition(enter: Int = 0, exit: Int = 0): RouteOptions {
        enterAnim = enter
        exitAnim = exit
        return this
    }

    fun activityOptions(bundle: Bundle): RouteOptions {
        activityOptions = bundle
        return this
    }
}