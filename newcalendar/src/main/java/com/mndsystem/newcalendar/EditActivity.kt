package com.mndsystem.newcalendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.mndsystem.newcalendar.databinding.ActivityEditBinding
import com.mndsystem.newcalendar.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEditBinding.inflate(layoutInflater) }
    private val api = APIS.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val dat = intent.getStringExtra("dat").toString()
        val tit = intent.getStringExtra("tit").toString()
        val big = intent.getStringExtra("big").toString()
        val dam = intent.getStringExtra("dam").toString()
        // 값 받아오기
        with(binding) {
            editDate.setText(dat)
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
                        delete(dat,tit,dam,big)
                        finish()
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }.show()

            }
            // 수정
            btnEdit.setOnClickListener { }
        }

    }

    private fun delete(dat:String,tit:String,dam:String,big:String) {
        api.deleteSchedule(dat, tit, dam, big).enqueue(object : Callback<String> {
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
}