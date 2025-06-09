package com.demo.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import me.reezy.cosmo.router.annotation.Route
import me.reezy.cosmo.utility.delegate.extraBoolean
import me.reezy.cosmo.utility.delegate.extraInt
import me.reezy.cosmo.utility.delegate.extraString

@Route("argument", routes = ["argument/:a/:b"])
class ArgumentActivity : AppCompatActivity(R.layout.layout_demo) {
  private val a by extraString()

  // 如果不能转换成Int，返回默认值，能转则返回转换后的Int
  private val b by extraInt()


  private val c by extraBoolean(false)


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    findViewById<TextView>(R.id.text).text = "ArgumentActivity\n a = $a, b = $b, c = $c"
  }
}