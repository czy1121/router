package com.demo.app

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import me.reezy.cosmo.ArgumentString
import me.reezy.cosmo.router.Router
import me.reezy.cosmo.router.annotation.Route
import me.reezy.cosmo.router.routeTo

@Route("web")
class WebViewActivity : AppCompatActivity(R.layout.layout_web) {

    // 获取url参数
    private val url by ArgumentString()

    private val web by lazy { findViewById<WebView>(R.id.web) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                when {
                    url.startsWith("weixin://") || url.startsWith("app://") -> {
                        Router.routeTo(view.context, request.url)
                        return true
                    }
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }


        initWebSettings(web)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(web, true)
        }

        web.addJavascriptInterface(JSInterface(this), "host")

        web.requestFocus()
        web.loadUrl(url.replace("http://localhost/", "file:///android_asset/"))
    }

    class JSInterface(private val context: Context) {

        @JavascriptInterface
        fun routeTo(url: String?) {
            url?.let {
                context.routeTo(it)
            }
        }
    }

    private fun initWebSettings(web: WebView) {
        val settings = web.settings
        // 缓存(cache)
        settings.setAppCacheEnabled(false)
        settings.setAppCachePath(web.context.cacheDir.absolutePath)

        // 存储(storage)
        settings.domStorageEnabled = true
        settings.databaseEnabled = true

        // 定位(location)
        settings.setGeolocationEnabled(true)

        // 缩放(zoom)
        settings.setSupportZoom(true)
        settings.builtInZoomControls = false
        settings.displayZoomControls = false

        // 文件权限
        settings.allowContentAccess = true
        settings.allowFileAccess = true
        settings.allowFileAccessFromFileURLs = false
        settings.allowUniversalAccessFromFileURLs = false

        //
        settings.textZoom = 100

        // 支持Javascript
        settings.javaScriptEnabled = true

        // 支持https
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        // 页面自适应手机屏幕，支持viewport属性
        settings.useWideViewPort = true
        // 缩放页面，使页面宽度等于WebView宽度
        settings.loadWithOverviewMode = true

        // 是否自动加载图片
        settings.loadsImagesAutomatically = true
        // 禁止加载网络图片
        settings.blockNetworkImage = false
        // 禁止加载所有网络资源
        settings.blockNetworkLoads = false

        // deprecated
        settings.saveFormData = true
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        settings.databasePath = web.context.getDir("database", Context.MODE_PRIVATE).path
        settings.setGeolocationDatabasePath(web.context.filesDir.path)
    }

    override fun onDestroy() {
        web.removeJavascriptInterface("host")
        web.stopLoading()
        web.clearHistory()
        web.removeAllViews()
        web.destroy()
        val parent = web.parent
        if (parent is ViewGroup) {
            parent.removeView(web)
        }
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (web.canGoBack()) {
            web.goBack()
        } else {
            super.onBackPressed()
        }
    }
}