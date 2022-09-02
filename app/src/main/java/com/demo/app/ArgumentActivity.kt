package com.demo.app

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import me.reezy.cosmo.ArgumentBoolean
import me.reezy.cosmo.ArgumentInt
import me.reezy.cosmo.ArgumentString
import me.reezy.cosmo.router.annotation.Route

@Route("argument", routes = ["argument/:a/:b"])
class ArgumentActivity : AppCompatActivity(R.layout.layout_demo) {
  private val a by ArgumentString()

  // 如果不能转换成Int，返回默认值，能转则返回转换后的Int
  private val b by ArgumentInt()


  private val c by ArgumentBoolean(false)


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    findViewById<TextView>(R.id.text).text = "ArgumentActivity\n a = $a, b = $b, c = $c"
  }
}