package com.quotes.widget

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import com.google.android.material.chip.Chip

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_section)

        findViewById<Toolbar>(R.id.custom_tool).apply {
            title = getString(R.string.app_name)
            navigationIcon = AppCompatResources.getDrawable(this@AboutActivity,R.drawable.ic_back_arrow)
            setNavigationOnClickListener {
                Intent(this@AboutActivity,SettingsActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }

        findViewById<Chip>(R.id.about_github).setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/Nakuls426/Kwowt")
                )
            )
        }
    }
}