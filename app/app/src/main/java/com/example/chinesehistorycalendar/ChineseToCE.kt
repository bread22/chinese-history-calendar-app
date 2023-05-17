package com.example.chinesehistorycalendar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ChineseToCE : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chinese_to_ce)

        // Button to switch view to MainActivity.
        val buttonNavigateToSecondActivity = findViewById<Button>(R.id.button_navigate_to_main_activity)
        buttonNavigateToSecondActivity.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}