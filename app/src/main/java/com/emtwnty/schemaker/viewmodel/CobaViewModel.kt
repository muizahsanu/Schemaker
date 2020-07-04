package com.emtwnty.schemaker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.emtwnty.schemaker.model.online.CobaRepo

class CobaViewModel(application: Application): AndroidViewModel(application) {

    private var repo: CobaRepo = CobaRepo

    fun getData() = repo.getData()

}