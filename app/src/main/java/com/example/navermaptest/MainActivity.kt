package com.example.navermaptest

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)


    startActivity(Intent(this, GooglemapPanoramaFragmentActivity::class.java))

    // google 일반지도 호출하는 코드
//    startActivity(Intent(this, GooglemapFragmentActivity::class.java))
    // naver지도 호출하는 activity로 넘어가는 코드
//    startActivity(Intent(this, MapFragmentActivity::class.java))
  }
}