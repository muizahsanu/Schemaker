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

    fun memberDataFromGroup(groupID: String): LiveData<List<UsersModel>>{
        return memberRepo.getUserData(groupID)
    }

}