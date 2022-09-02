package com.demo.app

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.demo.app.databinding.ActivityMainBinding
import me.reezy.cosmo.router.Router
import me.reezy.cosmo.router.forwarder.*
import me.reezy.cosmo.router.routeTo

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.bind(findViewById<ViewGroup>(android.R.id.content).getChildAt(0)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        Router.init(this)

        // 支持带scheme的路由 "app://hello/world",  "myscheme://hello/world"
        Router.addSchemes("app", "myscheme")

        // 在WebView打开网页 "https://juejin.cn" 转发到 "web?url=https://juejin.cn"
        Router.addForwarder(WebViewForwarder("web", setOf("juejin.cn", "localhost")))

        // 打开外部应用
        // domains/schemes 为白名单
        // domains 非空时，域名在名单内的 http/https 链接通过外部应用打开
        // domains 为空时，所有的 http/https 链接都通过外部应用打开
        // schemes 非空时，协议在名单内的链接通过外部应用打开
        // schemes 为空表，所有的链接都通过外部应用打开
        Router.addForwarder(OutgoingForwarder(
            domains = setOf("developer.android.com"),
            schemes = setOf("weixin"),
        ))

        // 打开 HelloWorldActivity
        binding.pageA.linkTo("hello/world")
        binding.pageB.linkTo("app://hello/world")

        // 在 WebviewActivity 中打开该链接
        binding.webA.linkTo("https://juejin.cn/user/3386151541932887")
        binding.webB.linkTo("http://localhost/demo.html")

        // 唤起微信
        binding.browseA.linkTo("weixin://dl/business?ticket=x")
        // 在外部浏览器打开外链网页
        binding.browseB.linkTo("https://developer.android.com/kotlin")



        // 通过路由执行代码
        binding.callable.linkTo("callable/demo")


        binding.argumentA.linkTo("argument?a=hoho&b=22222")
        binding.argumentB.linkTo("argument/haha/33333?c=true")


    }

    private fun View.linkTo(url: String) {
        setOnClickListener {
            it.context.routeTo(url)
        }
    }
}