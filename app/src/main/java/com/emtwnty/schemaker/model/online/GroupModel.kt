package com.emtwnty.schemaker.model.online

data class GroupModel (
    val groupID: String = "",
    val groupName: String = "",
    val groupDesc: String = "",
    val groupImage: String = "",
    val members: Map<String,Boolean> = hashMapOf(),
    val role: Map<String,String> = hashMapOf()
)