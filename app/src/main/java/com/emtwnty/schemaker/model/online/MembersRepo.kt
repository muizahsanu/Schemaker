package com.emtwnty.schemaker.model.online

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

object MembersRepo {

    private var mDatabase = Firebase.firestore
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var RESULT_SEARCH: MutableLiveData<String> = MutableLiveData()

    fun getUserData(groupID: String): LiveData<List<UsersModel>>{
        return object: LiveData<List<UsersModel>>(){
            override fun onActive() {
                super.onActive()
                mDatabase.collection("groups").document(groupID)
                    .collection("members").addSnapshotListener { valueMemberGrup, error ->
                        val docsMember = valueMemberGrup?.documents
                        docsMember?.forEach { docMember->
                            val userId = docMember.id
                            println("membersID_group => ${userId}")
                            val usersRef = mDatabase.collection("users").whereEqualTo("uid",userId)
                            usersRef.addSnapshotListener { valueUser, error ->
                                val arrayUsers = ArrayList<UsersModel>()
                                val userDocs = valueUser?.documents
                                userDocs?.forEach {docUser->
                                    val userData = docUser.toObject(UsersModel::class.java)
                                    println("membersID_group => ${userId}")
                                    arrayUsers.add(userData!!)
                                }
                                value = arrayUsers
                            }
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

}