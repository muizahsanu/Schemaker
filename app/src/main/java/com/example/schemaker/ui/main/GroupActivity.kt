package com.example.schemaker.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.schemaker.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_group.*

class GroupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        bottomNav_group.selectedItemId = R.id.nav_group_menu
        bottomNav_group.setOnNavigationItemSelectedListener(navigationItemSelectedListener())
    }

    override fun onResume() {
        super.onResume()
        bottomNav_group.selectedItemId = R.id.nav_group_menu
    }

    private fun navigationItemSelectedListener() =
        object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.nav_home_menu -> {
                        val intent = Intent(this@GroupActivity,HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
                    }
                    R.id.nav_setting_menu -> {
                        startActivity(
                            Intent(this@GroupActivity, SettingActivity::class.java)
                        )
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
                    }
                }
                return true
            }
        }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down)
    }
}