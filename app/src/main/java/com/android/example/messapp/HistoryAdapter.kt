package com.android.example.messapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.android.example.messapp.databinding.HistoryCardviewBinding

class HistoryAdapter: RecyclerView.Adapter<HistoryAdapter.ViewHolder>(){
    private var dates:List<Date>  = emptyList<Date>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):HistoryAdapter.ViewHolder {
        val binding = HistoryCardviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryAdapter.ViewHolder, position: Int) {
        with(holder){
            with(dates[position]){
                binding.date.text = this.date
                if(this.breakfast){
                    binding.breakfast.text="Breakfast: Done"
                }else{
                    binding.breakfast.text="Breakfast: Cancelled"
                }

                if(this.lunch){
                    binding.lunch.text="Lunch: Done"
                }else{
                    binding.lunch.text="Lunch: Cancelled"
                }

                if(this.dinner){
                    binding.dinner.text="Dinner: Done"
                }else{
                    binding.dinner.text="Dinner: Cancelled"
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return dates.size
    }
    fun setDates(dates: List<Date>){
        this.dates = dates
        notifyDataSetChanged()
    }
    inner class ViewHolder(val binding: HistoryCardviewBinding): RecyclerView.ViewHolder(binding.root){


    }


}