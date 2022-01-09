package com.android.example.messapp

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MealsFragment(private val position: Int) : Fragment() {
    private val viewModel by viewModels<FirestoreDataFetch>() {
        FirebaseDataFetchViewModelFactory(
            requireActivity().application
        )
    }
    private val dateViewModel by viewModels<DateViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_meals, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.mealsRecyclerView)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        dateViewModel.init((activity as AppCompatActivity).applicationContext as Application)
        recyclerView.layoutManager = LinearLayoutManager(context)

        if (position in 0..6) {
            viewModel.getMenu(resources.getStringArray(R.array.days)[position])
        } else {
            viewModel.getMenu(resources.getStringArray(R.array.days)[0])
        }

        val recyclerAdapter: MealsListAdapter = MealsListAdapter(context)
        recyclerView.adapter = recyclerAdapter

        viewModel.menuUiModelLiveData.observe(viewLifecycleOwner) {
            recyclerAdapter.submitList(it, recyclerView)
            progressBar.visibility = View.GONE

        }
        val format: DateFormat = SimpleDateFormat("dd-MM-yyyy")
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar[Calendar.DAY_OF_WEEK] = Calendar.MONDAY

        val days = arrayOfNulls<String>(7)
        for (i in 0..6) {
            days[i] = format.format(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        val date = days[position]

        val c: Calendar = GregorianCalendar()
        c[Calendar.HOUR_OF_DAY] = 0 //anything 0 - 23
        c[Calendar.MINUTE] = 0
        c[Calendar.SECOND] = 0
        if (date != null) {
            c[Calendar.DAY_OF_MONTH] = date.subSequence(0,2).toString().toInt()
            c[Calendar.MONTH] = date.subSequence(3,5).toString().toInt()-1
            c[Calendar.YEAR] = date.subSequence(6,10).toString().toInt()
        }
        val d1 = (Calendar.getInstance().timeInMillis - c.timeInMillis)/1000
        var meal=-1

            val itemTouchHelper = ItemTouchHelper(MealsItemTouchHelper(recyclerAdapter) { pos, dir ->
                //Breakfast - 8am  => 6 hrs
                //Lunch - 2pm => 12 hrs
                //Dinner - 7pm => 17 hrs

                if(pos==0){
                    meal=26100
                }else if(pos==1){
                    meal=52200
                }else{
                    meal=61200
                }
                when (dir) {
                ItemTouchHelper.RIGHT -> {
                    Log.i("MESS",meal.toString())
                    if(d1>meal){
                        //TODO alert dialog for late
                        Log.i("MESS","Late ho gaya bhai decide karne mein")
                    }
                    else
                    {

                        if (viewModel.menuUiModelLiveData.value!![pos].coming) {
                            val builder = AlertDialog.Builder(recyclerAdapter.context)
                            builder.setTitle("Delete")
                            builder.setMessage("Are you sure you want to delete?")
                            builder.setPositiveButton("Confirm") { _, _ ->
                                viewModel.updateUserPref(pos, false)
                            }
                            builder.setNegativeButton("Cancel") { dialog, _ ->
                                dialog?.dismiss()
                            }
                            val dialog = builder.create()
                            dialog.show()
                        }
                    }

                }

                ItemTouchHelper.LEFT -> {
                    if(d1>meal){
                        //TODO alert dialog for late
                        Log.i("MESS","Late ho gaya bhai decide karne mein")
                    }else{
                        if (!viewModel.menuUiModelLiveData.value!![pos].coming)
                            viewModel.updateUserPref(pos, true)
                    }
                }

            }

        })
            itemTouchHelper.attachToRecyclerView(recyclerView)
        viewModel.position = pos
        return view
    }

    companion object {
        var pos: Int = -1

        @JvmStatic
        fun newInstance(position: Int): MealsFragment {
            pos = position
            return MealsFragment(position)
        }
    }
}