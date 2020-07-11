package com.emtwnty.schemaker.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.emtwnty.schemaker.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_group_detail.*

class GroupDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_detail)

        btn_search_profile.setOnClickListener {
            val result_search = et_search_profile.text.toString()
            findSelectedFragment(result_search)
        }


        tabLayout_profile.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0-> {
                        val fragment = GroupSchduleFragment.newInstance("asd","zxc")
                        replaceFragment(fragment)
                    }
                    else-> {
                        val fragment = MembersFragment.newInstance("asd","zxc")
                        replaceFragment(fragment)
                    }
                }
            }
        })
    }

    private fun findSelectedFragment(resultSearch:String){
        val fragment: Fragment
        if(tabLayout_profile.selectedTabPosition == 0){
            fragment = GroupSchduleFragment.newInstance(resultSearch,"zxc")
        } else fragment = MembersFragment.newInstance(resultSearch,"zxc")
        replaceFragment(fragment)
    }

    private fun replaceFragment(fragment:Fragment){
        val fragTrans = supportFragmentManager.beginTransaction()
        fragTrans.replace(R.id.framelayout,fragment)
        fragTrans.addToBackStack(null)
        fragTrans.commit()
    }
}