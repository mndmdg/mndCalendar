package com.mndsystem.newcalendar

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mndsystem.newcalendar.databinding.ScheduleItemBinding

class ScheduleAdapter(private val data: List<CalendarData>) :
    RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    class ViewHolder(val binding: ScheduleItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setView(data: List<CalendarData>, position: Int) {
            binding.dam.text = data[position].dam
            binding.tit.text = data[position].tit
            if (data[position].big!="") {
                binding.big.visibility = View.VISIBLE
                binding.big.text = data[position].big
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ScheduleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setView(data,position)
        holder.binding.clickSchdule.setOnClickListener {
            val intent = Intent(holder.itemView.context,EditActivity::class.java)
            intent.putExtra("sdx",data[position].sdx)
            intent.putExtra("dat",data[position].dat)
            intent.putExtra("tit",data[position].tit)
            intent.putExtra("big",data[position].big)
            intent.putExtra("dam",data[position].dam)
            ContextCompat.startActivity(holder.itemView.context,intent,null)
            (holder.itemView.context as Activity).overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }
    }


    override fun getItemCount(): Int = data.size
}