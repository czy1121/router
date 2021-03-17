package me.reezy.demo.router

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.layout_demo.*
import me.reezy.jetpack.argument.ArgumentBoolean
import me.reezy.jetpack.argument.ArgumentInt
import me.reezy.jetpack.argument.ArgumentString
import me.reezy.router.annotation.Route

@Route("argument", routes = ["argument/:a/:b"])
class ArgumentActivity : AppCompatActivity(R.layout.layout_demo) {
  private val a by ArgumentString()

  // 如果不能转换成Int，返回默认值，能转则返回转换后的Int
  private val b by ArgumentInt()


  private val c by ArgumentBoolean(false)


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    text.text = "ArgumentActivity\n a = $a, b = $b, c = $c"
  }
}