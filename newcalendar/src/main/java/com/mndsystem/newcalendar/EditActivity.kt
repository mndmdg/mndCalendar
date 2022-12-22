package com.mndsystem.newcalendar

import android.app.DatePickerDialog
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.mndsystem.newcalendar.databinding.ActivityEditBinding
import com.mndsystem.newcalendar.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.nio.file.Files.delete
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEditBinding.inflate(layoutInflater) }
    private val api = APIS.create()
    // 바깥쪽 터치시 키보드 내려가기
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val focusView = currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val sdx = intent.getStringExtra("sdx").toString()
        val dat = intent.getStringExtra("dat").toString()
        val tit = intent.getStringExtra("tit").toString()
        val big = intent.getStringExtra("big").toString()
        val dam = intent.getStringExtra("dam").toString()
        // 값 받아오기
        with(binding) {
            // 날짜선택
            editDate.setOnClickListener {
                val today = GregorianCalendar()
                val year = today.get(Calendar.YEAR)
                val month = today.get(Calendar.MONTH)
                val date = today.get(Calendar.DATE)
                val cal = Calendar.getInstance()

                val dig = DatePickerDialog(this@EditActivity,
                    {view,year,month,dayOfmonth->
                        cal.set(year, month, date)
                        binding.editDate.text =SimpleDateFormat("yyyy-MM-dd").format(cal.time)
                    },year,month,date
                )
                dig.show()
            }
            editDate.text = dat
            editTit.setText(tit)
            editBig.setText(big)
            editDam.setText(dam)
            // 취소
            btnCancel.setOnClickListener {
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
            // 삭제
            btnRemove.setOnClickListener {
                AlertDialog.Builder(this@EditActivity)
                    .setTitle("알림")
                    .setMessage("일정을 삭제 할까요 ?")
                    .setNegativeButton("취소") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("확인") { dialog, _ ->
                        delete(sdx)
                        finish()
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }.show()

            }
            // 수정
            btnEdit.setOnClickListener {

                AlertDialog.Builder(this@EditActivity)
                    .setTitle("알림")
                    .setMessage("수정하실까요 ?")
                    .setPositiveButton("확인") { dialog, _ ->
                        val editDat = editDate.text.toString()
                        val editTit = editTit.text.toString()
                        val editDam = editDam.text.toString()
                        val editBig = editBig.text.toString()
                        update(sdx,editDat,editTit,editDam,editBig)
                        finish()
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                    }
                    .setNegativeButton("취소") { dialog, _ ->
                        dialog.dismiss()
                    }.show()




            }
        }

    }

    private fun delete(sdx:String) {
        api.deleteSchedule(sdx).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                runOnUiThread {
                    val body = response.body().toString()
                    if(body.contains("success")) {
                        Toast.makeText(this@EditActivity, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@EditActivity, "실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                runOnUiThread {
                }
            }
        })
    }

    private fun update(sdx:String,dat:String,tit:String,dam:String,big:String){
        api.updateSchedule(sdx,dat,tit,dam,big).enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.body().toString().contains("success")){
                    Toast.makeText(this@EditActivity, "수정되었습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@EditActivity, "수정실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@EditActivity, "통신실패", Toast.LENGTH_SHORT).show()
            }
        })

    }
}