package com.emtwnty.schemaker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emtwnty.schemaker.model.online.MembersRepo
import com.emtwnty.schemaker.model.online.UsersModel
import java.net.IDN

class MembersViewModel(app: Application): AndroidViewModel(app) {

    private var memberRepo: MembersRepo

    init {
        memberRepo = MembersRepo
    }

    fun findUserByUsername(username: String): LiveData<UsersModel>{
        return memberRepo.findUserByUsernam(username)
    }

    fun sendRequestGroup(otherUserID: String,userID: String,groupID: String){
        memberRepo.inviteUser(otherUserID,userID,groupID)
    }

    fun initGetUserData(groupID: String){
        memberRepo.initGetUserData(groupID)
    }

    fun getAllUserData(): MutableLiveData<ArrayList<UsersModel>>{
        return memberRepo.getAllUserData
    }

    fun leaveGroup(groupID: String){
        memberRepo.leaveGroup(groupID)
    }

}