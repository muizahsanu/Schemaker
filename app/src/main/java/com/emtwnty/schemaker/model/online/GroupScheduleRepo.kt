package com.emtwnty.schemaker.model.online

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
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

    fun addGroupSchedule(scheduleOnlineModel: ScheduleOnlineModel){
        CoroutineScope(IO).launch {
            addGroupScheduleGB(scheduleOnlineModel)
        }
    }

    private suspend fun addGroupScheduleGB(scheduleOnlineModel: ScheduleOnlineModel){
        withContext(Main){
            scheduleResponseCallback.value = "ADDSCEDULE_PROCCESSING"
        }
        withContext(IO){
            val groupID = scheduleOnlineModel.groupID
            val scheduleID = scheduleOnlineModel.scheduleID
            mDatabase.collection("groups").document(groupID)
                .collection("schedules").document(scheduleID)
                .set(scheduleOnlineModel).addOnCompleteListener {
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
}