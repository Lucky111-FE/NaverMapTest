package com.example.navermaptest

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Xml
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException


private const val TAG = "GooglemapFragmentActivity_싸피"
class GooglemapFragmentActivity : FragmentActivity(), OnMapReadyCallback{
    private var gpxCoordinates: List<LatLng>? = null
    private var mMap: GoogleMap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_googlemap_fragment)

        val fm = supportFragmentManager

        // GPX 데이터 파싱
        gpxCoordinates = parseGPX()

        val mapFragment = fm.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
//        setLocation(gpxCoordinates!![0])

        Log.d(TAG, "onCreate: $gpxCoordinates")
        

//        fm.findFragmentById(R.id.map
//        )
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

    override fun onMapReady(p0: GoogleMap) {
        Log.d(TAG, "onMapReady: 테스트")
        mMap = p0
        val initLng = gpxCoordinates?.get(0)
        setLocation(initLng!!)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(initLng, 15f)
        mMap!!.animateCamera(cameraUpdate)
        Log.d(TAG, "onMapReady: $gpxCoordinates")
        if (gpxCoordinates != null && gpxCoordinates!!.isNotEmpty()) {
            Log.d(TAG, "onMapReady: polygon 호출")

            val polyOptions1 = PolylineOptions()
            val polyOptionsOutline1 = PolylineOptions()
            val polyOptions2 = PolylineOptions()
            val polyOptionsOutline2 = PolylineOptions()

            // 절반으로 나누기 위해 LagLng 데이터들을 저장한 리스트들의 사이즈의 중간 값을 저장할 midIndex 객체 생성
            val midIndex = gpxCoordinates!!.size / 2
            // 인덱스를 받아서 새로운 리스트를 만드는 subList 함수로 0 ~ 중간까지 인덱스를 지정해서 앞부분 절반에 해당하는 새로운 리스트 생성
            val firstHalf = gpxCoordinates!!.subList(0, midIndex)
            // 인덱스를 받아서 새로운 리스트를 만드는 subList 함수로 중간 ~ 끝까지 인덱스를 지정해서 뒷부분 절반에 해당하는 새로운 리스트 생성
            val secondHalf = gpxCoordinates!!.subList(midIndex, gpxCoordinates!!.size)

            polyOptions1.addAll(firstHalf)
            polyOptions1.color(Color.RED)
            polyOptions1.width(30F)

            polyOptionsOutline1.addAll(firstHalf)
            polyOptionsOutline1.color(Color.WHITE)
            polyOptionsOutline1.width(55F)


            polyOptions2.addAll(secondHalf)
            polyOptions2.color(Color.BLUE)
            polyOptions2.width(30F)

            polyOptionsOutline2.addAll(secondHalf)
            polyOptionsOutline2.color(Color.WHITE)
            polyOptionsOutline2.width(55F)

            mMap!!.addPolyline(polyOptionsOutline1)
            mMap!!.addPolyline(polyOptionsOutline2)

            mMap!!.addPolyline(polyOptions1)
            mMap!!.addPolyline(polyOptions2)


//
//            // 지금은 임의로 절반씩 쪼개서 각자 색이 다른 별도의 PolyLine을 그려줘야 하므로 Polyline을 그려주는 PolylineOverlay 객체를 생성
//            // polygon1은 0~사이즈의 절반의 LatLng좌표을 담당하는 Polyline
//            val polygon1 = PolylineOverlay()
//            // polygonOutline1은 polygon1의 Outline 경로의 테두리를 그리기 위해 두꼐를 조금 더 두껍게 하고, 테두리로 색깔을 다른 것으로 설정해서 그리기
//            val polygonOutline1 = PolylineOverlay()
//            // polygon2는 사이즈의 절반부터 끝까지의 LatLng 좌표를 담당하는 Polyline
//            val polygon2 = PolylineOverlay()
//            // polygonOutline2은 polygon2의 Outline 경로의 테두리를 그리기 위해 두꼐를 조금 더 두껍게 하고, 테두리로 그리기 위해 색깔을 다른 것으로 설정해서 그리기
//            val polygonOutline2 = PolylineOverlay()
//
//            Log.d(TAG, "onMapReady: $gpxCoordinates")
//
//            // 절반으로 나누기 위해 LagLng 데이터들을 저장한 리스트들의 사이즈의 중간 값을 저장할 midIndex 객체 생성
//            val midIndex = gpxCoordinates!!.size / 2
//            // 인덱스를 받아서 새로운 리스트를 만드는 subList 함수로 0 ~ 중간까지 인덱스를 지정해서 앞부분 절반에 해당하는 새로운 리스트 생성
//            val firstHalf = gpxCoordinates!!.subList(0, midIndex)
//            // 인덱스를 받아서 새로운 리스트를 만드는 subList 함수로 중간 ~ 끝까지 인덱스를 지정해서 뒷부분 절반에 해당하는 새로운 리스트 생성
//            val secondHalf = gpxCoordinates!!.subList(midIndex, gpxCoordinates!!.size)
//
//            // 라인의 테두리를 그리기 위한 PolyLine으로 coords 좌표는 polygon1과 동일하게 설정하고, 색깔을 다르게, 테두리로 그리기 위해 두께는 조금 더 두껍게 설정
//            polygonOutline1.coords = firstHalf
//            polygonOutline1.color = Color.WHITE
//            polygonOutline1.width = 80
//
//            // 앞부분에 해당하는 좌표들을 기반으로 PolyLine을 그릴 것
//            polygon1.coords = firstHalf
//            // PolyLine의 색깔은 빨간색으로 지정
//            polygon1.color = Color.RED
//            // Polygon1의 두께를 30으로 설정, width Default는 5
//            polygon1.width = 30
//            // polygonOutline1의 좌표에 해당하는 Polygon Line을 Navermap에 적용
//            polygonOutline1.map = naverMap
//
//            // polygon1의 좌표에 해당하는 Polygon Line을 Navermap에 적용
//            // ★주의할 점 : 두꺼운걸 나중에 그려버리면 덮어씌워지는 꼴이기 때문에, 두꺼운 걸 먼저 그리고, 얇은 걸 나중에 그려야 함★
//            polygon1.map = naverMap
//
//            // 라인의 테두리를 그리기 위한 PolyLine으로 coords 좌표는 polygon2과 동일하게 설정하고, 색깔을 다르게, 테두리로 그리기 위해 두께는 조금 더 두껍게 설정
//            polygonOutline2.coords = secondHalf
//            polygonOutline2.color = Color.WHITE
//            polygonOutline2.width = 70
//
//            // 뒷부분에 해당하는 좌표들을 기반으로 PolyLine을 그릴 것
//            polygon2.coords = secondHalf
//            // PolyLine의 색깔은 파란색으로 지정
//            polygon2.color = Color.BLUE
//            // Polygon1의 두께를 30으로 설정, width Default는 5
//            polygon2.width = 30
//
//            // polygonOutline2의 좌표에 해당하는 Polygon Line을 Navermap에 적용
//            polygonOutline2.map = naverMap
//            // polygon2의 좌표에 해당하는 Polygon Line을 Navermap에 적용
//            // ★주의할 점 : 두꺼운걸 나중에 그려버리면 덮어씌워지는 꼴이기 때문에, 두꺼운 걸 먼저 그리고, 얇은 걸 나중에 그려야 함★
//            polygon2.map = naverMap
//


        }
        Toast.makeText(this, "onMapReady 호출", Toast.LENGTH_SHORT).show()
    }
}