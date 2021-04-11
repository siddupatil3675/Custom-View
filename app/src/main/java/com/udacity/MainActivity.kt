package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var downloadURL: String = ""
    private var fileDetails: String = ""

    private lateinit var notificationManager: NotificationManager
    private lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        notificationManager = ContextCompat.getSystemService(
                this, NotificationManager::class.java
        ) as NotificationManager
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        createChannel(
                getString(R.string.notification_channel_id),
                getString(R.string.notification_channel_name)
        )

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            download()
        }


    }

    private val receiver = object : BroadcastReceiver() {
        var status: String = ""
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                Toast.makeText(
                        context,
                        getString(R.string.notification_description),
                        Toast.LENGTH_SHORT
                ).show()
                custom_button.loaderStatus(ButtonState.Completed)


            }

            val cursor: Cursor =
                    downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
            while (cursor.moveToNext()) {
                when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                    DownloadManager.STATUS_FAILED -> {
                        status = getString(R.string.status_failure)

                    }
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        status = getString(R.string.status_success)
                    }
                }

            }
            notificationManager.sendNotification(this@MainActivity, fileDetails, status)
        }
    }

    private fun download() {
        if (downloadURL.isNotEmpty()) {
            val request =
                    DownloadManager.Request(Uri.parse(downloadURL))
                            .setTitle(getString(R.string.app_name))
                            .setDescription(getString(R.string.app_description))
                            .setRequiresCharging(false)
                            .setAllowedOverMetered(true)
                            .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                    downloadManager.enqueue(request)
            custom_button.loaderStatus(ButtonState.Loading)
        } else {
            Toast.makeText(this, getString(R.string.validation), Toast.LENGTH_SHORT)
                    .show()
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            )
                    .apply {
                        setShowBadge(false)
                    }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.app_name)

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked
            when (view.getId()) {
                R.id.radio_glide ->
                    if (checked) {
                        downloadURL = getString(R.string.glide_url)
                        fileDetails = getString(R.string.glide_label)
                    }
                R.id.radio_udacity ->
                    if (checked) {
                        downloadURL = getString(R.string.udacity_url)
                        fileDetails = getString(R.string.udacity_label)
                    }
                R.id.radio_retrofit ->
                    if (checked) {
                        downloadURL = getString(R.string.retrofit_url)
                        fileDetails = getString(R.string.retrofit_label)
                    }
            }
        }
    }

}
