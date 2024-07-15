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
    private var checkPoly = false
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

    @UiThread
    // 네이버 지도에서 Map 로딩이 끝나면 반환되는 호출되는 콜백 함수(콜백함수란, 특정 조건이 만족되면 실행되는 함수, ex : 식후땡도 밥을 먹으면 ~를 한다의 콜백 함수의 일종이라고 할 수 있음)
    override fun onMapReady(naverMap: NaverMap) {
        // LatLng 좌표들이 저장되어 있을 gpxCoordinates 객체가 없거나, 비어 있지 않다면, 안의 LatLng 객체들을 바탕으로 PolyLine을 그릴 것임
        if (gpxCoordinates != null && gpxCoordinates!!.isNotEmpty()) {
            Log.d(TAG, "onMapReady: polygon 호출")

            // 지금은 임의로 절반씩 쪼개서 각자 색이 다른 별도의 PolyLine을 그려줘야 하므로 Polyline을 그려주는 PolylineOverlay 객체를 생성
            // polygon1은 0~사이즈의 절반의 LatLng좌표을 담당하는 Polyline
            val polygon1 = PolylineOverlay()
            // polygonOutline1은 polygon1의 Outline 경로의 테두리를 그리기 위해 두꼐를 조금 더 두껍게 하고, 테두리로 색깔을 다른 것으로 설정해서 그리기
            val polygonOutline1 = PolylineOverlay()
            // polygon2는 사이즈의 절반부터 끝까지의 LatLng 좌표를 담당하는 Polyline
            val polygon2 = PolylineOverlay()
            // polygonOutline2은 polygon2의 Outline 경로의 테두리를 그리기 위해 두꼐를 조금 더 두껍게 하고, 테두리로 그리기 위해 색깔을 다른 것으로 설정해서 그리기
            val polygonOutline2 = PolylineOverlay()

            Log.d(TAG, "onMapReady: $gpxCoordinates")

            // 절반으로 나누기 위해 LagLng 데이터들을 저장한 리스트들의 사이즈의 중간 값을 저장할 midIndex 객체 생성
            val midIndex = gpxCoordinates!!.size / 2
            // 인덱스를 받아서 새로운 리스트를 만드는 subList 함수로 0 ~ 중간까지 인덱스를 지정해서 앞부분 절반에 해당하는 새로운 리스트 생성
            val firstHalf = gpxCoordinates!!.subList(0, midIndex)
            // 인덱스를 받아서 새로운 리스트를 만드는 subList 함수로 중간 ~ 끝까지 인덱스를 지정해서 뒷부분 절반에 해당하는 새로운 리스트 생성
            val secondHalf = gpxCoordinates!!.subList(midIndex, gpxCoordinates!!.size)

            // 라인의 테두리를 그리기 위한 PolyLine으로 coords 좌표는 polygon1과 동일하게 설정하고, 색깔을 다르게, 테두리로 그리기 위해 두께는 조금 더 두껍게 설정
            polygonOutline1.coords = firstHalf
            polygon1.setOnClickListener { overlay ->
                if(checkPoly){
                    polygon1.setColor(Color.BLACK)
                    polygon1.setMap(naverMap)
                }
                else{
                    polygon1.setColor(Color.RED)
                    polygon1.setMap(naverMap)
                }
                checkPoly = !checkPoly
                true
            }
            polygonOutline1.color = Color.WHITE
            polygonOutline1.width = 80

            // 앞부분에 해당하는 좌표들을 기반으로 PolyLine을 그릴 것
            polygon1.coords = firstHalf
            // PolyLine의 색깔은 빨간색으로 지정
            polygon1.color = Color.RED
            // Polygon1의 두께를 30으로 설정, width Default는 5
            polygon1.width = 30
            // polygonOutline1의 좌표에 해당하는 Polygon Line을 Navermap에 적용
            polygonOutline1.map = naverMap

            // polygon1의 좌표에 해당하는 Polygon Line을 Navermap에 적용
            // ★주의할 점 : 두꺼운걸 나중에 그려버리면 덮어씌워지는 꼴이기 때문에, 두꺼운 걸 먼저 그리고, 얇은 걸 나중에 그려야 함★
            polygon1.map = naverMap

            // 라인의 테두리를 그리기 위한 PolyLine으로 coords 좌표는 polygon2과 동일하게 설정하고, 색깔을 다르게, 테두리로 그리기 위해 두께는 조금 더 두껍게 설정
            polygonOutline2.coords = secondHalf
            polygonOutline2.color = Color.WHITE
            polygonOutline2.width = 70

            // 뒷부분에 해당하는 좌표들을 기반으로 PolyLine을 그릴 것
            polygon2.coords = secondHalf
            // PolyLine의 색깔은 파란색으로 지정
            polygon2.color = Color.BLUE
            // Polygon1의 두께를 30으로 설정, width Default는 5
            polygon2.width = 30

            // polygonOutline2의 좌표에 해당하는 Polygon Line을 Navermap에 적용
            polygonOutline2.map = naverMap
            // polygon2의 좌표에 해당하는 Polygon Line을 Navermap에 적용
            // ★주의할 점 : 두꺼운걸 나중에 그려버리면 덮어씌워지는 꼴이기 때문에, 두꺼운 걸 먼저 그리고, 얇은 걸 나중에 그려야 함★
            polygon2.map = naverMap
        }
        Toast.makeText(this, "onMapReady 호출", Toast.LENGTH_SHORT).show()
    }
}