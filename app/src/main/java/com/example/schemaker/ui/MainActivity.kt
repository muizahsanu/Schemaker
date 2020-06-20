package com.example.schemaker.ui

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Pair as UtilPair
import com.example.schemaker.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val duration = 2000L

        btn_login.setOnClickListener {
            val options = ActivityOptions.makeSceneTransitionAnimation(
                this,
                UtilPair.create(iv_logo_welcome, "image_logo"),
                UtilPair.create(linear_parent_main,"linear1"),
                UtilPair.create(linear_bottom_main,"linear2")
            )

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent, options.toBundle())
        }

    }
}
