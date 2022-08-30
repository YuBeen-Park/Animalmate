package com.example.animalmate

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_signup.*


class SignupActivity : AppCompatActivity() {
    lateinit var rdatabase : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        init()
    }

    private fun init() {
        rdatabase = FirebaseDatabase.getInstance().getReference("Users")
        signup_register.setOnClickListener {
            var id = signup_id.text.toString()
            var pw = signup_pw.text.toString()
            var name = signup_name.text.toString()
            var phone = signup_phone.text.toString()
            var email = signup_email.text.toString()

            if(id == "" ||pw == "" || name == "" || phone == "" || email == ""){
                Toast.makeText(this, "모든 정보를 입력해주세요!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val userinfo = UserInfo(pw, name, phone, email)
            //아이디 중복체크 후 데이터베이스에 추가
            var rootref = FirebaseDatabase.getInstance().getReference()
            var checkID = rootref.child("Users").child("ID").child(id)
                //.equalTo(id)

            checkID.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(datasnapshot: DataSnapshot) {
                    if(datasnapshot.exists()){//아이디가 이미 존재
                        Toast.makeText(this@SignupActivity, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        this@SignupActivity.register(userinfo, id)
                    }
                }

            })

        }
        signup_cancel.setOnClickListener {
            var i = Intent()
            i.putExtra("success", false)
            setResult(Activity.RESULT_CANCELED, i)
            finish()
        }
    }

    fun register(userinfo : UserInfo, uid : String){
        rdatabase.child("ID").child(uid).setValue("")
        rdatabase.child("userinfo").child(uid).setValue(userinfo)
        var i = Intent()
        i.putExtra("success", true)
        setResult(Activity.RESULT_OK, i)
        finish()
    }
}

