package com.dimsuz.yamm.main

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dimsuz.yamm.R
import com.dimsuz.yamm.login.LoginActivity

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val isLoggedIn = false // TODO
    if (!isLoggedIn) {
      val intent = Intent(this, LoginActivity::class.java)
      startActivity(intent)
      finish()
      return
    }
    setContentView(R.layout.activity_content_main)
  }
}