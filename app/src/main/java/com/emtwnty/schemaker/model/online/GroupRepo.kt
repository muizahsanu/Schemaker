package com.emtwnty.schemaker.model.online

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.tasks.await

object GroupRepo {

    private var mDatabase = Firebase.firestore
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var responseCallback: MutableLiveData<String> = MutableLiveData()
    private var mStorage: FirebaseStorage = Firebase.storage

    private var mutbaleDataGroup: MutableLiveData<ArrayList<GroupModel>> = MutableLiveData()

    init {
        if(mutbaleDataGroup.value == null || mutbaleDataGroup.value.toString() == "null"){
            getGroupData()
        }
    }

    /** [ START ] Delete Group **/
    fun deleteGroupByID(groupID: String){
        val userID = mAuth.currentUser?.uid

        mDatabase.collection("groups").document(groupID)
            .delete()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    mDatabase.collection("users").document(userID!!)
                        .update("groups.$groupID",FieldValue.delete())
                }
            }
    }
    /** [ END ] Delete Group **/



    /** [ START ] Menambah Group Mamber **/
    fun addMemberGroup(groupID: String){
        responseCallback.value = "PROSES_ADDMEMBER"
        CoroutineScope(IO).launch {
            addMemberGroupBG(groupID)
        }
    }

    private suspend fun addMemberGroupBG(groupID: String){
        withContext(IO){
            val userID = mAuth.currentUser?.uid.toString()
            val usersRef = mDatabase.collection("users").document(userID)
            val groupsRef = mDatabase.collection("groups").document(groupID)

            // Menambah grouplist di users
            usersRef.update("groups.$groupID",true)
                .addOnCompleteListener { userTask->
                    if (userTask.isSuccessful){
                        groupsRef.update("members.$userID",true)
                        groupsRef.update("role.$userID","genin")
                            .addOnCompleteListener { groupTask->
                                if (groupTask.isSuccessful)
                                    responseCallback.value = "FINISH_JOIN_GROUP"
                            }
                    }
                }
        }
        withContext(Main){
            responseCallback.value = ""
        }
    }
    /** [ END ] Menambah Group Mamber **/



    /** [ START ] Menambahkan / membuat group **/
    fun addGroup(groupModel: GroupModel){
        responseCallback.value = "RUNNING"
        CoroutineScope(IO).launch {
            addGroupBG(groupModel)
        }
    }
    private suspend fun addGroupBG(groupModel: GroupModel){
        delay(1500)
        val groupID = groupModel.groupID
        val userID = mAuth.currentUser?.uid

        mDatabase.collection("groups").document(groupID)
            .set(groupModel)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    mDatabase.collection("users").document(userID!!)
                        .update("groups.$groupID",true)
                    responseCallback.value = "FINISH"
                }else responseCallback.value = it.exception.toString()

            }
        withContext(Main){
            responseCallback.value = ""
        }
    }
    /** [ END ] Menambahkan / membuat group **/


    /** [ START ] Update Group **/
    fun updateGroup(groupID: String,newDataGroup: Map<String,Any>){
        CoroutineScope(IO).launch {
            updateGroupBG(groupID,newDataGroup)
        }
    }
    private suspend fun updateGroupBG(groupID: String,newDataGroup: Map<String,Any>){
        withContext(Main){
            responseCallback.value = "UPDATING"
        }
        withContext(IO){
            mDatabase.collection("groups").document(groupID)
                .update(newDataGroup).addOnCompleteListener {
                    if(it.isSuccessful){
                        responseCallback.value = "FINISH"
                    }
                }.await()
        }
        withContext(Main){
            responseCallback.value = ""
        }
    }
    /** [ END ] Update Group **/



    /** [ START ] Mengambil user role **/
    fun getUserRole(userID: String, groupID: String):LiveData<String>{
        return object : LiveData<String>() {
            override fun onActive() {
                super.onActive()
                CoroutineScope(IO).launch {
                    val role = mDatabase.collection("groups").document(groupID)
                        .get().await().get("role.$userID").toString()
                    CoroutineScope(Main).launch {
                        value = role
                    }
                }
            }
        }
    }
    /** [ END ] Mengambil user role **/



    /** [ START ] Menampilkan Group sesuai dengan member user **/
    private fun getGroupData() {
        println("Group_data => Halo")
        val currentUser = mAuth.currentUser

        if (currentUser != null || currentUser?.uid.toString().isNotEmpty()) {
            println("Group_data => mems")

            val userID = currentUser?.uid
            mDatabase.collection("groups").whereEqualTo("members.$userID",true)
                .addSnapshotListener { value, iniError ->
                    if(value == null){
                        return@addSnapshotListener
                    }
                    val arrayGroup = ArrayList<GroupModel>()
                    val docGroup = value.documents
                    for (docSnapshot in docGroup){
                        val dataGroup = docSnapshot.toObject(GroupModel::class.java)
                        println("Group_data111 => $dataGroup")
                        arrayGroup.add(dataGroup!!)
                    }
                    mutbaleDataGroup.value = arrayGroup
                    println("Get_group_error => ${iniError?.cause.toString()}")
                }
        }
    }
    fun initGetGroupData(){
        getGroupData()
    }
    fun resetMutableGetGroup(){
        mutbaleDataGroup.postValue(null)
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
                    imageRef.putFile(imageUri).addOnCompleteListener { task ->
                        if(task.isSuccessful){
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
    get() {return mutbaleDataGroup}
    set(value) {
        mutbaleDataGroup = value}

}