package com.emtwnty.schemaker.model.online

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.tasks.await

object UsersRepo {

    private var mFirebaseDb = Firebase.firestore
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var result:String = "Wait"


    fun setData(usersModel: UsersModel){
        CoroutineScope(IO).launch {
            setDataBG(usersModel)
        }
    }

    fun getUserDataByID(uid: String): LiveData<List<UsersModel>>{
        return object : LiveData<List<UsersModel>>(){
            override fun onActive() {
                super.onActive()
                CoroutineScope(Main).launch {
                    value = getUserDataByIDBG(uid)
                }
            }
        }
    }

    private suspend fun setDataBG(usersModel: UsersModel){
        val uid = mAuth.currentUser?.uid.toString()
        val usersRef = mFirebaseDb.collection("users").document(uid)
        val usersData = getUserDataByIDBG(uid)
        if(usersData.isEmpty()){
            usersRef.set(usersModel).await()
        }
    }

    private suspend fun getUserDataByIDBG(uid: String): List<UsersModel>{
        val usersRef = mFirebaseDb.collection("users").document(uid)

        val userData:ArrayList<UsersModel> = arrayListOf()
        val dataSnapshot = usersRef.get().await().toObject<UsersModel>()
        if(dataSnapshot != null){
            userData.add(dataSnapshot)
        }
        return userData
    }

    fun getResult():String{
        return result
    }


///** [START- Fungsi Mengambil data user pada database] **/
//    fun getData(): LiveData<List<UsersModel>> {
//        return object : LiveData<List<UsersModel>>() {
//            // Ketika fungsi getData di panggil
//            override fun onActive() {
//                super.onActive()
//                // mengambil reference dari database
//                mFirebaseDb = Firebase.database.reference.child("users")
//
//                /** [START- run program in background] **/
//                CoroutineScope(IO).launch {
//                    // Mengambil data dari firebase realtime database
//                    mFirebaseDb.addListenerForSingleValueEvent(object : ValueEventListener {
//                        override fun onCancelled(error: DatabaseError) {
//                        }
//
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            val halo: ArrayList<UsersModel> = ArrayList()
//                            for (i in snapshot.children) {
//                                val data = i.getValue(UsersModel::class.java)
//                                halo.add(data!!)
//                            }
//                            /** [START- run program in Main Thread] **/
//                            CoroutineScope(Main).launch {
//                                value = halo
//                            }
//                            /** [STOP- run program in Main Thread] **/
//                        }
//                    })
//                }
//                /** [END- run program in background] **/
//            }
//        }
//    }
///** [END- Fungsi Mengambil data user pada database] **/
}