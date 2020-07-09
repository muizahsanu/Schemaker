package com.emtwnty.schemaker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.emtwnty.schemaker.model.online.UsersModel
import com.emtwnty.schemaker.model.online.UsersRepo

class UsersViewModel(app: Application): AndroidViewModel(app) {
    private var repo: UsersRepo = UsersRepo


    fun setData(usersModel: UsersModel){
        repo.setData(usersModel)
    }

    fun getUserDataByID(uid:String) = repo.getUserDataByID(uid)
}