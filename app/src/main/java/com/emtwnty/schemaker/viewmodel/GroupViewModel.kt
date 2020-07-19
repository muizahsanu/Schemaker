package com.emtwnty.schemaker.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emtwnty.schemaker.model.online.GroupModel
import com.emtwnty.schemaker.model.online.GroupRepo

class GroupViewModel(app: Application):AndroidViewModel(app) {

    private var repo: GroupRepo
    init {
        repo = GroupRepo
    }

    fun addGroup(groupModel: GroupModel){
        repo.addGroup(groupModel)
    }
    fun addMemberGroup(groupID: String){
        repo.addMemberGroup(groupID)
    }

    fun deleteGroupByID(groupID: String){
        repo.deleteGroupByID(groupID)
    }
    fun getAllGroup():MutableLiveData<ArrayList<GroupModel>>{
        return repo.getAllData
    }

    fun uploadImage(imageUri: Uri, groupID:String):LiveData<String>{
        return repo.getImageURL(imageUri,groupID)
    }

    fun iniGetGroupData(){
        repo.initGetGroupData()
    }

    fun getUserRole(userID: String,groupID: String):LiveData<String> = repo.getUserRole(userID,groupID)

    fun result():LiveData<String> = repo.responseCallback

}