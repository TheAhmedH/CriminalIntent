package com.bignerdranch.android.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment

private lateinit var crime: Crime
private lateinit var crimeTitleText: EditText
private lateinit var crimeDetailsButton: Button
private lateinit var solvedCheckBox: CheckBox

class CrimeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        crimeTitleText = view.findViewById(R.id.crime_title)
        crimeDetailsButton = view.findViewById(R.id.crime_details)
        solvedCheckBox = view.findViewById(R.id.solved_lable)

        crimeDetailsButton.apply {
            text = crime.date.toString()
            isEnabled = false
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                    sequence: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
            ) {
                //nothing to be implemented here
            }

            override fun onTextChanged(
                    sequence: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
            ) {
                crime.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
                //nothing to implement here too
            }
        }
        crimeTitleText.addTextChangedListener(titleWatcher)

        solvedCheckBox.apply {
            setOnClickListener { isChecked ->
                crime.isSolved = true
            }
        }
    }
}


