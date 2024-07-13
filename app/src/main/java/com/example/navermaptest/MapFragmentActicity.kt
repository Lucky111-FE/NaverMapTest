package com.example.navermaptest

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Xml
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.fragment.app.FragmentActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.PolylineOverlay
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

private const val TAG = "MapFragmentActicity_싸피"
class MapFragmentActivity : FragmentActivity(), OnMapReadyCallback {
    private var gpxCoordinates: List<LatLng>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.map_fragment_activity)

        // GPX 데이터 파싱
        gpxCoordinates = parseGPX();

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

        mapFragment.getMapAsync(this)
    }

    private fun parseGPX(): List<LatLng> {
        val coordinates: MutableList<LatLng> = ArrayList()
        try {
            val inputStream = assets.open("팔공산_0000000001.gpx")
            val parser = Xml.newPullParser()
            parser.setInput(inputStream, null)
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "trkpt") {
                    val lat = parser.getAttributeValue(null, "lat").toDouble()
                    val lon = parser.getAttributeValue(null, "lon").toDouble()
                    coordinates.add(LatLng(lat, lon))
                }
                eventType = parser.next()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        }
        return coordinates
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        if (gpxCoordinates != null && gpxCoordinates!!.isNotEmpty()) {
            Log.d(TAG, "onMapReady: polygon 호출")
            val polygon1 = PolylineOverlay()
            val polygon2 = PolylineOverlay()
            Log.d(TAG, "onMapReady: $gpxCoordinates")
            polygon1.coords = gpxCoordinates!!
            polygon1.color = Color.RED
            polygon1.map = naverMap
        }
        Toast.makeText(this, "onMapReady 호출", Toast.LENGTH_SHORT).show()
    }
}