package com.emtwnty.schemaker.model.online

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.tasks.await

object GroupRepo {

    private var mDatabase = Firebase.firestore
    var responseCallback: MutableLiveData<String> = MutableLiveData()
    private var groupListener: GroupListener? = null

    fun addGroup(groupMap: HashMap<String,Any>){
        responseCallback.value = "RUNNING"
        CoroutineScope(IO).launch {
            addGroupBG(groupMap)
        }
    }

    private suspend fun addGroupBG(groupMap: HashMap<String,Any>){
        delay(1500)
        val groupID = groupMap.get("groupID")
        val groupRef = mDatabase.collection("groups").document(groupID.toString())
        groupRef.set(groupMap).addOnCompleteListener {
            if(it.isSuccessful){
                responseCallback.value = "FINISH"
            }
            else{
                responseCallback.value = it.exception.toString()
            }
        }
        withContext(Main){
            responseCallback.value = ""
        }
    }

    fun getAllGroup(): LiveData<List<GroupModel>>{
        return object : LiveData<List<GroupModel>>(){
            override fun onActive() {
                super.onActive()
                CoroutineScope(Main).launch {
                    value = getAllGroupBG()
                }
            }
        }
    }

    private suspend fun getAllGroupBG(): ArrayList<GroupModel>{
        val groupRef = mDatabase.collection("groups")
        val groupList: ArrayList<GroupModel> = arrayListOf()
        delay(3000L)
        groupRef.get().addOnSuccessListener {result->
            for (document in result){
                val dataSnapshot = document.toObject(GroupModel::class.java)
                groupList.add(dataSnapshot)
            }
        }.await()
        return groupList
    }
}