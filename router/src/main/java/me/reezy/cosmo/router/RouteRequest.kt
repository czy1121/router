package me.reezy.cosmo.router

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle


class RouteRequest(
    val context: Context,
    val uri: Uri,
    val params: Bundle,
    val intent: Intent?,

    val requestCode: Int,
    val enterAnim: Int,
    val exitAnim: Int,
    val activityOptions: Bundle?,
) {
    fun getParam(key: String): String? = uri.getQueryParameter(key) ?: kotlin.run {
        when (val value = params.get(key)) {
            is String -> value
            is Number, Boolean -> value.toString()
            else -> null
        }
    }
}