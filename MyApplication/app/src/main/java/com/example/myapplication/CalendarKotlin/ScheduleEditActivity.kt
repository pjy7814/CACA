package com.example.myapplication.CalendarKotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.Toast
import com.example.myapplication.Home_Board.HomeActivity
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_schedule_edit.*
import kotlinx.android.synthetic.main.board.*
import java.lang.IllegalArgumentException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ScheduleEditActivity : AppCompatActivity(), DatePickerFragment.OnDateSelectedListener, TimePickerFragment.OnTimeSelectedListener, SaveConfirmFragment.SaveListener, DeleteConfirmFragment.DeleteListener{

    private var editStartDateFlag = true
    private var editStartTimeFlag = true
    lateinit var scheduleId : String
    lateinit var FamilyName : String

    var fbAuth = FirebaseAuth.getInstance() // 로그인
    var fbFire = FirebaseFirestore.getInstance()
    var uid = fbAuth?.uid.toString() // uid
    val db: FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_edit)


        FamilyName = intent.getStringExtra("FamilyName").toString() // FamilyName
        scheduleId = intent.getStringExtra("selected_date").toString() // Date


        startDateEdit.setOnClickListener {
            editStartDateFlag = true
            val dialog = DatePickerFragment()
            dialog.show(supportFragmentManager, "startDate_dialog")
        }
        endDateEdit.setOnClickListener {
            editStartDateFlag = false
            val dialog = DatePickerFragment()
            dialog.show(supportFragmentManager, "endDate_dialog")
        }
        startTimeEdit.setOnClickListener {
            editStartTimeFlag = true
            val dialog = TimePickerFragment()
            dialog.show(supportFragmentManager, "startTime_dialog")
        }
        endTimeEdit.setOnClickListener {
            editStartTimeFlag = false
            val dialog = TimePickerFragment()
            dialog.show(supportFragmentManager, "endTime_dialog")
        }


        saveButton.setOnClickListener {view:View-> // 저장!
            val startDateTime = (startDateEdit.text.toString() + " "+ startTimeEdit.text.toString())
                .toDate()?.time
            val endDateTime = (endDateEdit.text.toString() + " " + endTimeEdit.text.toString())
                .toDate()?.time
            if (startDateTime != null && endDateTime != null) {
                if(startDateTime <= endDateTime){
                    val dialog = SaveConfirmFragment()
                    dialog.show(supportFragmentManager, "saveConfirm_dialog")
                }else{
                    val dialog = InvalidTimeFragment()
                    dialog.show(supportFragmentManager, "invalidTime_dialog")
                }
            }else{
                onSave()
            }
        }
        deleteButton.setOnClickListener {view:View-> // 삭제
            val dialog = DeleteConfirmFragment()
            dialog.show(supportFragmentManager, "deleteConfirm_dialog")
        }

    }

    private fun String.toDate(pattern:String = "yyyy/MM/dd HH:mm"): Date?{
        return try{
            SimpleDateFormat(pattern).parse(this)
        }catch (e:IllegalArgumentException){
            return null
        }catch (e:ParseException){
            return null
        }
    }

    override fun onSelected(year: Int, month: Int, date: Int) {
        val c = Calendar.getInstance()
        c.set(year, month, date)
        if(editStartDateFlag){
            startDateEdit.setText(DateFormat.format("yyyy/MM/dd", c))
        }else{
            endDateEdit.setText(DateFormat.format("yyyy/MM/dd", c))
        }
    }
    override fun onSelected(hourOfDay: Int, minute: Int) {
        if(editStartTimeFlag){
            startTimeEdit.setText("%1$02d:%2$02d".format(hourOfDay, minute))
        }else{
            endTimeEdit.setText("%1$02d:%2$02d".format(hourOfDay, minute))
        }
    }

    override fun onSave() {

        var calendar_content = hashMapOf(
            "title" to titleEdit.text.toString(),
            "place" to placeEdit.text.toString(),
            "detail" to detailEdit.text.toString(),
            "start_date" to startDateEdit.text.toString(),
            "start_time" to startTimeEdit.text.toString(),
            "end_date" to endDateEdit.text.toString(),
            "end_time" to endTimeEdit.text.toString(),
        )

        db.collection("Chats").document(FamilyName.toString()).collection("CALENDAR")
            .document(scheduleId.toString()).set(calendar_content as Map<String, Any>)// DB
        Toast.makeText(this, "${scheduleId} 일정 업로드 완료!", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onDelete() {
//        if(scheduleId != -1L){
////            realm.executeTransaction {db:Realm->
////                db.where<Schedule>().equalTo("id", scheduleId)
////                    .findFirst()?.deleteFromRealm()
////            }
//        }
//        val intent = Intent(this, HomeActivity::class.java)
//        startActivity(intent)
    }

        override fun onDestroy() {
        super.onDestroy()
    }
}
