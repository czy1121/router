package me.reezy.cosmo.router

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment



inline fun Context.routeTo(uri: Uri, params: Bundle = Bundle(), intent: Intent? = null, crossinline block: RouteOptions.() -> Unit = {}) =
    Router.routeTo(this, uri, params, intent, RouteOptions().apply(block))
inline fun Context.routeTo(uri: String, params: Bundle = Bundle(), intent: Intent? = null, crossinline block: RouteOptions.() -> Unit = {}) =
    Router.routeTo(this, Uri.parse(uri), params, intent, RouteOptions().apply(block))

inline fun Fragment.routeTo(uri: Uri, params: Bundle = Bundle(), intent: Intent? = null, crossinline block: RouteOptions.() -> Unit = {}) =
    Router.routeTo(this.requireActivity(), uri, params, intent, RouteOptions().apply(block))
inline fun Fragment.routeTo(uri: String, params: Bundle = Bundle(), intent: Intent? = null, crossinline block: RouteOptions.() -> Unit = {}) =
    Router.routeTo(this.requireActivity(), Uri.parse(uri), params, intent, RouteOptions().apply(block))



internal fun Context.meta(key: String): String? {
    try {
        return packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA).metaData?.getString(key)
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return null
}