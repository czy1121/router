package com.demo.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import me.reezy.cosmo.router.annotation.Route

@Route("hello/world")
class HelloWorldActivity : AppCompatActivity(R.layout.layout_demo) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<TextView>(R.id.text).text = "HelloWorldActivity"
    }

}