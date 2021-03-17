package me.reezy.demo.router

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.layout_demo.*
import me.reezy.router.annotation.Route

@Route("hello/world")
class HelloWorldActivity : AppCompatActivity(R.layout.layout_demo) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        text.text = "HelloWorldActivity"
    }

}