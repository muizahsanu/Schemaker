package com.emtwnty.schemaker.model.online

data class GroupScheModel (
    val scheduleID: String = "",
    val title: String = "",
    val description: String = "",
    val timestamp: Long = 0,
    val groupID:String = "",
    val remindMe: Boolean = false,
    val done: Boolean = false
)