package com.wahid.wbcamera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val CAMERA_PERMISSION_CODE = 100
    private val OPENING_CODE = "WahidFriend"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etCode = findViewById<EditText>(R.id.etCode)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)

        tvWelcome.text = "Welcome to Wahid Developer"

        btnSubmit.setOnClickListener {
            val enteredCode = etCode.text.toString()
            if (enteredCode == OPENING_CODE) {
                checkCameraPermission()
            } else {
                Toast.makeText(this, "Wrong Code! Use: WahidFriend", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            openDashboard()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openDashboard()
            } else {
                Toast.makeText(this, "Camera permission required!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}
