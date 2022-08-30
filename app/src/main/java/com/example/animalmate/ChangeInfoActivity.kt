package com.example.animalmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_change_info.*

class ChangeInfoActivity : AppCompatActivity() {

    lateinit var rdatabase : DatabaseReference
    var id :String = ""
    var info : Boolean = true
    var changepw : Boolean = false

    var existing_Name : String = ""
    var existing_Phone : String = ""
    var existing_Email : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_info)

        if(intent.hasExtra("id")){
            id = intent.getStringExtra("id")
            info = intent.getBooleanExtra("info", true)
        }
        else{
            changepw = intent.getBooleanExtra("pw", true)
        }
        change_cancel.setOnClickListener {
            finish()
        }
        init()
    }
    fun init(){
        rdatabase = FirebaseDatabase.getInstance().getReference("Users")
        if(info){
            if(changepw){//비밀번호 찾기인 경우
                change_email.visibility = View.GONE
                change_insert.text = "확인"
                change_pw.visibility = View.GONE
                change_newpw.visibility = View.GONE
                checkInfo()
            }
            else{
                change_newpw.visibility = View.GONE
                change_id.visibility = View.GONE
                changeInfo()
            }

        }
        else{
            change_name.visibility = View.GONE
            change_phone.visibility = View.GONE
            change_email.visibility = View.GONE
            change_id.visibility = View.GONE
            changePW()
        }


    }
    fun checkInfo(){
        change_insert.setOnClickListener {
            if(change_insert.text.toString() == "확인"){
                var name = change_name.text.toString()
                var phone = change_phone.text.toString()
                var curid = change_id.text.toString()
                if(curid == ""||name == "" || phone == ""){
                    Toast.makeText(this, "모든 정보를 입력해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                var rootref = FirebaseDatabase.getInstance().getReference()
                var check = rootref.child("Users").child("userinfo").child(curid)
                check.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(datasnapshot: DataSnapshot) {
                        val existingName = datasnapshot.child("uname").getValue(String::class.java).toString()
                        val existingPhone = datasnapshot.child("uphoneNum").getValue(String::class.java).toString()

                        if(existingName == name && existingPhone == phone){//비밀번호가 일치할 경우
                            existing_Name = existingName
                            existing_Phone = existingPhone
                            existing_Email = datasnapshot.child("uemail").getValue(String::class.java).toString()
                            this@ChangeInfoActivity.change(curid)
                        }
                        else{
                            Toast.makeText(this@ChangeInfoActivity, "정보가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }

                })
            }
            else if(change_insert.text.toString() == "등록"){
                var newpw = change_pw.text.toString()
                var checknewpw = change_newpw.text.toString()
                if(newpw != checknewpw){
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
                else{
                    val newpw = UserInfo(newpw, existing_Name, existing_Phone, existing_Email)
                    register(newpw, id)
                }
            }


        }
    }
    fun change(curid : String){
        change_name.visibility = View.GONE
        change_id.visibility = View.GONE
        change_phone.visibility = View.GONE
        change_pw.visibility = View.VISIBLE
        change_pw.hint = "새로운 비밀번호를 입력하세요"
        change_newpw.visibility = View.VISIBLE
        change_newpw.hint = "새로운 비밀번호를 한번더 입력하세요"
        change_insert.text = "등록"
        id = curid
    }
    fun changeInfo(){
        change_insert.setOnClickListener {
            var name = change_name.text.toString()
            var phone = change_phone.text.toString()
            var email = change_email.text.toString()
            var pw = change_pw.text.toString()
            var userinfo = UserInfo(pw, name, phone, email)
            if(name == "" || phone == "" || email == ""|| pw == ""){
                Toast.makeText(this, "모든 정보를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var rootref = FirebaseDatabase.getInstance().getReference()
            var checkPW = rootref.child("Users").child("userinfo").child(id).child("upw")
            checkPW.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(datasnapshot: DataSnapshot) {
                    val existingPW = datasnapshot.getValue(String::class.java).toString()
                    if(existingPW == pw){//비밀번호가 일치할 경우
                        this@ChangeInfoActivity.register(userinfo, id)
                    }
                    else{
                        Toast.makeText(this@ChangeInfoActivity, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }
    }
    fun changePW(){
        change_insert.setOnClickListener {
            var pw = change_pw.text.toString()
            var newPW = change_newpw.text.toString()
            if(pw == ""||newPW == ""){
                Toast.makeText(this, "모든 정보를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var rootref = FirebaseDatabase.getInstance().getReference()
            var checkPW = rootref.child("Users").child("userinfo").child(id)
            checkPW.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(datasnapshot: DataSnapshot) {

                    val existingPW = datasnapshot.child("upw").getValue(String::class.java).toString()
                    if(existingPW == pw){//비밀번호가 일치할 경우
                        val name = datasnapshot.child("uname").getValue(String::class.java).toString()
                        val phone = datasnapshot.child("uphoneNum").getValue(String::class.java).toString()
                        val email = datasnapshot.child("uemail").getValue(String::class.java).toString()
                        val userinfo = UserInfo(newPW, name, phone, email)
                        this@ChangeInfoActivity.register(userinfo, id)
                    }
                    else{
                        Toast.makeText(this@ChangeInfoActivity, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }
    }

    fun register(userinfo : UserInfo, uid : String){
        rdatabase.child("userinfo").child(uid).setValue(userinfo)
        finish()
    }
}
