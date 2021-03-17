package me.reezy.router

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class RouteActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        intent.data?.let {
            routeTo(it)
        }
        finish()
    }
}