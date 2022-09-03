# Router 
 
一个 kotlin + ksp 实现的路由库，通过 uri 实现页面间跳转

支持：

- 相对链接 - "path/page?a=1"
- 指定协议 - "yourscheme://path/page?a=1"
- 通过 uri 在WebView打开网页
- 通过 uri 打开外部应用


此库专注于页面间跳转，页面参数解析由另外的库 (https://github.com/czy1121/argument) 负责，不包含 **依赖注入/Fragment获取/模块间通信** 之类的不相关功能。

## 引入

``` groovy
repositories {
    maven { url "https://gitee.com/ezy/repo/raw/cosmo/"}
}
dependencies {
    implementation "me.reezy.cosmo:router:0.7.0"
    ksp "me.reezy.cosmo:router-ksp:0.7.0"
}
```

模块配置

```groovy
apply plugin: 'com.google.devtools.ksp'


ksp {
    arg("router.moduleName", "app")
    arg("router.packageName", "YOUR_APPLICATION_ID")
}
```

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

默认支持使用相对链接打开页面

```kotlin
routeTo("hello/world")
```

**指定协议(scheme)**

注册协议后可通过协议链接打开指定页面

```kotlin
// 注册协议
Router.addSchemes("app", "myscheme")

routeTo("app://hello/world")
routeTo("myscheme://hello/world")
```

## 路由转发

非应用内路由通过添加转发器处理，可实现多种功能

- 通过Uri在WebView打开网页
- 通过Uri打开外部应用


**路由到WebView打开网页**

将网页链接(http/https)转发到某个实现了WebView的页面

```kotlin
// "https://juejin.cn" 转发到 "web?url=https://juejin.cn"
Router.addForwarder(WebViewForwarder("web", setOf("juejin.cn", "localhost")))


// 在 WebviewActivity 中打开该链接
routeTo("https://juejin.cn/user/3386151541932887")
```


**打开外部应用**

```kotlin
// domains/schemes 为白名单，为空时表示全部通过
// domains 非空时，域名在名单内的 http/https 链接通过外部应用打开
// domains 为空时，所有的 http/https 链接都通过外部应用打开
// schemes 非空时，协议在名单内的链接通过外部应用打开
// schemes 为空表，所有的链接都通过外部应用打开
Router.addForwarder(OutgoingForwarder(
    domains = setOf("developer.android.com"),
    schemes = setOf("weixin"),
))


// 唤起微信
routeTo("weixin://dl/business?ticket=x")
// 在外部浏览器打开外链网页
routeTo("https://developer.android.com/kotlin")
```


## 通过路由执行代码

路由除了可以打开页面，还可以通过实现 `RouteCallable` 接口直接执行代码

```kotlin
// 注册路由
@Route("callable/demo")
class CallableDemo : RouteCallable {
    override fun call(request: RouteRequest) {
        Toast.makeText(request.context, "this is callable demo", Toast.LENGTH_LONG).show()
    }
}


// 通过路由执行代码
routeTo("callable/demo")
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


## LICENSE

The Component is open-sourced software licensed under the [Apache license](LICENSE).

