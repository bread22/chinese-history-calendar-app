package com.wuqingdev.chinesehistorycalendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.util.Linkify
import android.widget.TextView

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        // Define privacyPolicyTextView here
        val privacyPolicyTextView: TextView = findViewById(R.id.tv_privacy_policy)

        // Use either Linkify or HTML formatting to make the text a hyperlink
        // Linkify:
        Linkify.addLinks(privacyPolicyTextView, Linkify.WEB_URLS)

        val versionTextView: TextView = findViewById(R.id.tv_version)
        val version = packageManager.getPackageInfo(packageName, 0).versionName
        versionTextView.text = "Version: $version"
    }
}