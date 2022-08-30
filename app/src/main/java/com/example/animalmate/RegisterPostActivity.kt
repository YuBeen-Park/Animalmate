package com.example.animalmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_register_post.*

class RegisterPostActivity : AppCompatActivity() {

    var ID : String = ""
    var review : Boolean = false
    var reviewHosName : String = ""
    var reviewHosAddress :String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_post)
        if(intent.hasExtra("id")){
            ID = intent.getStringExtra("id")
            review = intent.getBooleanExtra("review", true)

        }
        init()
    }

    fun init(){

        if(review){
            reviewHosName = intent.getStringExtra("hname")
            reviewHosAddress = intent.getStringExtra("haddress")
            post_title.visibility = View.GONE
            post_cancel.setOnClickListener{
                var i = Intent(this@RegisterPostActivity, CommunityActivity::class.java)
                i.putExtra("id", ID)
                i.putExtra("review", true)
                i.putExtra("hname", reviewHosName)
                i.putExtra("haddress", reviewHosAddress)
                startActivity(i)
                finish()
            }
            post_button.setOnClickListener{

                var content = post_content.text.toString()
                var rating = post_rating.rating
                if(content != "" && rating != null){

                    var rdatabase = FirebaseDatabase.getInstance().getReference("Review")
                    val reviewInfo = ReviewInfo(ID, content, rating)
                    var checkRatings = rdatabase.child(reviewHosName).child(reviewHosAddress).child(ID)
                    var exist = false
                    var existingRating = 0.0f
                    checkRatings.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if(p0.childrenCount >0){//이전에 리뷰한 적이 있음
                                exist = true
                                existingRating = p0.child("rating").getValue(Float::class.java)!!
                                //Log.i("rating", existingRating.toString())
                            }
                            calculatingRating(reviewInfo, exist, existingRating, rating)
                        }

                    })
                }

            }
        }
        else{
            post_rating.visibility = View.GONE
            post_cancel.setOnClickListener{
                var i = Intent(this@RegisterPostActivity, CommunityActivity::class.java)
                i.putExtra("id", ID)
                i.putExtra("review", false)
                startActivity(i)
                finish()/////////////////activity 종료
            }
            post_button.setOnClickListener{
                var title = post_title.text.toString()
                var content = post_content.text.toString()
                if(title != "" && content != ""){

                    var rdatabase = FirebaseDatabase.getInstance().getReference("Community")
                    val communityInfo = CommunityInfo(ID, title, content)
                    rdatabase.push().setValue(communityInfo)
                    var i = Intent(this@RegisterPostActivity, CommunityActivity::class.java)
                    i.putExtra("id", ID)
                    i.putExtra("review", false)
                    startActivity(i)
                    finish()/////////////////activity 종료
                }

            }
        }
    }
    fun calculatingRating(reviewInfo : ReviewInfo, exist : Boolean, existingRating : Float, rating:Float){
        var rdatabase = FirebaseDatabase.getInstance().getReference("Review")
        rdatabase.child(reviewHosName).child(reviewHosAddress).child(ID).setValue(reviewInfo)

        var calDatabase = FirebaseDatabase.getInstance().getReference("Rating")
            .child(reviewHosName).child(reviewHosAddress)
        calDatabase.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.childrenCount >0 ){
                    var index = p0.child("index").getValue(Int::class.java)
                    var ratings = p0.child("rating").getValue(Float::class.java)
                    if(exist){//교체해줘야 하는경우
                        ratings = ratings!! -existingRating+rating
                    }
                    else{
                        index = index!! + 1
                        ratings = ratings!! + rating
                    }


                    calDatabase.child("index").setValue(index)
                    calDatabase.child("rating").setValue(ratings)

                }
                else{//리뷰 없어
                    calDatabase.child("index").setValue(1)
                    calDatabase.child("rating").setValue(rating)
                }
            }

        })
        var i = Intent(this@RegisterPostActivity, CommunityActivity::class.java)
        i.putExtra("id", ID)
        i.putExtra("review", true)
        i.putExtra("hname", reviewHosName)
        i.putExtra("haddress", reviewHosAddress)
        startActivity(i)
        finish()///////////////////////////////activity 종료
    }
}
