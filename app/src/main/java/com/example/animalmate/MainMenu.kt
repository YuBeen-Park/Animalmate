package com.example.animalmate

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.text.htmlEncode
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_main_menu.*
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import org.jsoup.select.Elements
import java.lang.ref.WeakReference
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.Charset

class MainMenu : AppCompatActivity() {

    val key = "d1bb600331e147bbb5e632a809f957a9"
    val hospitalURL = "https://openapi.gg.go.kr/Animalhosptl?KEY="+key+"&Type=xml&pSize=200&"
    lateinit var adapter : HomeSearchAdapter
    var search :String= ""
    var ID : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        supportActionBar?.title = "ANIMALMATE"
        //supportActionBar?.setBackgroundDrawable(ColorDrawable())
        initMember()
        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(intent.hasExtra("ID")){
            menuInflater.inflate(R.menu.menu, menu)
        }
        else{
            menuInflater.inflate(R.menu.menu2, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id == R.id.action_logout){

            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun initMember(){

        if(intent.hasExtra("MEMBER")){
            val member = intent.getBooleanExtra("MEMBER",false)
            if(member){//로그인한 경우
                ID = intent.getStringExtra("ID")

                //Log.i("mainmenu", ID)
            }
            else{//비회원인 경우
            }
        }
    }

    fun init(){

        menuRecycler.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)
        adapter = HomeSearchAdapter(ArrayList<HospitalInfo>())

        adapter.itemClickListener = object : HomeSearchAdapter.OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                //Log.i("아이템 클릭", "이동해야해")
                if(ID != ""){
                    val selectedHospitalName = adapter.hospital[position].hName
                    val selectedHospitalAddress = adapter.hospital[position].hAddress
                    var i = Intent(this@MainMenu, CommunityActivity::class.java)
                    i.putExtra("id", ID)
                    //Log.i("clickid", ID)
                    i.putExtra("review", true)
                    i.putExtra("hname", selectedHospitalName)
                    i.putExtra("haddress", selectedHospitalAddress)
                    startActivity(i)
                }
                else{
                    Toast.makeText(this@MainMenu, "회원이 되면 더 많은 기능을 즐길 수 있습니다!", Toast.LENGTH_SHORT).show()
                }

            }

        }
        menuRecycler.adapter = adapter
        mapbtn.setOnClickListener {

            var i = Intent(this, SecondActivity::class.java)
            startActivity(i)
        }
        weatherbtn.setOnClickListener {
            if(ID ==""){//비회원
                Toast.makeText(this, "회원이 되면 더 많은 기능을 즐길 수 있습니다!", Toast.LENGTH_SHORT).show()

            }
            else{//회원
                var i = Intent(this, WeatherActivity::class.java)
                startActivity(i)
            }
        }
        communitybtn.setOnClickListener {
            if(ID ==""){//비회원
                Toast.makeText(this, "회원이 되면 더 많은 기능을 즐길 수 있습니다!", Toast.LENGTH_SHORT).show()

            }
            else{//회원
                var i = Intent(this, CommunityActivity::class.java)
                i.putExtra("id", ID)
                i.putExtra("review", false)
                startActivity(i)
            }
        }
        mypagebtn.setOnClickListener {
            if(ID ==""){//비회원
                Toast.makeText(this, "회원이 되면 더 많은 기능을 즐길 수 있습니다!", Toast.LENGTH_SHORT).show()

            }
            else{//회원
                var i = Intent(this, SettingActivity::class.java)
                i.putExtra("id", ID)
                startActivity(i)
            }
        }
        mainmenu_searchbtn.setOnClickListener {
            search = editText_search.text.toString()
            if(search == ""){
                adapter.hospital.clear()
                adapter.notifyDataSetChanged()

                return@setOnClickListener
            }
            mainmenu_searchbtn.isClickable = false
            Toast.makeText(this, "잠시 기다려주세요!", Toast.LENGTH_SHORT).show()
            val check = search[search.length-1]
            if( check == '시' || check == '군'){//도시이름인 경우
                startTask(true)
            }
            else{//병원 이름인 경우
                startTask(false)
            }
        }
    }

    fun startTask(searchCheck : Boolean){
        val task = MyAsyncTask(this)
        task.execute(searchCheck)
    }

    fun dataChanged(){
        adapter.notifyDataSetChanged()
        if(adapter.hospital.size <=0){
            Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    class MyAsyncTask(context:MainMenu): AsyncTask<Boolean, Unit, Unit>(){
        val activityReference = WeakReference(context)
        override fun doInBackground(vararg params: Boolean?) {
            val activity = activityReference.get()
            activity?.adapter?.hospital?.clear()
            var argUrl = activity?.hospitalURL
            val search = activity?.search
            if(params[0]!!){//도시이름
                val searchEncode = URLEncoder.encode(search, "utf-8")
                argUrl = argUrl.plus("SIGUN_NM=").plus(searchEncode).plus("&pIndex=")
                //argUrl = argUrl.plus("SIGUN_NM=").plus(search).plus("&pIndex=")

                //Log.i("city", "true")
            }
            else{//병원이름
                argUrl = argUrl.plus("pIndex=")
            }
            for( pIndex in 1..20){
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

                        if(params[0]!!){//도시이름
                            var l : LatLng
                            try{
                                l = LatLng(latitude.toDouble(), longitude.toDouble())
                                //Log.i("hospital", hospital.select("BIZPLC_NM").text().toString())
                                activity?.adapter?.hospital?.add(HospitalInfo(hosName, hosPhone, l, hosAddress))
                            }catch ( e : NumberFormatException){
                                continue
                            }
                        }
                        else{
                            var count =0
                            if(hosName.contains(search!!, true)){//검색내용 있는 경우
                                Log.i("search", count.toString())
                                var l : LatLng
                                try{
                                    l = LatLng(latitude.toDouble(), longitude.toDouble())
                                    //Log.i("hospital", hospital.select("BIZPLC_NM").text().toString())
                                    activity?.adapter?.hospital?.add(HospitalInfo(hosName, hosPhone, l, hosAddress))
                                }catch ( e : NumberFormatException){
                                    continue
                                }
                            }

                        }

                    }

                }

            }



        }


        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            val activity = activityReference.get()
//            if(activity == null || activity.isFinishing){
//                return
//            }
            Log.i("adaptersize", activity?.adapter?.hospital?.size.toString())
            activity?.dataChanged()
            activity?.mainmenu_searchbtn?.isClickable = true
        }
    }
}
