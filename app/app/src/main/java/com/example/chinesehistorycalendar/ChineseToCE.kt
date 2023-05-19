package com.example.chinesehistorycalendar

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity


class ChineseToCE : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    // Define your spinners and their corresponding database fields
    private val spinners = mapOf(
        "dynasty" to R.id.cDynastySpinner,
        "emperor" to R.id.cEmperorSpinner,
        "nianhao" to R.id.cNianhaoSpinner,
        "c_year" to R.id.cYearSpinner,
        "ganzhi_year" to R.id.cYearGanzhiSpinner,
        "c_month" to R.id.cMonthSpinner,
        "c_day" to R.id.cDaySpinner,
        "ganzhi_day" to R.id.cDayGanzhiSpinner
    )

    // Store the current selected values
    private val selectedValues = mutableMapOf<String, String>()
    private var newAdapter = mutableMapOf(
        "dynasty" to true,
        "emperor" to true,
        "nianhao" to true,
        "c_year" to true,
        "ganzhi_year" to true,
        "c_month" to true,
        "c_day" to true,
        "ganzhi_day" to true
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chinese_to_ce)

        db = DatabaseHelper(this)

        // Initialize each spinner with its possible values
        for ((field, spinnerId) in spinners) {
            val spinner = findViewById<Spinner>(spinnerId)
            updateSpinnerValues(spinner, field, emptyMap())
        }

        // Add an item selected listener for each spinner
        for ((field, spinnerId) in spinners) {
            findViewById<Spinner>(spinnerId).onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // do nothing
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    // Ignore during initialization
                    if (newAdapter[field] == true) {
                        newAdapter[field] = false
                        return
                    }
                    // Update the selected value for this field
                    selectedValues[field] = parent?.getItemAtPosition(position) as String

                    // Update the possible values of all other spinners based on the currently selected values
                    for ((otherField, otherSpinnerId) in spinners) {
                        if (otherField != field) {
                            updateSpinnerValues(findViewById(otherSpinnerId), otherField, selectedValues)
                        }
                    }
                }
            }
        }
    }

    private fun updateSpinnerValues(spinner: Spinner, field: String, constraints: Map<String, String>) {
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            db.getValuesForFieldWithConstraints(field, constraints)
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            newAdapter[field] = true
        }
    }
}
