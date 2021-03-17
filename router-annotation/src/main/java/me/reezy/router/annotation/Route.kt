package me.reezy.router.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class Route(val value:String, val routes: Array<String> = [], val interceptors:Array<String> = [], val flags: Int = 0)