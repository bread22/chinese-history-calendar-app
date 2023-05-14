package com.example.chinesehistorycalendar

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import android.widget.AutoCompleteTextView
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    private lateinit var yearAutoComplete: AutoCompleteTextView
    private lateinit var monthAutoComplete: AutoCompleteTextView
    private lateinit var dayAutoComplete: AutoCompleteTextView
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        yearAutoComplete = findViewById(R.id.yearAutoComplete)
        monthAutoComplete = findViewById(R.id.monthAutoComplete)
        dayAutoComplete = findViewById(R.id.dayAutoComplete)
        resultTextView = findViewById(R.id.textView_result)

        val years = (1..1912).map { it.toString() }.toTypedArray()
        val months = (1..12).map { it.toString() }.toTypedArray()
        val days = (1..31).map { it.toString() }.toTypedArray()

        yearAutoComplete.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, years))
        monthAutoComplete.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, months))
        dayAutoComplete.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, days))

        yearAutoComplete.threshold = 1
        monthAutoComplete.threshold = 1
        dayAutoComplete.threshold = 1

        val textChangedListener: (Any) -> Unit = {
            // For the sake of demonstration, let's assume that the result of the query is an integer
            resultTextView.text = "${yearAutoComplete.text}/${monthAutoComplete.text}/${dayAutoComplete.text}"
        }

        yearAutoComplete.setOnItemClickListener { _, _, _, _ -> textChangedListener.invoke(Unit) }
        monthAutoComplete.setOnItemClickListener { _, _, _, _ -> textChangedListener.invoke(Unit) }
        dayAutoComplete.setOnItemClickListener { _, _, _, _ -> textChangedListener.invoke(Unit) }
    }
}