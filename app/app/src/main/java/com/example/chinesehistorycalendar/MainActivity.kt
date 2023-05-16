package com.example.chinesehistorycalendar

import android.util.Log
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import DatabaseHelper
import DatabaseHelper.ChineseYearResultEntry


class MainActivity : AppCompatActivity() {

    private lateinit var yearAutoComplete: AutoCompleteTextView
    private lateinit var monthAutoComplete: AutoCompleteTextView
    private lateinit var dayAutoComplete: AutoCompleteTextView
    private lateinit var resultTextView: TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Initialize DatabaseHelper
        val dbHelper = DatabaseHelper(this)
//        val cursor = db.rawQuery("SELECT * FROM your_table_name", null)
//
//        val stringBuilder = StringBuilder()
//
//        while (cursor.moveToNext()) {
//            // Get the data from the cursor
//            val columnName1 = cursor.getString(cursor.getColumnIndex("column_name1"))
//            val columnName2 = cursor.getString(cursor.getColumnIndex("column_name2"))
//
//            // Format the data and append it to the StringBuilder
//            stringBuilder.append("Column1: $columnName1, Column2: $columnName2\n")
//        }

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
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView_CNY)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = ResultAdapter(emptyList()) // set an empty adapter


        val textChangedListener: (Any) -> Unit = {
            // For the sake of demonstration, let's assume that the result of the query is an integer
            resultTextView.text = "${yearAutoComplete.text}/${monthAutoComplete.text}/${dayAutoComplete.text}"
        }

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
    inner class ResultAdapter(private val resultList: List<ChineseYearResultEntry>) : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

        inner class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val resultText: TextView = itemView.findViewById(R.id.result_text)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
            val itemView = layoutInflater.inflate(R.layout.result_item, parent, false)
            return ResultViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
            val currentItem = resultList[position]
            val resultString = """
                ${currentItem.dynasty} ${currentItem.emperor} ${currentItem.nianhao} ${currentItem.year}年 ${currentItem.ganzhi_year} ${currentItem.month}月 ${currentItem.day} ${currentItem.ganzhi_day}
            """.trimIndent()

            holder.resultText.text = resultString
            Log.d("ResultAdapter", "Binding item: $currentItem") // Add this line
        }

        override fun getItemCount() = resultList.size
    }
}
