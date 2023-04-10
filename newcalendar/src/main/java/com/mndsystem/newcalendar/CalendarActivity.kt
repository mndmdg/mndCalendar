package com.mndsystem.newcalendar

import android.content.Intent
import android.graphics.Rect
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.HandlerCompat.postDelayed
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mndsystem.newcalendar.databinding.ActivityCalendarBinding
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
open class CalendarActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCalendarBinding.inflate(layoutInflater) }
    private val calendar = Calendar.getInstance()
    private val sdf = SimpleDateFormat("yyyy - MM", Locale("ko", "KR"))

    override fun onRestart() {
        super.onRestart()
        getCalendarData("null")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 하단 내비게이션 바 삭제
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        // 현재 날짜 들고오기
        calendar.time = Date()
        binding.tvTitle.text = sdf.format(calendar.time)
        getCalendarData("first")
//        리사이클러뷰에 데이터 넣기

        with(binding) {
            // 날짜 변경
            // PREV 버튼 클릭
            btnPrev.setOnClickListener {
                calendar.add(Calendar.MONTH, -1)
                getCalendarData("null")
                tvTitle.text = sdf.format(calendar.time)
            }
            // NEXT 버튼 클릭
            btnNext.setOnClickListener {
                calendar.add(Calendar.MONTH, 1)
                getCalendarData("null")
                tvTitle.text = sdf.format(calendar.time)
            }
        }
        // TODAY 버튼 클릭
        binding.btnToday.setOnClickListener {
            calendar.time = Date()
            binding.tvTitle.text = sdf.format(calendar.time)
            getCalendarData("null")
            val y = binding.recyclerViewMain.getChildAt(calendar.get(Calendar.DAY_OF_MONTH) - 1).y
            binding.nestedScrollView.post(Runnable {
                binding.nestedScrollView.fling(0)
                binding.nestedScrollView.smoothScrollTo(0, y.toInt())
            })

        }
        // 등록하기
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getCalendarData(mod:String) {
        val api = APIS.create()
        val data = mutableListOf<CalendarData>()
        val datArray = mutableListOf<String>()
        api.getCalendarData(
            calendar.get(Calendar.YEAR).toString(),
            (calendar.get(Calendar.MONTH) + 1).toString()
        )
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("TAG", "${response.body()}")
                    val jsonArray = JSONArray(response.body())
                    for (i in 0 until jsonArray.length() - 1) {
                        val jObject = jsonArray.getJSONObject(i)
                        val sdx = jObject.getString("sdx")
                        val dat = jObject.getString("dat")
                        val tit = jObject.getString("tit")
                        val dam = jObject.getString("dam")
                        val big = jObject.getString("big")
                        data.add(CalendarData(sdx, dat, tit, dam, big))
                        datArray.add(dat)
                    }
                    runOnUiThread {
                        val propPool = RecyclerView.RecycledViewPool()
                        binding.recyclerViewMain.setRecycledViewPool(propPool)
                        binding.recyclerViewMain.adapter =
                            DayAdapter(data, datArray.distinct(), this@CalendarActivity)
                        binding.recyclerViewMain.setItemViewCacheSize(datArray.distinct().size)
                        binding.recyclerViewMain.isNestedScrollingEnabled = true
                        val layoutManager = LinearLayoutManager(
                            this@CalendarActivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        layoutManager.recycleChildrenOnDetach = true
                        binding.recyclerViewMain.layoutManager = layoutManager
                        if (mod=="first"){
                            calendar.time
                            val idx = calendar.get(Calendar.DAY_OF_MONTH) - 1
                            val handler = Handler()
                            handler.postDelayed({
                                val yy = binding.recyclerViewMain.getChildAt(idx).y
                                binding.nestedScrollView.smoothScrollTo(0, yy.toInt())
                            }, 1500)
                        }
                    }

                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("통신에러", "onFailure: ${t.message}")
                }
            }
            )
    }

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
}

