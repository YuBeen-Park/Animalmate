package com.example.animalmate

import android.Manifest
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_second.*
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import org.jsoup.select.Elements
import java.net.URL

class SecondActivity : AppCompatActivity(), GoogleMap.OnMarkerClickListener{

    var fusedLocationClient : FusedLocationProviderClient?=null
    var locationCallback: LocationCallback?=null
    var locationRequest : LocationRequest?=null

    lateinit var googlemap:GoogleMap

    var loc = LatLng(37.554752, 126.970631)
    var arrLoc = ArrayList<LatLng>()

    //var checkHospital = false
    //https://openapi.gg.go.kr/Animalhosptl?KEY=d1bb600331e147bbb5e632a809f957a9&Type=xml&pIndex=0
    val key = "d1bb600331e147bbb5e632a809f957a9"
    val hospitalURL = "https://openapi.gg.go.kr/Animalhosptl?KEY="+key+"&Type=xml&pSize=200&pIndex="
    //https://openapi.gg.go.kr/AnimalPharmacy?KEY=d1bb600331e147bbb5e632a809f957a9&Type=xml&pSize=200&pIndex=1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        map_linearlayout.visibility = View.GONE
        initLocation()
        //initMap()
        //initspinner()

    }

    private fun initLocation() {
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //허용한 경우
            getuserlocation()
            startLocationUpdates()
            initMap()

        }
        else{
            //허용되지 않았을 때 권한 요청
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 100)
        }

    }


    private fun initHospital() {
        val task = MyAsyncTask(object : MyAsyncTask.AsyncResponse{
            override fun readyToSetHospital(arr: ArrayList<HospitalInfo>) {

                //Log.i("hospitalnum", arr.size.toString())

                for(hospital in arr){
                    val position = hospital.hLatlng
                    val options = MarkerOptions()
                    options.position(position)
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    var marker = googlemap.addMarker(options)
                    marker.tag = hospital
                }
            }

        })
        task.execute(hospitalURL)
    }
    private fun startLocationUpdates() {//location정보 갱신
        locationRequest = LocationRequest.create()?.apply{
            //interval = 10000
            //fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?:return
                for(location in locationResult.locations){
                    loc = LatLng(location.latitude, location.longitude)
                    googlemap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f))
                }


            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient?.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper())



    }

    fun stopLocationUpdates(){
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }


    private fun getuserlocation() {//최신정보 가져오기
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient?.lastLocation?.addOnSuccessListener {
            loc = LatLng(it.latitude, it.longitude)
        }
    }

    override fun onRequestPermissionsResult(//승인하면 결과 반환되는 곳
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 100){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED){
                getuserlocation()
                startLocationUpdates()
                initMap()
            }
            else{
                Toast.makeText(this, "위치정보 제공을 해야 합니다.", Toast.LENGTH_SHORT).show()
                //initMap()
            }
        }
    }
    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync{
            googlemap = it
            googlemap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 10.0f))
            googlemap.setMinZoomPreference(10.0f)
            googlemap.setMaxZoomPreference(18.0f)
            val options = MarkerOptions()
            options.position(loc)
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//            options.title("역")
//            options.snippet("서울역")
//            val mk1 = googlemap.addMarker(options)
//            mk1.showInfoWindow()
            initMapListener()

            initHospital()

        }
    }
    fun initMapListener(){
        googlemap.setOnMapClickListener {
            //googlemap.clear()//마커정보 다 지워
            map_linearlayout.visibility = View.GONE

        }
        googlemap.setOnMarkerClickListener(this)
    }
//


    class MyAsyncTask(asyncResponse: AsyncResponse): AsyncTask<String, Unit, Unit>(){

        var response: AsyncResponse? = asyncResponse

        var arr : ArrayList<HospitalInfo> = ArrayList()

        interface AsyncResponse {
            fun readyToSetHospital(arr:ArrayList<HospitalInfo>)
        }

        override fun doInBackground(vararg params: String?) {
            //var pIndex = 1
            for( pIndex in 1..30){
                val argUrl = params[0].toString()
                var curURL = argUrl.plus(pIndex.toString())
                val url = URL(curURL)
                val doc = Jsoup.connect(url.toString()).parser(Parser.xmlParser()).get()

                var hospitals: Elements
                hospitals = doc.select("row")
                if(hospitals.size <= 0){
                    break;
                }
                for (hospital in hospitals) {
                    val available = hospital.select("BSN_STATE_NM").text()

                    if(available.trim() == "정상"){
                        val latitude= hospital.select("REFINE_WGS84_LAT").text()
                        val longitude = hospital.select("REFINE_WGS84_LOGT").text()
                        val hosName = hospital.select("BIZPLC_NM").text()
                        val hosPhone = hospital.select("LOCPLC_FACLT_TELNO").text()
                        val hosAddress = hospital.select("REFINE_ROADNM_ADDR").text()
//                        if(latitude == null || longitude == null){
//                            continue
//                        }
                        var l : LatLng
                        try{
                            l = LatLng(latitude.toDouble(), longitude.toDouble())
                            //Log.i("hospital", hospital.select("BIZPLC_NM").text().toString())
                            arr.add(HospitalInfo(hosName, hosPhone, l, hosAddress))
                        }catch ( e : NumberFormatException){
                            continue
                        }
                    }

                }
            }


        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            response!!.readyToSetHospital(arr)
        }

    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        map_linearlayout.visibility = View.VISIBLE
        val hosInfo = marker?.tag as HospitalInfo
        map_name.text = hosInfo.hName
        map_address.text = hosInfo.hAddress
        map_phone.text = hosInfo.hPhone
        //별점 계산
        var rdatabase = FirebaseDatabase.getInstance().getReference("Rating")
            .child(hosInfo.hName).child(hosInfo.hAddress)
        rdatabase.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.childrenCount >0){
                    var index = p0.child("index").getValue(Int::class.java)
                    var ratings = p0.child("rating").getValue(Float::class.java)
                    var total = ratings!! / index!!
                    content_ratingBar.rating = total
                    content_ratingBar.visibility = View.VISIBLE
                    content_ratingNumber.text = total.toString()
                    content_ratingNumber.visibility = View.VISIBLE
                }
                else{
                    content_ratingBar.visibility = View.GONE
                    content_ratingNumber.visibility = View.GONE
                }
            }

        })

        return true
    }


}
