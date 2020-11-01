package com.bignerdranch.android.criminalintent

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.persistableBundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.util.*

private const val TAG = "CrimeListFragment"
private lateinit var crimeRecyclerView: RecyclerView
private var adapter: CrimeAdapter? = CrimeAdapter((emptyList()))
private var callbacks: CrimeListFragment.Callbacks? = null

class CrimeListFragment : Fragment() {

    val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    //Adding a callback interface to delegate onclick events from CrimeListFragment back to it's host Activity
    interface Callbacks {
        fun onCrimeSelected(id: UUID)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    //Inflating the Menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    //Callback onAttach
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    //Callback onDetach
    override fun onDetach() {
        super.onDetach()
        callbacks = null
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
        crimeRecyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimesListLiveData.observe(viewLifecycleOwner, Observer { crimes ->
            crimes?.let {
                Log.i(TAG, "Got crimes ${crimes.size}")
                updateUI(crimes)
            }
        })
    }

    //Prepare the Adapter
    fun updateUI(crimes: List<Crime>) {
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }
}

//Creating an Adapter
private class CrimeAdapter(var crimes: List<Crime>) :
        RecyclerView.Adapter<CrimeAdapter.CrimeViewHolder>() {

    lateinit var currentSelectedCrimeID: UUID

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeViewHolder {
        val view =
                LayoutInflater.from(parent.context).inflate(R.layout.list_item_crime, parent, false)
        return CrimeViewHolder(view)
    }

    //Creating a Crime ViewHolder
    private inner class CrimeViewHolder(view: View) :
            RecyclerView.ViewHolder(view), View.OnClickListener {
        val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved_image)
        val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        val dateTextView: TextView = itemView.findViewById(R.id.crime_date)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            callbacks?.onCrimeSelected(currentSelectedCrimeID)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CrimeViewHolder, position: Int) {
        var currentCrime = crimes[position]
        currentSelectedCrimeID = currentCrime.id
        holder.solvedImageView.visibility = if (currentCrime.isSolved) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
//        val simpleDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        holder.apply {
            DateFormat.DAY_OF_YEAR_FIELD
            dateTextView.text = currentCrime.date.toString()
            titleTextView.text = currentCrime.title
        }
    }

    override fun getItemCount() = crimes.size
}