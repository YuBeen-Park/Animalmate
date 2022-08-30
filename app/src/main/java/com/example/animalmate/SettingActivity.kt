package com.example.animalmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    var ID :String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        if(intent.hasExtra("id")){
            ID = intent.getStringExtra("id")
        }
        init()
    }
    fun init(){
        var id = ID.plus("님")
        setting_id.text = id

        setting_info.setOnClickListener {
            var i = Intent(this, ChangeInfoActivity::class.java)
            i.putExtra("id", ID)
            i.putExtra("info", true)
            startActivity(i)
        }
        setting_changepw.setOnClickListener {
            var i = Intent(this, ChangeInfoActivity::class.java)
            i.putExtra("id", ID)
            i.putExtra("info", false)
            startActivity(i)
        }
    }
}
