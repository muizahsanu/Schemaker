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


    fun getUserDataByID(uid: String): LiveData<UsersModel>{
        return object : LiveData<UsersModel>(){
            override fun onActive() {
                super.onActive()
                CoroutineScope(Main).launch {
                    value = getUserProfileBG(uid)
                }
            }
        }
    }
    private suspend fun getUserProfileBG(uid:String): UsersModel{
        val usersRef = mFirebaseDb.collection("users").document(uid)

        val userDataSnapshot = usersRef.get().await().toObject(UsersModel::class.java)
        return userDataSnapshot!!
    }

    fun getResult():String{
        return result
    }
}