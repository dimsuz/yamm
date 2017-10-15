package com.dimsuz.yamm.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dimsuz.yamm.R
import com.dimsuz.yamm.YammApplication
import com.dimsuz.yamm.util.AppConfig
import com.dimsuz.yamm.util.appScope
import com.dimsuz.yamm.util.instance

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_content_main)
    val appConfig = appScope.instance<AppConfig>()
    appConfig.setServerUrl("https://mm.appkode.ru")
    (application as YammApplication).onServerUrlChanged()
  }
}