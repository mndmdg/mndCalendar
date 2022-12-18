package com.mnd.calender

import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior.StableState
import com.mnd.calender.databinding.ListItemBinding
import java.time.LocalDate

class ListAdapter(private val itemList: MutableList<CalenderItem>) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {


    class ViewHolder(private val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun setList(memo: CalenderItem) {
            with(binding) {
                if (LocalDate.now().toString().substring(8, 10)==memo.day){
                    linearDay.setBackgroundColor(Color.YELLOW)
                }

                when (memo.week) {
                    1 -> {
                        textWeek.text = "일요일"
                        textWeek.setTextColor(Color.RED)
                    }
                    2 -> {
                        textWeek.text = "월요일"
                    }
                    3 -> {
                        textWeek.text = "화요일"
                    }
                    4 -> {
                        textWeek.text = "수요일"
                    }
                    5 -> {
                        textWeek.text = "목요일"
                    }
                    6 -> {
                        textWeek.text = "금요일"
                    }
                    7 -> {
                        textWeek.text = "토요일"
                        textWeek.setTextColor(Color.BLUE)
                    }
                }
                textDay.text = memo.day
                textBig.text = memo.big
                if (memo.big != "") {
                    textBig.isVisible = true
                }
                textContent.text = memo.Title
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun setList(memo: CalenderItem, preMemo: CalenderItem, position: Int) {
            val TAG = "어댑터태그"
            Log.d(TAG, "현재 : ${LocalDate.now().toString().substring(8, 10)==memo.day}")
            with(binding) {
                // 현재 달과 리사이클러뷰의 달이 같으면
                // 현재날짜에 노란색을 입힘.
                // position 값이 1부터 시작함 왜인지는 모르겠음
                if (memo.mon == LocalDate.now().toString().substring(5, 7)) {
                    if (memo.day == LocalDate.now().toString().substring(8, 10)) {
                        linearDay.setBackgroundColor(Color.YELLOW)
                    }
                }
                when (memo.week) {
                    1 -> {
                        textWeek.text = "일요일"
                        textWeek.setTextColor(Color.RED)
                    }
                    2 -> {
                        textWeek.text = "월요일"
                    }
                    3 -> {
                        textWeek.text = "화요일"
                    }
                    4 -> {
                        textWeek.text = "수요일"
                    }
                    5 -> {
                        textWeek.text = "목요일"
                    }
                    6 -> {
                        textWeek.text = "금요일"
                    }
                    7 -> {
                        textWeek.text = "토요일"
                        textWeek.setTextColor(Color.BLUE)
                    }
                }

                if (preMemo.day == memo.day) {
                    textDay.text = memo.day
                    binding.linearDay.isGone = true
                    binding.line1.isGone = true
                } else {
                    textDay.text = memo.day
                }

                if (memo.big != "") {
                    textBig.text = memo.big
                    textBig.isVisible = true
                }
                textContent.text = memo.Title
//                textManager.text = memo.manager
            }
        }

    }

    // 아이템 레이아웃과 결합
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listData = itemList[position]

        // 첫번째 값일때
        if (position == 0)
            holder.setList(listData)
        else
            holder.setList(listData, itemList[position - 1], position)
    }

    override fun getItemCount(): Int = itemList.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}