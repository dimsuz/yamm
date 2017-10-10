package com.dimsuz.yamm.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.dimsuz.yamm.R
import com.dimsuz.yamm.network.NetworkModule

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_content_main)
    Log.d("Hello", "${NetworkModule()}")
  }
}