package com.emtwnty.schemaker.model.online

import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

object CobaRepo {

    private lateinit var mFirebaseDb : DatabaseReference



/** [START- Fungsi Mengambil data user pada database] **/
    fun getData(): LiveData<List<CobaModel>> {
        return object : LiveData<List<CobaModel>>() {
            // Ketika fungsi getData di panggil
            override fun onActive() {
                super.onActive()
                // mengambil reference dari database
                mFirebaseDb = Firebase.database.reference.child("users")

                /** [START- run program in background] **/
                CoroutineScope(IO).launch {
                    // Mengambil data dari firebase realtime database
                    mFirebaseDb.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val halo: ArrayList<CobaModel> = ArrayList()
                            for (i in snapshot.children) {
                                val data = i.getValue(CobaModel::class.java)
                                halo.add(data!!)
                            }
                            /** [START- run program in Main Thread] **/
                            CoroutineScope(Main).launch {
                                value = halo
                            }
                            /** [STOP- run program in Main Thread] **/
                        }
                    })
                }
                /** [END- run program in background] **/
            }
        }
    }
/** [END- Fungsi Mengambil data user pada database] **/
}