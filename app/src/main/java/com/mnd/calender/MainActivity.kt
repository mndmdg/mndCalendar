package com.mnd.calender

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.mnd.calender.databinding.ActivityMainBinding
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val api = APIS.create()
    private lateinit var selectedDate: LocalDate
    private val TAG = "로그"
    private lateinit var dataList : MutableList<CalenderItem>
    private var dayArray = mutableListOf<String>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // 버튼클릭시 오늘 날짜로 리사이클러뷰 이동
        // 원래는 포지션값으로 받았는데 day 값으로 바꿔야함
        binding.btnToday.setOnClickListener {
            if (selectedDate != LocalDate.now()) {
                selectedDate = LocalDate.now()
                setMonthView()
                getData(selectedDate.toString().substring(0, 4), selectedDate.toString().substring(5, 7))

                (binding.recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(dayArray.indexOf(LocalDate.now().toString().substring(8, 10)), 0)
            }else {
                (binding.recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(dayArray.indexOf(LocalDate.now().toString().substring(8, 10)),0)

            }
        }


        // 리사이클러뷰와 어댑터 연결
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // 현재 날짜 넣어주기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) selectedDate = LocalDate.now()
        else Toast.makeText(this, "버전이 낮아 앱을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()


        getData(selectedDate.toString().substring(0, 4), selectedDate.toString().substring(5, 7))
        setMonthView()

        binding.btnPre.setOnClickListener {
            selectedDate = selectedDate.minusMonths(1)
            setMonthView()
            getData(
                selectedDate.toString().substring(0, 4),
                selectedDate.toString().substring(5, 7)
            )
        }
        binding.btnNext.setOnClickListener {
            selectedDate = selectedDate.plusMonths(1)
            setMonthView()
            Log.d(
                TAG,
                "${selectedDate.toString().substring(0, 4)},${
                    selectedDate.toString().substring(5, 7)
                }"
            )
            getData(
                selectedDate.toString().substring(0, 4),
                selectedDate.toString().substring(5, 7)
            )
        }


        (binding.recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
            LocalDate.now().toString().substring(8, 10).toInt(),
            0
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMonthView() {
        binding.textNow.text = setDate(selectedDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM")
        return date.format(formatter)
    }

    private fun getData(year: String, mon: String) {
        api.selectCalendar(year,mon).enqueue(object :Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {

                var date = ""
                var content = ""
                var manager = ""
                var big = ""

                dataList = mutableListOf<CalenderItem>()

                val jsonArray = JSONArray(response.body().toString())

                for (i in 0 until jsonArray.length()) {

                    val value = jsonArray.getJSONObject(i)
                    Log.d(TAG, "onResponse: $value")

                    // 값이 없거나 다를 경우를 대비 -result or empty
                    if (value.toString().contains("dat")) {

                        date = value.getString("dat")
                        content = value.getString("tit")
                        manager = value.getString("dam")
                        big = value.getString("big")

                        val cal = Calendar.getInstance()

                        val dateArray = date.split("-").toTypedArray()

                        cal[dateArray[0].toInt(), dateArray[1].toInt() - 1] = dateArray[2].toInt()
                        dayArray.add(date.substring(8,10))
                        dataList.add(
                            CalenderItem(
                                date.substring(5,7),
                                date.substring(8, 10),
                                cal.get(Calendar.DAY_OF_WEEK),
                                manager,
                                content,
                                big
                            )
                        )
                    }
                }

                val listAdapter = ListAdapter(dataList)
                runOnUiThread { binding.recyclerView.adapter = listAdapter }


            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "통신 오류 발생", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

}