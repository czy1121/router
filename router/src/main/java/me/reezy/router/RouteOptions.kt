package me.reezy.router

import android.content.Intent
import android.os.Bundle

class RouteOptions {

    internal var params: Bundle = Bundle()
    internal var intent: Intent? = null

    internal var requestCode: Int = 0
    internal var enterAnim: Int = 0
    internal var exitAnim: Int = 0
    internal var activityOptions: Bundle? = null

    fun params(bundle: Bundle): RouteOptions {
        this.params.putAll(bundle)
        return this
    }

    fun intent(block: Intent.() -> Unit): RouteOptions {
        if (intent == null) {
            intent = Intent()
        }
        intent?.apply(block)
        return this
    }

    fun flags(flags: Int): RouteOptions {
        if (intent == null) {
            intent = Intent()
        }
        intent?.flags = flags
        return this
    }

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