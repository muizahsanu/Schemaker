package com.emtwnty.schemaker.model.online

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.collections.ArrayList

object MembersRepo {

    private var mDatabase = Firebase.firestore
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var RESULT_SEARCH: MutableLiveData<String> = MutableLiveData()

    private var _mutableUserData: MutableLiveData<ArrayList<UsersModel>> = MutableLiveData()

    private fun getUserData(groupID: String){
        mDatabase.collection("groups").document(groupID)
            .collection("members").addSnapshotListener { valueMemberGrup, error ->
                val docsMember = valueMemberGrup?.documents
                val arrayUsers = ArrayList<UsersModel>()
                docsMember?.forEach { docMember->
                    val userId = docMember.id
                    println("membersID_group => ${userId}")
                    val usersRef = mDatabase.collection("users").whereEqualTo("uid",userId)
                    usersRef.addSnapshotListener { valueUser, error ->
                        val userDocs = valueUser?.documents
                        userDocs?.forEach {docUser->
                            val userData = docUser.toObject(UsersModel::class.java)
                            println("membersID_group => ${userId}")
                            arrayUsers.add(userData!!)
                        }
                    }
                    _mutableUserData.value = arrayUsers
                }
            }
    }
    fun initGetUserData(groupID: String){
        getUserData(groupID)
    }
    fun resetMutableUserData(){
        _mutableUserData.postValue(null)
    }

    fun leaveGroup(groupID: String){
        val userID = mAuth.currentUser?.uid.toString()
        mDatabase.collection("users").document(userID)
            .collection("groups").document(groupID).delete()
            .addOnCompleteListener {
                println("berhasil keluar grup")
                if(it.isSuccessful){
                    mDatabase.collection("groups").document(groupID)
                        .collection("members").document(userID).delete()
                        .addOnCompleteListener {
                            if(it.isSuccessful){
                                println("berhasil hapus member")
                            }
                        }
                }
            }
    }

    fun kickMember(groupID: String,userID: String){
        mDatabase.collection("users").document(userID)
            .collection("groups").document(groupID).delete()
            .addOnCompleteListener {
                println("berhasil keluar grup")
                if(it.isSuccessful){
                    mDatabase.collection("groups").document(groupID)
                        .collection("members").document(userID).delete()
                        .addOnCompleteListener {
                            if(it.isSuccessful){
                                println("berhasil hapus member")
                            }
                        }
                }
            }
    }

    fun findUserByUsernam(username: String): LiveData<UsersModel>{
        return object: LiveData<UsersModel>(){
            override fun onActive() {
                super.onActive()
                mDatabase.collection("users").whereEqualTo("username",username)
                    .get().addOnSuccessListener { usersSnapshot ->
                        if(usersSnapshot == null){
                            RESULT_SEARCH.value = "KOSONG"
                            return@addOnSuccessListener
                        }
                            val userDocuments = usersSnapshot.documents
                            userDocuments.forEach {userDocSnapshot->
                                val userData = userDocSnapshot.toObject(UsersModel::class.java)
                                value = userData
                                RESULT_SEARCH.value = "BERHASIL"
                            }

                    }.addOnFailureListener {
                        RESULT_SEARCH.value = it.toString()
                    }
            }
        }
    }

    fun inviteUser(otherUserID: String,userID: String, groupID: String){
        val usersRef = mDatabase.collection("users").document(otherUserID)
            .collection("request").document(groupID)
        val dataInvite = InviteModel(groupID,userID,groupID)
        usersRef.set(dataInvite)
    }

    internal var getAllUserData: MutableLiveData<ArrayList<UsersModel>>
        get() {return _mutableUserData
        }
        set(value) {
            _mutableUserData = value}

}