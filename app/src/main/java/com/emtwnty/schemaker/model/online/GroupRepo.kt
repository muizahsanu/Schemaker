package com.emtwnty.schemaker.model.online

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.tasks.await

object GroupRepo {

    private var mDatabase = Firebase.firestore
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var responseCallback: MutableLiveData<String> = MutableLiveData()
    private var groupListener: GroupListener? = null
    private var mStorage: FirebaseStorage = Firebase.storage

    fun addGroup(groupMap: HashMap<String,Any>){
        responseCallback.value = "RUNNING"
        CoroutineScope(IO).launch {
            addGroupBG(groupMap)
        }
    }

    private suspend fun addGroupBG(groupMap: HashMap<String,Any>){
        delay(1500)
        val userID = mAuth.currentUser?.uid.toString()
        val users = HashMap<String,Any>()
        users.put("role","hokage")
        val groupID = groupMap.get("groupID")
        val groupRef = mDatabase.collection("groups").document(groupID.toString())
        val memberRef = groupRef.collection("members").document(userID)
        val usersRef = mDatabase.collection("users").document(userID)
            .collection("groups").document(groupID.toString())
        groupRef.set(groupMap).addOnCompleteListener {
            if(it.isSuccessful){
                memberRef.set(users)
                usersRef.set(users)
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
                CoroutineScope(IO).launch {
                    val userID = mAuth.currentUser?.uid.toString()
                    val groupRef = mDatabase.collection("groups")
                    val groupList: ArrayList<GroupModel> = arrayListOf()
                    val usersRef = mDatabase.collection("users").document(userID)
                        .collection("groups")
                    groupList.clear()
                    usersRef.get().addOnSuccessListener { resultUser->
                        for (documentUser in resultUser){
                            groupRef.whereEqualTo("groupID",documentUser.id).get().addOnSuccessListener {result->
                                for (document in result){
                                    val data = document.toObject(GroupModel::class.java)
                                    groupList.add(data)
                                }
                                CoroutineScope(Main).launch {
                                    value = groupList
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun getImageURL(imageUri: Uri,groupID: String): LiveData<String>{
        return object : LiveData<String>(){
            override fun onActive() {
                super.onActive()
                responseCallback.value = "UPLOAD_IMAGE"
                CoroutineScope(IO).launch {
                    val imageRef = mStorage.reference.child("/images/$groupID")
                    imageRef.putFile(imageUri).addOnCompleteListener {
                        if(it.isSuccessful){
                            imageRef.downloadUrl.addOnSuccessListener {
                                value = it.toString()
                                responseCallback.value = "FINISH_UPLOAD_IMAGE"
                            }
                        }
                    }
                }
            }
        }
    }
}