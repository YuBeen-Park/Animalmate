package com.example.animalmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_community.*

class CommunityActivity : AppCompatActivity() {

    lateinit var layoutManager: LinearLayoutManager
    lateinit var communityAdapter : CommunityAdapter
    lateinit var reviewAdapter : ReviewAdapter
    lateinit var rdb : DatabaseReference

    var ID : String = ""
    var review = false
    var reviewHosName : String = ""
    var reviewHosAddress :String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)
        if(intent.hasExtra("id")){
            //Log.i("들어왔나", "yes")
            ID = intent.getStringExtra("id")
            Log.i("communityid", ID)
        }
        if(intent.hasExtra("review")){
            review = intent.getBooleanExtra("review", true)
            if(review){
                reviewHosName = intent.getStringExtra("hname")
                reviewHosAddress = intent.getStringExtra("haddress")
                community_hospital.text = reviewHosName
            }
        }
        init()
        if(review){//병원 리뷰인경우
            initReview()
        }
        else{
            community_hospital.visibility = View.GONE
            initCommunity()
        }
        
        initBtn()
    }
    fun init(){
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        community_recyclerview.layoutManager = layoutManager

    }
    fun initCommunity(){
        rdb = FirebaseDatabase.getInstance().getReference("Community")
        val query = FirebaseDatabase.getInstance().getReference("Community")
        val option = FirebaseRecyclerOptions.Builder<CommunityInfo>()
            .setQuery(query, CommunityInfo::class.java)
            .build()
        communityAdapter = CommunityAdapter(option)
        community_recyclerview.adapter = communityAdapter
    }
    fun initReview(){
        rdb = FirebaseDatabase.getInstance().getReference("Review")
        val query = FirebaseDatabase.getInstance().reference.child("Review")
            .child(reviewHosName).child(reviewHosAddress).limitToLast(50)
        val option = FirebaseRecyclerOptions.Builder<ReviewInfo>()
            .setQuery(query, ReviewInfo::class.java)//담아올 클래스
            .build()
        reviewAdapter = ReviewAdapter(option)
        community_recyclerview.adapter = reviewAdapter
    }
    
    fun initBtn(){
        community_register.setOnClickListener{
            if(review){//병원리뷰인 경우
                var i = Intent(this, RegisterPostActivity::class.java)
                i.putExtra("id", ID)
                i.putExtra("review", true)
                i.putExtra("hname", reviewHosName)
                i.putExtra("haddress", reviewHosAddress)
                startActivity(i)
                finish()
            }
            else{//커뮤니티리뷰인 경우
                var i = Intent(this, RegisterPostActivity::class.java)
                i.putExtra("id", ID)
                i.putExtra("review", false)
                startActivity(i)
                finish()
            }
        }
    }
    override fun onStart(){
        super.onStart()
        if(review){
            reviewAdapter.startListening()
        }
        else{
            communityAdapter.startListening()
        }
    }
    override fun onStop(){
        super.onStop()
        if(review){
            reviewAdapter.stopListening()
        }
        else{
            communityAdapter.stopListening()
        }
    }
}
