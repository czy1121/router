package me.reezy.demo.router

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.reezy.router.Router
import me.reezy.router.interceptor.WebViewInterceptor
import me.reezy.router.routeTo

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        Router.init(this)
        Router.addSchemesIn("app", "router")
        Router.addHttpDomainsIn("demo.reezy.me", "m.demo.reezy.me")
        Router.addSchemesOut("weixin")
        Router.addHttpDomainsOut("developer.android.com")

        Router.addInterceptor(WebViewInterceptor("webview", setOf("juejin.cn", "localhost")))


        // 打开 HelloWorldActivity
        page_a.linkTo("hello/world")
        page_b.linkTo("app://hello/world")
        page_c.linkTo("router://hello/world")
        page_d.linkTo("https://demo.reezy.me/hello/world")

        // 在 WebviewActivity 中打开该链接
        web_a.linkTo("https://juejin.cn/user/3386151541932887")
        web_b.linkTo("http://localhost/demo.html")

        // 在应用内通过此链接可以打开 PostActivity
        post_a.linkTo("https://m.demo.reezy.me/post?id=123456")
        post_c.linkTo("https://m.demo.reezy.me/post/222222")
        post_b.linkTo("router://post?id=111111")

        // 唤起微信
        browse_a.linkTo("weixin://dl/business?ticket=x")
        // 在外部浏览器打开外链网页
        browse_b.linkTo("https://developer.android.com/kotlin")



        // 通过路由执行代码
        call.linkTo("call/demo")


        argument_a.linkTo("argument?a=hoho&b=22222")
        argument_b.linkTo("argument/haha/33333?c=true")


    }

    private fun View.linkTo(url: String) {
        setOnClickListener {
            it.context.routeTo(url)
        }
    }
}