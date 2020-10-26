package com.bignerdranch.android.criminalintent

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.coroutines.coroutineContext
import android.widget.Toast.makeText as makeText1

private const val TAG = "CrimeListFragment"
private lateinit var crimeRecyclerView: RecyclerView
private var adapter: CrimeAdapter? = null


class CrimeListFragment : Fragment() {

    val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view)
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)

        //Setting the adapter
        UpdateUI()

        return view
    }

    private fun UpdateUI() {
        //crimeRecyclerView.adapter = CrimeAdapter(crimeListViewModel.crimesList)
        val crimes = crimeListViewModel.crimesList
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    //Total number of crimes available
        Log.d(TAG, "Total Crime ${crimeListViewModel.crimesList.size} created")
    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }
}

//Creating a Crime ViewHolder
private class CrimeViewHolder(view: View) :
    RecyclerView.ViewHolder(view), View.OnClickListener {
    val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
    val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
    }
}

private class CrimeAdapter(var crimes: List<Crime>) : RecyclerView.Adapter<CrimeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_crime, parent, false)
        return CrimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: CrimeViewHolder, position: Int) {
        var currentCrime = crimes[position]
        holder.apply {
            dateTextView.text = currentCrime.date.toString()
            titleTextView.text = currentCrime.title
        }
    }

    override fun getItemCount() = crimes.size
}

