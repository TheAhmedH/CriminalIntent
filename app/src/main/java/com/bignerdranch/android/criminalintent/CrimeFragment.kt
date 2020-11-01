package com.bignerdranch.android.criminalintent

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.createChooser
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher

import android.view.*
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_crime.*
import java.text.DateFormat
import java.util.*

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0
private const val DATE_FORMAT = "EEE, MMM, dd"
private const val REQUEST_CONTACT = 1

class CrimeFragment : Fragment(), DatePickerFragment.CallBacks {
    private lateinit var crime: Crime
    private lateinit var crimeTitleText: EditText
    private lateinit var crimeDateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button

    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeID: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        //DetailViewModel loads the Crime in onCreate
        crimeDetailViewModel.loadCrime(crimeID)
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dateString = android.text.format.DateFormat.format(DATE_FORMAT, crime.date).toString()
        val suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }
        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        crimeTitleText = view.findViewById(R.id.crime_title)
        crimeDateButton = view.findViewById(R.id.crime_details)
        solvedCheckBox = view.findViewById(R.id.solved_lable)
        reportButton = view.findViewById(R.id.crime_report_button)
        suspectButton = view.findViewById(R.id.crime_suspect_button)
//        crimeDateButton.apply {
//            text = crime.date.toString()
//            isEnabled = false
//        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        crimeDetailViewModel.crimeLiveData.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer { crime ->
                    crime?.let {
                        this.crime = crime
                        updateUI()
                    }
                })
    }

    fun updateUI() {
        crimeTitleText.setText(crime.title)
        crimeDateButton.text = crime.date.toString()
        solvedCheckBox.isChecked = crime.isSolved

        //TODO: Suspect becomes common to all crimes
//        if (crime.suspect.isNotEmpty()) {
//            suspectButton.text = crime.suspect
//        }
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
        crimeDateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                show(this@CrimeFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }

        reportButton.setOnClickListener {
            var newIntent = Intent().apply {
                action = ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }
            //Adding a chooser for intent to display activities that respond to the implicit intent
            val chooserIntent = Intent.createChooser(newIntent, getString(R.string.send_report))
            startActivity(chooserIntent)
        }

        //Selecting a contact for a suspect
        suspectButton.apply {
            val contactsIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            setOnClickListener {
                startActivityForResult(contactsIntent, REQUEST_CONTACT)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    companion object {
        fun newInstance(crimeID: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeID)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }

    override fun OnDateSelected(date: Date) {
        crime.date = date
        updateUI()
    }

    //TODO: Suspect becomes common to all crimes once selected
    //Updating the suspectButton value instantly as Contact is selected. onActivityResult is called before onViewCreated
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        when {
//            resultCode != Activity.RESULT_OK -> return
//            requestCode == REQUEST_CONTACT && data != null -> {
//                val contactUri: Uri? = data?.data
//                // Specify which fields you want your query to return values for
//                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
//                // Perform your query - the contactUri is like a "where" clause here
//                val cursor = requireActivity().contentResolver.query(contactUri!!, queryFields, null, null, null)
//                cursor?.use {
//                    // Verify cursor contains at least one result
//                    if (it.count == 0) {
//                        return
//                    }
//                    // Pull out the first column of the first row of data - // that is your suspect's name
//                    it.moveToFirst()
//                    val suspect = it.getString(0)
//                    crime.suspect = suspect
//                    crimeDetailViewModel.saveCrime(crime)
//                    suspectButton.text = suspect
//                }
//            }
//        }
//    }
}


