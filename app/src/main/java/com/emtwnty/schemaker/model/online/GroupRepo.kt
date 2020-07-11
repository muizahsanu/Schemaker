package com.emtwnty.schemaker.model.online

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

object GroupRepo {

    private var mDatabase = Firebase.firestore
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var responseCallback: MutableLiveData<String> = MutableLiveData()
    private var groupListener: GroupListener? = null
    private var mStorage: FirebaseStorage = Firebase.storage
    val arrayData = ArrayList<GroupModel>()

    private var _mutbaleDataGroup: MutableLiveData<ArrayList<GroupModel>> = MutableLiveData<ArrayList<GroupModel>>()

    init {
        getDataUsers()
    }

    /** [ START ] Menambahkan / membuat group **/
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
    /** [ END ] Menambahkan / membuat group **/


    /** [ START ] Menampilkan Group sesuai dengan member user **/
    private fun getDataUsers(){
        val userID = mAuth.currentUser?.uid.toString()
        mDatabase.collection("users").document(userID)
            .collection("groups").get().addOnSuccessListener {
                if (it != null) {
                    val documentsUsers = it.documents
                    for (halo in documentsUsers){
                        getDataGroup(halo.id)
                    }
                }
            }
    }
    private fun getDataGroup(groupID: String){
        mDatabase.collection("groups").whereEqualTo("groupID",groupID).get().addOnSuccessListener {
            if(it != null){

                val documents = it.documents
                documents.forEach {
                    val groups = it.toObject(GroupModel::class.java)
                    if(groups != null){
                        arrayData.add(groups)
                    }
                }
                _mutbaleDataGroup.value = arrayData
            }
        }
    }
    /** [ END ] Menampilkan Group sesuai dengan member user **/


    /** [ START ] Upload file ke storage, Mengambil link dari file **/
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
    /** [ END ] Upload file ke storage, Mengambil link dari file **/

    internal var getAllData: MutableLiveData<ArrayList<GroupModel>>
    get() {return _mutbaleDataGroup}
    set(value) {
        _mutbaleDataGroup = value}

}