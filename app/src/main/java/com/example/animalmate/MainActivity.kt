package com.example.animalmate

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1234 && resultCode == Activity.RESULT_OK){
            var check = data?.getBooleanExtra("success", false)!!
            if(check){
                Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun init() {
        signup.setOnClickListener {
            var i  = Intent(this, SignupActivity::class.java)
            startActivityForResult(i, 1234)
        }
        login.setOnClickListener {
            var userId = id_login.text.toString()
            var userpw = pw_login.text.toString()
            if(userId == null || userpw==null){
                Toast.makeText(this , "아이디/비밀번호를 반드시 입력하세요.", Toast.LENGTH_LONG).show()
            }
            else{

                var rootref = FirebaseDatabase.getInstance().getReference()
                var checkID = rootref.child("Users").child("userinfo").child(userId)

                checkID.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(datasnapshot: DataSnapshot) {
                        var result = false
                        if(datasnapshot.exists()){//아이디가 이미 존재
                            val checkPW = datasnapshot.child("upw").getValue().toString()
                            //Toast.makeText(this@MainActivity, checkPW+ ", "+userpw, Toast.LENGTH_SHORT).show()
                            if(userpw == checkPW){//로그인 성공!
                                result = true
                            }
//
                        }
                        login(result)
                    }

                })


            }

        }
        nonmember.setOnClickListener {
            var i  = Intent(this, MainMenu::class.java)
            i.putExtra("MEMBER", false)
            startActivity(i)
        }

        find_pw.setOnClickListener {
            var i = Intent(this, ChangeInfoActivity::class.java)
            i.putExtra("pw", true)
            startActivity(i)
        }
    }
    fun login(result : Boolean){
        if(result){
            Toast.makeText(this, id_login.text.toString().plus("님 어서오세요!"), Toast.LENGTH_SHORT).show()
            var i = Intent(this, MainMenu::class.java)
            i.putExtra("MEMBER", true)
            i.putExtra("ID", id_login.text.toString())
            startActivity(i)
            id_login.text = null
            pw_login.text = null
        }
        else{
            Toast.makeText(this, "아이디와 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()

        }
    }
}
