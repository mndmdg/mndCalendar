package com.mndsystem.newcalendar

import android.app.DatePickerDialog
import android.graphics.Rect
import android.icu.util.LocaleData
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.mndsystem.newcalendar.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private val api = APIS.create()
    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }
    private val sdf = SimpleDateFormat("yyyy - MM", Locale("ko", "KR"))
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
        with(binding) {
            // 날짜선택
            regiDate.setOnClickListener {
                val today = GregorianCalendar()
                val year = today.get(Calendar.YEAR)
                val month = today.get(Calendar.MONTH)
                val date = today.get(Calendar.DATE)
                val cal = Calendar.getInstance()

                val dig = DatePickerDialog(this@RegisterActivity,
                    {view,year,month,dayOfmonth->
                    cal.set(year, month, dayOfmonth)
                    binding.regiDate.text = sdf.format(cal.time)
                },year,month,date
                )
                dig.show()
            }
            if (intent.getStringExtra("dat")==null){

                val date = sdf.format(Date())
                regiDate.text = date
            }else{
                regiDate.text = intent.getStringExtra("dat")
            }


            // 등록
            btnSave.setOnClickListener {
                AlertDialog.Builder(this@RegisterActivity)
                    .setTitle("알림")
                    .setMessage("일정을 등록할까요 ?")
                    .setNegativeButton("취소") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("확인") { dialog, _ ->
                        register()
                        dialog.dismiss()
                        finish()
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }.show()

            }
            //취소
            btnCancelRegi.setOnClickListener {
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }

    }

    private fun register() {

        with(binding) {
            val dat = regiDate.text.toString()
            val tit = regiTit.text.toString()
            val big = regiBig.text.toString()
            val dam = regiDam.text.toString()
            api.registerSchedule(dat, tit, dam, big).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    runOnUiThread {
                        val body = response.body().toString()
                        if (body.contains("success")) {
                            Toast.makeText(this@RegisterActivity, "삭제되었습니다.", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(this@RegisterActivity, "실패하였습니다.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("TAG", "onFailure: ${t.message}")
                }
            })

        }
    }
}