package com.emtwnty.schemaker.model.online

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object GroupScheduleRepo {

    private var mDatabase = FirebaseFirestore.getInstance()
    var scheduleResponseCallback: MutableLiveData<String> = MutableLiveData()
    private var mAuth:FirebaseAuth = FirebaseAuth.getInstance()

    private var mutableGroupSche: MutableLiveData<ArrayList<GroupScheModel>> = MutableLiveData()


    /** [ START ] Delete Schedule **/
    fun deleteSchedule(scheduleID: String){
        CoroutineScope(IO).launch {
            deleteScheduleBG(scheduleID)
        }
    }
    private suspend fun deleteScheduleBG(scheduleID: String){
        withContext(IO){
            mDatabase.collection("schedules").document(scheduleID)
                .delete().await()
        }
    }
    /** [ END ] Delete Schedule **/


    /** [ START ] Update Schedule **/
    fun updateGroupSche(scheduleID: String, newSchedule: Map<String,Any>){
        CoroutineScope(IO).launch {
            updateGroupScheBG(scheduleID,newSchedule)
        }
    }
    private suspend fun updateGroupScheBG(scheduleID: String, newSchedule: Map<String,Any>){
        withContext(IO){
            mDatabase.collection("schedules").document(scheduleID)
                .update(newSchedule).await()
        }
    }
    /** [ END ] Update Schedule **/


    /** [ START ] Add New Schedule **/
    fun addGroupSchedule(groupScheModel: GroupScheModel){
        CoroutineScope(IO).launch {
            addGroupScheduleGB(groupScheModel)
        }
    }
    private suspend fun addGroupScheduleGB(groupScheModel: GroupScheModel){
        withContext(Main){
            scheduleResponseCallback.value = "ADDSCEDULE_PROCCESSING"
        }
        withContext(IO){
            val scheduleID = groupScheModel.scheduleID
            mDatabase.collection("schedules").document(scheduleID)
                .set(groupScheModel).addOnCompleteListener {
                    if(it.isSuccessful){
                        scheduleResponseCallback.value = "ADDSCEDULE_SUCCESS"
                    }
                    else{
                        scheduleResponseCallback.value = "ADDSCEDULE_FAILED: ${it.exception}"
                    }
                }.await()

        }
        withContext(Main){
            scheduleResponseCallback.value = ""
        }
    }
    /** [ END ] Add New Schedule **/


    /** [ START ] Get Schedule **/
    private fun getGroupScheData(groupID: String){
        mDatabase.collection("schedules").whereEqualTo("groupID",groupID)
            .addSnapshotListener { value, _ ->
                if(value != null){
                    val arraySche = ArrayList<GroupScheModel>()
                    val scheDoc = value.documents
                    for(scheSnapshot in scheDoc){
                        val dataSche = scheSnapshot.toObject(GroupScheModel::class.java)
                        println("data_schedule => $dataSche")
                        arraySche.add(dataSche!!)
                    }
                    mutableGroupSche.value = arraySche
                    println("data_schedulemutableGroupSch => ${mutableGroupSche.value}")
                }
            }
    }
    fun initGetGroupSche(groupID: String){
        getGroupScheData(groupID)
    }
    internal var getAllGroupSche: MutableLiveData<ArrayList<GroupScheModel>>
    get() {return mutableGroupSche}
    set(value){
        mutableGroupSche = value
    }
    /** [ END ] Get Schedule **/
}