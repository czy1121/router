package me.reezy.router

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.fragment.app.Fragment

internal const val PKG = "me.reezy.router"

internal val schemesHttp = setOf("http", "https")


inline fun Context.routeTo(uri: Uri, crossinline block: RouteOptions.() -> Unit = {}) = Router.routeTo(this, uri, RouteOptions().apply(block))
inline fun Context.routeTo(uri: String, crossinline block: RouteOptions.() -> Unit = {}) = Router.routeTo(this, Uri.parse(uri), RouteOptions().apply(block))

inline fun Fragment.routeTo(uri: Uri, crossinline block: RouteOptions.() -> Unit = {}) = Router.routeTo(this.requireActivity(), uri, RouteOptions().apply(block))
inline fun Fragment.routeTo(uri: String, crossinline block: RouteOptions.() -> Unit = {}) = Router.routeTo(this.requireActivity(), Uri.parse(uri), RouteOptions().apply(block))



internal fun Context.meta(key: String): String? {
    try {
        return packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA).metaData?.getString(key)
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return null
}