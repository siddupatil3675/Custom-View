package com.udacity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val fileName = intent.getStringExtra(getString(R.string.extra_file_name))
        val status = intent.getStringExtra(getString(R.string.download_status))

        file_name_text.text = fileName.toString()
        download_status_text.text = status.toString()
        if (status == getString(R.string.status_success)) {
            download_status_text.setTextColor(getColor(R.color.success))
        } else {
            download_status_text.setTextColor(getColor(R.color.failure))

        }
    }

    fun onBackPressed(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}
