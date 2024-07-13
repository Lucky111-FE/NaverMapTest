package com.example.navermaptest

import android.os.Bundle
import android.util.Log
import android.util.Xml
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback
import com.google.android.gms.maps.StreetViewPanorama
import com.google.android.gms.maps.model.LatLng
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

private const val TAG = "GooglemapPanoramaFragmentActivity_싸피"
class GooglemapPanoramaFragmentActivity : FragmentActivity(), OnStreetViewPanoramaReadyCallback {
    private var gpxCoordinates: List<LatLng>? = null
    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_googlemap_panorama_fragment)

        val fm = supportFragmentManager

        // GPX 데이터 파싱
        gpxCoordinates = parseGPX()
    }

    private fun parseGPX(): List<LatLng> {
        val coordinates: MutableList<LatLng> = ArrayList()
        try {
            // assets에 있는 팔공산 gpx 데이터 가져옴
            val inputStream = assets.open("팔공산_0000000001.gpx")
            // gpx내의 xml parsing을 위한 parser 호출
            val parser = Xml.newPullParser()
            // gpx 데이터를 parser에 삽입, parser로 gpx 데이터를 순차적으로 읽는 것
            parser.setInput(inputStream, null)
            // 여기서부터 대충 데이터의 시작과 끝을 읽으면서 경도, 위도를 읽어서 각 데이터 별로 LatLng라는 좌표 객체를 만든 후 리스트에 삽입
            var eventType = parser.eventType
            // 끝까지 읽기 전까지 반복적으로 읽으면서 좌표 추가하는 행위를 반복하는 코드
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "trkpt") {
                    val lat = parser.getAttributeValue(null, "lat").toDouble()
                    val lon = parser.getAttributeValue(null, "lon").toDouble()
                    coordinates.add(LatLng(lat, lon))
                }
                // 다음 거 읽기
                eventType = parser.next()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        }
        // LatLng 좌표 객체들이 저장된 리스트 반환
        return coordinates
    }

    private fun setLocation(currentLatLng: LatLng) {
        Log.d(TAG, "setLocation: 카메라 업데이트")
//        val currentLatLng = LatLng(location.latitude, location.longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f)
        mMap!!.animateCamera(cameraUpdate)
    }

    override fun onStreetViewPanoramaReady(p0: StreetViewPanorama) {
        p0.setPosition(gpxCoordinates!![0])
    }
}