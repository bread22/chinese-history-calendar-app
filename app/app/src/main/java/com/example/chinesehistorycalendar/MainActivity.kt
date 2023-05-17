package com.example.chinesehistorycalendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chinesehistorycalendar.DatabaseHelper
import com.example.chinesehistorycalendar.Constants

class MainActivity : AppCompatActivity() {

    private lateinit var yearAutoComplete: AutoCompleteTextView
    private lateinit var monthAutoComplete: AutoCompleteTextView
    private lateinit var dayAutoComplete: AutoCompleteTextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Button to switch view for Chinese to C.E.
        val buttonNavigateToSecondActivity = findViewById<Button>(R.id.button_navigate_to_chinese2ce_activity)
        buttonNavigateToSecondActivity.setOnClickListener {
            val intent = Intent(this, ChineseToCE::class.java)
            startActivity(intent)
        }

        // Get references to your AutoCompleteTextViews, for C.E date
        yearAutoComplete = findViewById(R.id.yearAutoComplete)
        monthAutoComplete = findViewById(R.id.monthAutoComplete)
        dayAutoComplete = findViewById(R.id.dayAutoComplete)

        // Set up adapters for your AutoCompleteTextViews
        val years = (1..1912).map { it.toString() }.toTypedArray()
        val months = (1..12).map { it.toString() }.toTypedArray()
        val days = (1..31).map { it.toString() }.toTypedArray()
        yearAutoComplete.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, years))
        monthAutoComplete.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, months))
        dayAutoComplete.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, days))

        yearAutoComplete.threshold = 1
        monthAutoComplete.threshold = 1
        dayAutoComplete.threshold = 1

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView_CNY)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = ResultAdapter(emptyList()) // set an empty adapter


        // Add listeners to your AutoCompleteTextViews
        yearAutoComplete.setOnItemClickListener { _, _, _, _ -> updateResults() }
        monthAutoComplete.setOnItemClickListener { _, _, _, _ -> updateResults() }
        dayAutoComplete.setOnItemClickListener { _, _, _, _ -> updateResults() }

    }

    private fun updateResults() {
        val wYear = yearAutoComplete.text.toString().toIntOrNull() ?: return
        val wMonth = monthAutoComplete.text.toString().toIntOrNull() ?: return
        val wDay = dayAutoComplete.text.toString().toIntOrNull() ?: return

        val db = DatabaseHelper(this)
        val resultList = db.getResults(wYear, wMonth, wDay)

        Log.d("MainActivity", "Fetched results: $resultList") // Add this line

        recyclerView.adapter = ResultAdapter(resultList)
    }
    inner class ResultAdapter(private val resultList: List<DatabaseHelper.ChineseYearResultEntry>) : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

        inner class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val resultText: TextView = itemView.findViewById(R.id.result_text)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
            val itemView = layoutInflater.inflate(R.layout.result_item, parent, false)
            return ResultViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
            val currentItem = resultList[position]
            val resultString = "${currentItem.dynasty} " +
                    "${currentItem.emperor} " +
                    "${currentItem.nianhao} " +
                    "${Constants.NUMBER_MAP_YEAR_MONTH[currentItem.year]}年 " +
                    "${currentItem.ganzhi_year} " +
                    "${convertMonthToChinese(currentItem.month)}月 " +
                    "${Constants.NUMBER_MAP_DAY[currentItem.day]} " +
                    "${currentItem.ganzhi_day}"

            holder.resultText.text = resultString
            Log.d("ResultAdapter", "Binding item: $currentItem") // Add this line
        }

        override fun getItemCount() = resultList.size
    }

    fun convertMonthToChinese(input: String): String {
        // Define a regex pattern to match numbers
        val pattern = Regex("[0-9]+")

        // Split the input into a list of parts
        val parts = pattern.findAll(input).toList().map { it.value }

        // Convert each part that's a number
        val convertedParts = parts.map { part ->
            Constants.NUMBER_MAP_YEAR_MONTH[part] ?: part
        }

        // Join the converted parts back into a single string
        var result = input
        for ((index, part) in parts.withIndex()) {
            result = input.replace(part, convertedParts[index])
        }

        return result
    }

}
