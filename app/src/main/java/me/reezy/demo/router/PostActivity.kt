package me.reezy.demo.router

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.layout_demo.*
import me.reezy.jetpack.argument.ArgumentInt
import me.reezy.router.annotation.Route

@Route("post", routes = ["post/:id"])
class PostActivity : AppCompatActivity(R.layout.layout_demo) {


    private val id by ArgumentInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        text.text = "PostActivity\n id = $id"
    }

}