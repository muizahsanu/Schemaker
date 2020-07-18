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
    private var mStorage: FirebaseStorage = Firebase.storage

    private var _mutbaleDataGroup: MutableLiveData<ArrayList<GroupModel>> = MutableLiveData<ArrayList<GroupModel>>()
    private var _mutableGroupScedule: MutableLiveData<ArrayList<ScheduleOnlineModel>> = MutableLiveData()

    init {
        if(_mutbaleDataGroup.value == null || _mutbaleDataGroup.value.toString() == "null"){
            getGroupData()
        }
    }

    /** [ START ] Mengambil Group Schedule  **/

    private fun getScheduleGroup(groupID: String) {
        println("Ngeget ke database")
        mDatabase.collection("groups").document(groupID)
            .collection("schedules").get().addOnSuccessListener {
                if (it != null) {
                    val scheduleArray = ArrayList<ScheduleOnlineModel>()
                    val scheDoc = it.documents
                    for (scheSnapshoot in scheDoc) {
                        val scheData = scheSnapshoot.toObject(ScheduleOnlineModel::class.java)
                        if (scheData != null) {
                            scheduleArray.add(scheData)
                        }
                    }
                    _mutableGroupScedule.value = scheduleArray
                }
            }
    }
    fun initGetScheduleGroup(groupID: String){
        getScheduleGroup(groupID)
    }

    internal var getAllDataSchedule: MutableLiveData<ArrayList<ScheduleOnlineModel>>
    get() {return _mutableGroupScedule}
    set(value) {
        _mutableGroupScedule = value
    }

    /** [ END ] Mengambil Group Schedule  **/

    /** [ START ] Menambah Group Mamber **/
    fun addMemberGroup(groupID: String){
        CoroutineScope(IO).launch {
            addMemberGroupBG(groupID)
        }
    }

    private suspend fun addMemberGroupBG(groupID: String){
        withContext(IO){
            val userID = mAuth.currentUser?.uid.toString()
            val newMemberMap = HashMap<String,Any>()
            newMemberMap.put("role","genin")
            mDatabase.collection("groups").document(groupID)
                .collection("members").document(userID).set(newMemberMap)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        mDatabase.collection("users").document(userID)
                            .collection("groups").document(groupID).set(newMemberMap)
                    }
                }
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
        val userID = mAuth.currentUser?.uid.toString()
        val groupID = groupModel.groupID
        // ref
        val groupRef = mDatabase.collection("groups").document(groupID)
        val userRef = mDatabase.collection("users").document(userID)
            .collection("groups").document(groupID)
        val membersOnGroup = HashMap<String,Any>()
        membersOnGroup.put("role","hokage")
        val groupsInUsers = HashMap<String,Any>()
        groupsInUsers.put("role","hokage")

        groupRef.set(groupModel).addOnCompleteListener {
            if(it.isSuccessful){
                groupRef.collection("members").document(userID).set(membersOnGroup)
                userRef.set(groupsInUsers)
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



    /** [ START ] Menampilkan group sesuai ID **/

    fun getGroupByID(groupID: String): LiveData<GroupModel>{
        return object : LiveData<GroupModel>(){
            override fun onActive() {
                super.onActive()
                mDatabase.collection("groups").document(groupID).get()
                    .addOnSuccessListener {
                        value = it.toObject(GroupModel::class.java)
                    }
            }
        }
    }
    /** [ END ] Menampilkan group sesuai ID **/



    /** [ START ] Menampilkan Group sesuai dengan member user **/
    fun getGroupData() {
        println("Group_data => Halo")
        val currentUser = mAuth.currentUser

        if (currentUser != null) {
            println("Group_data => mems")

            val userID = currentUser.uid
            val groupsRef = mDatabase.collection("groups")
            val usersRef = mDatabase.collection("users").document(userID).collection("groups")

            usersRef.addSnapshotListener { value, error ->

                val arrayGroupData = ArrayList<GroupModel>()
                val user_groupsDocs = value?.documents

                user_groupsDocs?.forEach { docSnapshootUser->
                    val groupID = docSnapshootUser.id
                    println("Group_ID => {$groupID}")

                    groupsRef.whereEqualTo("groupID",groupID).get().addOnSuccessListener { docGroup->
                        val groupsDoc = docGroup.documents
                        groupsDoc.forEach {
                            val memek = it.toObject(GroupModel::class.java)
                            arrayGroupData.add(memek!!)
                        }
                        _mutbaleDataGroup.value = arrayGroupData
                    }
                }
            }
        }
    }
    fun resetMutable(){
        _mutbaleDataGroup.postValue(null)
    }
    fun initGetGroupData(){
        getGroupData()
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