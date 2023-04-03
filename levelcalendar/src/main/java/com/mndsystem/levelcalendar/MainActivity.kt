package com.mndsystem.levelcalendar

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.os.HandlerCompat.postDelayed
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mndsystem.levelcalendar.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

@RequiresApi(Build.VERSION_CODES.N)
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val TAG = "로그"
    private val calendar = Calendar.getInstance()
    private val sdf = SimpleDateFormat("yyyy - MM", Locale("ko", "KR"))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        // 현재 날짜 들고오기
        calendar.time = Date()
        binding.tvTitle.text = sdf.format(calendar.time)
        getCalendarData()
//        리사이클러뷰에 데이터 넣기

        with(binding) {
            // 날짜 변경
            // PREV 버튼 클릭
            btnPrev.setOnClickListener {
                calendar.add(Calendar.MONTH, -1)
                getCalendarData()
                tvTitle.text = sdf.format(calendar.time)
            }
            // NEXT 버튼 클릭
            btnNext.setOnClickListener {
                calendar.add(Calendar.MONTH, 1)
                getCalendarData()
                tvTitle.text = sdf.format(calendar.time)
            }
        }
        // TODAY 버튼 클릭
        binding.btnToday.setOnClickListener {
            calendar.time = Date()
            binding.tvTitle.text = sdf.format(calendar.time)
            getCalendarData()
            val y = binding.recyclerViewMain.getChildAt(calendar.get(Calendar.DAY_OF_MONTH) - 1).y
            binding.nestedScrollView.post(Runnable {
                binding.nestedScrollView.fling(0)
                binding.nestedScrollView.scrollTo(0, y.toInt())
            })

        }
        // 등록하기
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }


    }

    private fun getCalendarData() {
        val api = APIS.create()
        val data = mutableListOf<CalendarData>()
        val datArray = mutableListOf<String>()
        api.getCalendarData(
            calendar.get(Calendar.YEAR).toString(),
            (calendar.get(Calendar.MONTH) + 1).toString()
        )
            .enqueue(object : Callback<String> {
                @RequiresApi(Build.VERSION_CODES.O)
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
                        data.add(CalendarData(sdx,dat, tit, dam, big))
                        datArray.add(dat)
                    }
                    runOnUiThread {
                        val propPool = RecyclerView.RecycledViewPool()
                        binding.recyclerViewMain.setRecycledViewPool(propPool)
                        binding.recyclerViewMain.adapter =
                            DayAdapter(data, datArray.distinct(), this@MainActivity)
                        binding.recyclerViewMain.setItemViewCacheSize(datArray.distinct().size)
                        binding.recyclerViewMain.isNestedScrollingEnabled = true
                        val layoutManager = LinearLayoutManager(
                            this@MainActivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        layoutManager.recycleChildrenOnDetach = true
                        binding.recyclerViewMain.layoutManager = layoutManager

                    }

                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("통신에러", "onFailure: ${t.message}")
                }
            }
            )
    }
}