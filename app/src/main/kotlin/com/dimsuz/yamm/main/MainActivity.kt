package com.dimsuz.yamm.main

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dimsuz.yamm.R
import com.dimsuz.yamm.login.LoginActivity
import com.dimsuz.yamm.util.AppConfig
import com.dimsuz.yamm.util.appScope
import com.dimsuz.yamm.util.instance

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val appConfig = appScope.instance<AppConfig>()
    if (appConfig.getServerUrl() == null) {
      val intent = Intent(this, LoginActivity::class.java)
      startActivity(intent)
      finish()
      return
    }
    setContentView(R.layout.activity_content_main)
  }
}