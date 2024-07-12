package com.example.navermaptest

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.fragment.app.FragmentActivity
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback

class MapFragmentActivity : FragmentActivity(), OnMapReadyCallback {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.map_fragment_activity)

    val fm = supportFragmentManager
    val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
      ?: MapFragment.newInstance().also {
        fm.beginTransaction().add(R.id.map, it).commit()
      }

    mapFragment.getMapAsync(this)
  }

  @UiThread
  override fun onMapReady(naverMap: NaverMap) {
    Toast.makeText(this, "onMapReady 호출", Toast.LENGTH_SHORT).show()
  }
}