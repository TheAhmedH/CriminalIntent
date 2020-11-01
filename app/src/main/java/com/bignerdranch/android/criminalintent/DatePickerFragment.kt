package com.bignerdranch.android.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.GregorianCalendar
import android.os.Build
import android.os.Bundle
import android.view.Gravity.apply
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_DATE = "date"

class DatePickerFragment : DialogFragment() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dateListener = DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
            val resultDate: Date = GregorianCalendar(year, month, day).time

            targetFragment?.let { fragment -> (fragment as CallBacks).OnDateSelected(resultDate) }
        }

        val date = arguments?.getSerializable(ARG_DATE) as Date
        var calendar = Calendar.getInstance()
        calendar.time = date
        var initialYear = calendar.get(Calendar.YEAR)
        var initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireContext(), dateListener, initialYear, initialMonth, initialDay)
    }

    interface CallBacks {
        fun OnDateSelected(date: Date)
    }


    companion object {
        fun newInstance(date: Date): DatePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }
            return DatePickerFragment().apply {
                arguments = args
            }
        }
    }

}

