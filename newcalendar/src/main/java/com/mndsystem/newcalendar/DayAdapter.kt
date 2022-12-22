package com.mndsystem.newcalendar

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mndsystem.newcalendar.databinding.DayItemBinding
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
class DayAdapter(
    private val data: MutableList<CalendarData>,
    private val datArray: List<String>,
    private val context: Context
) :
    RecyclerView.Adapter<DayAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: DayItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            layoutManager.recycleChildrenOnDetach = true
            binding.recyclerViewDay.layoutManager = layoutManager
        }

        @SuppressLint("SetTextI18n")
        fun setSchedule(data: MutableList<CalendarData>, dat: String) {
            val scheduleArray = data.filter { it.dat == dat }
            val cal = Calendar.getInstance()
            binding.tvDay.text = dat.substring(8, 10)
            if (scheduleArray[0].tit.isNotEmpty()){
                binding.recyclerViewDay.adapter = ScheduleAdapter(scheduleArray)
            }else{
                binding.recyclerViewLayout.visibility = View.GONE
                Log.d("TAG", "setSchedule: ??")
            }

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
            if (sdf.format(Date()) == dat) {
                binding.clickDay.setBackgroundColor(Color.YELLOW)
            }
            val split = dat.split("-")
            cal.set(split[0].toInt(), split[1].toInt() - 1, split[2].toInt())
            when (cal.get(Calendar.DAY_OF_WEEK)) {
                1 -> {
                    binding.apply {
//                        tvDay.setTextColor(Color.RED)
                        tvWeek.setTextColor(Color.RED)
                        tvWeek.text = "일요일"
                    }
                }
                2 -> {
                    binding.apply {
                        tvWeek.text = "월요일"
                    }
                }
                3 -> {
                    binding.apply {
                        tvWeek.text = "화요일"
                    }
                }
                4 -> {
                    binding.apply {
                        tvWeek.text = "수요일"
                    }
                }
                5 -> {
                    binding.apply {
                        tvWeek.text = "목요일"
                    }
                }
                6 -> {
                    binding.apply {
                        tvWeek.text = "금요일"

                    }
                }
                7 -> {
                    binding.apply {
//                        tvDay.setTextColor(Color.BLUE)
                        tvWeek.setTextColor(Color.BLUE)
                        tvWeek.text = "토요일"
                    }
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setSchedule(data, datArray[position])

    }

    override fun getItemCount(): Int = datArray.size
}