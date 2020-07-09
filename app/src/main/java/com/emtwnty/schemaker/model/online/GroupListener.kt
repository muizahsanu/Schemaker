package com.emtwnty.schemaker.model.online

interface GroupListener {
    fun onStarted()
    fun onFinish()
    fun onFailed()
}