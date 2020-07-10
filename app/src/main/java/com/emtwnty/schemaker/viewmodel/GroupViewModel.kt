package com.emtwnty.schemaker.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.emtwnty.schemaker.model.online.GroupListener
import com.emtwnty.schemaker.model.online.GroupModel
import com.emtwnty.schemaker.model.online.GroupRepo

class GroupViewModel(app: Application):AndroidViewModel(app) {

    private var repo: GroupRepo = GroupRepo
    private var groupListener: GroupListener? = null

    fun addGroup(groupMap: HashMap<String,Any>){
        repo.addGroup(groupMap)
    }

    fun getAllGroup():LiveData<List<GroupModel>> = repo.getAllGroup()

    fun result():LiveData<String> = repo.responseCallback

    fun uploadImage(imageUri: Uri, groupID:String):LiveData<String>{
        return repo.getImageURL(imageUri,groupID)
    }
}