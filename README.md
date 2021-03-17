# Router 
 
这是一个Kotlin实现的Anroid路由库，提供一种统一的方式(Uri)帮助我们到达应用的各处。

给定一个Uri，路由会决定到达哪里：

- **应用内路由**
  - 相对链接 
  - 指定协议(scheme)
  - 指定域名(domain)
  - 路由到WebView
  - 在WebView中使用路由
- **从外部路由到应用内**  
  - 网页链接(http/https)
  - 自定义协议(scheme)
- **路由到外部应用** 
  - 打开外部应用 
  - 打开外部网页
- **通过路由执行代码**
  

项目地址：https://github.com/czy1121/router

引入
```groovy
repositories { 
    maven { url "https://gitee.com/ezy/repo/raw/android_public/"}
} 
dependencies {
    implementation "me.reezy.router:router:0.9.0" 
    kapt "me.reezy.router:router-compiler:0.9.0"  
}
```

相比 WMRouter/ARouter，此库专注于路由，页面参数解析由另外的库(https://github.com/czy1121/argument)负责，不包含 **依赖注入/Fragment获取/模块间通信** 之类的不相关功能。

## 基础用法

注册路由

```kotlin 
@Route("hello/world")
class HelloWorldActivity : AppCompatActivity() {  
}
```

在 AndroidManifest.xml 的 <application> 里添加模块

```kotlin 
<meta-data android:name="modules" android:value="app" />
```

初始化，根据指定的模块收集注册的路由

```kotlin
Router.init(context) 
```

跳转，在 `Activity/Fragment` 中可以直接使用 `routeTo` 方法

```kotlin
routeTo("hello/world")
```

指定选项

```kotlin
routeTo("hello/world?c=3") {
    // 传入参数
    params(bundleOf("a" to "1", "b" to "2"))
    // 转场动画
    transition(enter, exit)
    // startActivityForResult
    requestCode(0x1234)
    // 启动标志
    flags(Intent.FLAG_ACTIVITY_NEW_TASK)
    // ActivityOptions
    activityOptions(ActivityOptionsCompat.makeBasic().toBundle()!!)
    // intent
    intent { 
        // 
    }
}
```

`@Route`

```kotlin
// 不知道是哪的问题，@Repeatable 没用，一个类无法定义多个 @Route
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class Route(
  // 路由值，支持路径参数
  val value:String, 
  // 多个路由值，支持路径参数
  val routes: Array<String> = [], 
  // 拦截器名称
  val interceptors:Array<String> = [], 
  // 启动标记
  val flags: Int = 0
) 
```

## 路由机制


其实就是个**路由映射表**(route -> activity) 与 **正则匹配列表**(regex -> activity)。

- 通过编译时注解`@Route`定义路由
- 通过APT收集定义的路由并生成模块的路由加载类：`RouteLoader_${moduleName}`
- 路由初始化里根据模块调用`RouteLoader_${moduleName}.load`将路由加载到路由表
- 加载路由时，如果路径不包含参数，添加到路由映射表，如果包含路径参数，添加到正则匹配列表
- 通过 `routeTo` 方法查找Uri对应的目标路由并处理
- 先查找路由映射表，如果找不到，则通过正则列表依次匹配，如果还没有则返回false



## 应用内路由  

**相对链接**

通常使用相对路径就可以打开指定页面

```kotlin
routeTo("hello/world")
```

**指定协议(scheme)**

注册协议后可通过协议链接打开指定页面

```kotlin
// 注册协议
Router.addSchemesIn("app", "router")

routeTo("app://hello/world")
routeTo("router://hello/world")
```

**指定域名(domain)**

注册域名后可通过http链接打开指定页面

```kotlin
// 注册域名
Router.addHttpDomainsIn("demo.reezy.me")

routeTo("https://demo.reezy.me/hello/world") 
```

**路由到WebView**

通过注册拦截器 `WebViewInterceptor` 可以通过http链接直接打开对应的WebView 

```kotlin
// 注册Webview路由
@Route("webview")
class WebviewActivity : AppCompatActivity() { 
    // 获取url参数
    val url by ArgumentString() 
}

// 注册拦截器
Router.addInterceptor(WebViewInterceptor("webview", setOf("juejin.cn")))

// 在 WebviewActivity 中打开该链接
routeTo("https://juejin.cn/user/3386151541932887") 
```
 
**在WebView中使用路由**

通常可注入JS方法 routeTo，这样在WebView中可以打开原生页面

```kotlin 
@JavascriptInterface
fun routeTo(url: String?) {
    url?.let {
        context.routeTo(it)
    }
}
``` 


## 从外部路由到应用内 

除了在应用内路由，我们还经常需要从外部打开指定页面 

**网页链接(http/https)**

注册网页深度链接 

```xml 
<activity android:name="me.reezy.router.RouteActivity">
    <intent-filter>
        <!-- 为了安全外部访问需要受限 -->
        <!-- 限制了只有 https://m.demo.reezy.me/post 才能唤起 -->
        <data android:scheme="https" android:host="m.demo.reezy.me" android:path="/post" /> 
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
    </intent-filter>
</activity>
```

注册页面与域名

```kotlin  
// 注册路由
@Route("post")
class PostActivity : AppCompatActivity() {  
  // ...
}

// 注册域名
Router.addHttpDomainsIn("m.demo.reezy.me")

// 在应用内通过此链接可以打开 PostActivity
routeTo("https://m.demo.reezy.me/post?id=123456")
``` 

**自定义协议(scheme)**

注册自定义协议 

```xml 
<activity android:name="me.reezy.router.RouteActivity">
    <intent-filter> 
        <!-- 为了安全外部访问需要受限 -->
        <!-- 限制了只有 router://post 才能唤起 -->
        <data android:scheme="router" android:host="post"  /> 
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
    </intent-filter>
</activity>
```
 
注册页面 

```kotlin  
// 注册路由
@Route("post")
class PostActivity : AppCompatActivity() {  
  // ...
} 

// 在应用内通过此链接可以打开 PostActivity
routeTo("router://post?id=123456")
```
 

**在应用外唤起**

在外部应用中可通过Intent唤起

```kotlin 
val intent = Intent()
intent.action = Intent.ACTION_VIEW
intent.data = "router://post?id=123456" 
intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) 
context.startActivity(intent) 
```

如果要在应用外浏览器中打开链接，需要声明网站关联性

参考：https://developer.android.com/studio/write/app-link-indexing.html#associatesite 
 


 
## 路由到外部应用 - 外链

我们还需要通过路由打开外部应用

**打开外部应用**

必需先注册外链协议才能偿试唤起

```kotlin  
// 注册外部应用协议
Router.addSchemesOut("weixin")

// 唤起微信
routeTo("weixin://dl/business?ticket=x")
```

**打开外部网页**

必须注册外链网页域名才能在外部浏览器打开

```kotlin  
// 注册外链网页域名
Router.addHttpDomainsOut("developer.android.com")

// 在外部浏览器打开外链网页
routeTo("https://developer.android.com/kotlin")
``` 


## 通过路由执行代码

路由除了可以打开页面，还可以通过实现 `RouteCall` 接口直接执行代码

```kotlin  
// 注册路由
@Route("call/demo")
class DemoCall : RouteCall {
    override fun call(request: RouteRequest) {
        Toast.makeText(request.context, "this is demo", Toast.LENGTH_LONG).show()
    } 
}


// 通过路由执行代码
routeTo("call/demo")
```

## 路由参数

**传递参数**

有多种方式向目标页面传递参数

- 通过`RouteOptions.params`方法
  ```kotlin
  routeTo("hello/world") {
      // 可以传递 Parcelable 这种复杂参数
      params(bundleOf("a" to "1", "b" to "2"))
  }
  ```
- 通过`Uri`的查询参数
  ```kotlin
  routeTo("hello/world?a=1&b=2") 
  ```
- 通过`Uri`的路径参数
  ```kotlin
  @Route("waoh/:a/:b")
  class PathParamsActivity : AppCompatActivity() {  
    val a by ArgumentString()  
    val b by ArgumentString()  
  }
  
  // 跳转到 PathParamsActivity 页面 a = aaa2, b = bbb2
  routeTo("waoh/aaa1/bbb2") 
  ```

如果通过多种方式传递了同一个参数，只会有一个生效

优先级：查询参数>路径参数>选项参数

```kotlin   
// a = query
routeTo("waoh/path/bbb2?a=query") {
    params(bundleOf("a" to "1"))
} 
```


**获取参数**

传递了参数，在目标页面，还需要取出参数。

这里通过另一个库来实现此功能，利用委托属性可以方便地实现。   


项目地址：https://github.com/czy1121/argument

引入

```groovy
repositories { 
    maven { url "https://gitee.com/ezy/repo/raw/android_public/"}
} 
dependencies {
    implementation "me.reezy.jetpack:argument:0.94.0"  
}
```

使用

支持数据类型： `Int/Long/Float/String/Boolean/Parcelable`

获取时会偿试转换，转换失败时返回指定的默认值

```kotlin
@Route("argument", routes = ["argument/:a/:b"])
class ArgumentActivity : AppCompatActivity() {  
  val a by ArgumentString()  
  
  // 如果不能转换成Int，返回默认值，能转则返回转换后的Int
  val b by ArgumentInt()  
  
  
  val c by ArgumentBoolean(false)  
}
 
routeTo("argument?a=hoho&b=22222") 
routeTo("argument/haha/33333") 
```



## 拦截器

**全局拦截器**

全局拦截器会在路由匹配前依被调用，返回true表示已经拦截并不再有后续操作。 

`WebViewInterceptor` 是一个全局拦截器，根据链接中的域名打开相应的的WebView。

**命名拦截器**

可以添加一些命名拦截器，注册路由时指定拦截器名称可为它添加一个命名拦截器

比如登录拦截器，在跳转前会检查是否登录，未登录时跳转到登录页

```kotlin 
class LoginInterceptor : Interceptor {
    override fun intercept(request: Request): Boolean {
        if (!Session.isLogin()) {
            request.context.routeTo("login") {
                flags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            return true
        }
        return false
    }
}

// 注册命名拦截器
Router.addNamedInterceptor("login", LoginInterceptor())

// 注册路由，并指定命名拦截器
@Route("page/one", interceptors = ["login"])
class PageOneActivity : AppCompatActivity() {  
}

// 只有登录后才能到达 PageOneActivity
routeTo("page/one") 
```


 
 



 
 
 


## LICENSE

The Component is open-sourced software licensed under the [Apache license](LICENSE).
