package com.emtwnty.schemaker.model.online

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object MembersRepo {

    private var mDatabase = Firebase.firestore
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private var _mutableDataMembers: MutableLiveData<ArrayList<UsersModel>> = MutableLiveData()

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

}