package com.example.animalmate

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationProvider
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_weather.*
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import org.jsoup.select.Elements
import java.net.URL

class WeatherActivity : AppCompatActivity() {

    val weatherURL = "https://api.openweathermap.org/data/2.5/weather?appid=c27e8bc3c224aee4ff33238c1df3d682&mode=xml&units=metric&lat="
    val long = "&lon="
    //api.openweathermap.org/data/2.5/weather?lat=37.554752&lon=126.970631&appid=c27e8bc3c224aee4ff33238c1df3d682&mode=xml
    //http://api.openweathermap.org/data/2.5/weather?appid=c27e8bc3c224aee4ff33238c1df3d682&mode=xml&units=metric&lat=37.554752&lon=126.970631
    lateinit var locationManager : LocationManager
    var fusedLocationClient : FusedLocationProviderClient?=null


    lateinit var curWeather : WeatherInfo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        init()
        getLocationPermission()
    }
    fun init(){
        weather_update.setOnClickListener {
            getLocationPermission()
        }
    }
    fun getMyLocation(location : LatLng){//날씨
        Log.i("위치", location.latitude.toString().plus(" : ").plus(location.longitude.toString()))
        var curURL = weatherURL.plus(location.latitude.toString()).plus(long)
            .plus(location.longitude.toString())
        val task = MyAsyncTask(this)
        task.execute(curURL)

    }

    fun getLocationPermission()  {
        var currentLocation: Location? = null
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //허용한 경우
            fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient?.lastLocation?.addOnSuccessListener {
                val latlng = LatLng(it.latitude, it.longitude)
                getMyLocation(latlng)
            }
//

        } else {
            //허용되지 않았을 때 권한 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 100
            )
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
                getLocationPermission()
            }
            else{
                Toast.makeText(this, "위치정보 제공을 해야 합니다.", Toast.LENGTH_SHORT).show()

            }
        }
    }

    fun setWeather(){
        weather_feel.text = curWeather.feeltemp
        weather_temperature.text = curWeather.temparature
        weather.text = curWeather.weather
        weather_date.text = curWeather.lastupdate
        setImage(curWeather.feeltemp)
    }

    fun setImage(temp : String){
        var temparature :Float = temp.toFloat()
        var level = 0
        var text = ""
        if(temparature>=35 || temparature<=-12){
            level = 3
        }
        else if(temparature>=28 || temparature<=-4){
            if(curWeather.bad){
                level = 3
            }
            else{
                level = 2
            }
        }
        else if(temparature>=22 || temparature<=7){
            if(curWeather.bad){
                level = 2
            }
            else{
                level = 1
            }
        }
        else{
            if(curWeather.bad){
                level = 2
            }
            else{
                level = 0
            }
        }

        when(level){
            0->{
                weather_face.setImageResource(R.drawable.face0)
                weather_state.text = "좋음"
                text = "산책하기 딱 좋은 날씨에요!"
            }
            1->{
                weather_face.setImageResource(R.drawable.face1)
                weather_state.text = "양호"
                text = "산책 시 물을 꼭 챙기세요!"
            }
            2->{
                weather_face.setImageResource(R.drawable.face2)
                weather_state.text = "나쁨"
                text = "오랜 산책 시 반려동물의 상태를 반드시 확인해주세요!"
            }
            3->{
                weather_face.setImageResource(R.drawable.face3)
                weather_state.text = "매우나쁨"
                text = "짧은 산책을 권장해요!"
            }
        }
        if(curWeather.bad){
            text = "짧은 산책을 권장해요!"
        }
        comment.text = text
    }


    class MyAsyncTask(val activity: WeatherActivity) : AsyncTask<String, Unit, Unit>() {

        override fun doInBackground(vararg params: String?) {
            val curURL = params[0].toString()
            val weatherdoc = Jsoup.connect(URL(curURL).toString())
                .parser(Parser.xmlParser()).get()
            val current : Elements = weatherdoc.select("current")

            val parsing1 = current[0].select("feels_like")
            val feelTemp = parsing1[0].attr("value")

            val parsing2 = current[0].select("temperature")
            val temperature = parsing2[0].attr("value")

            val parsing3 = current[0].select("weather")
            val curweather = parsing3[0].attr("value")

            val number = parsing3[0].attr("number").toInt()
            var bad = false
            if(number >=200 && number<=299){
                bad = true
            }
            else if(number >=502 && number<=599){
                bad = true
            }
            else if(number ==602 || number == 611 || number==622){
                bad = true
            }



            val parsing4 = current[0].select("lastupdate")
            val lastupdate = parsing4[0].attr("value")


            var weatherinfo = WeatherInfo(feelTemp, temperature, curweather, lastupdate, bad)
            Log.i("weather", feelTemp.plus(" : ").plus(temperature).plus(" : ").plus(curweather))
            activity.curWeather = weatherinfo
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            activity.setWeather()
        }

    }
}
