package com.mndsystem.levelcalendar

import android.Manifest
import android.accounts.AccountManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.mndsystem.levelcalendar.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
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
        setTheme(R.style.Theme_Calender)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            // 로그인 부분
            if (binding.editPhone.text.isNotEmpty()&&binding.editPassword.text.isNotEmpty()) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("number", binding.editPhone.text.toString())
                intent.putExtra("pwd", binding.editPassword.text.toString())
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }else{
                AlertDialog.Builder(this)
                    .setMessage("알림")
                    .setMessage("로그인정보가 확인되지 않습니다.")
                    .setPositiveButton("확인") { dialog, _ ->
                        dialog.dismiss()
                        binding.editPhone.requestFocus()
                    }
            }
        }



    }
}