package com.example.chinesehistorycalendar

import android.database.Cursor
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import DatabaseHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dbHelper = DatabaseHelper(this)

        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM your_table_name", null)

        while (cursor.moveToNext()) {
            // Read data from the cursor and display it
        }

        cursor.close()
        db.close()
    }
}
