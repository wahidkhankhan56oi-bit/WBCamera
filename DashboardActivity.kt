package com.wahid.wbcamera

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    private var isCameraActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val btnVirtualCamera = findViewById<Button>(R.id.btnVirtualCamera)
        val btnStopCamera = findViewById<Button>(R.id.btnStopCamera)
        val btnGalleryVideo = findViewById<Button>(R.id.btnGalleryVideo)
        val btnCloneApp = findViewById<Button>(R.id.btnCloneApp)

        btnVirtualCamera.setOnClickListener { startVirtualCamera() }
        btnStopCamera.setOnClickListener { stopVirtualCamera() }
        btnGalleryVideo.setOnClickListener { selectVideoFromGallery() }
        btnCloneApp.setOnClickListener {
            Toast.makeText(this, "Clone App Feature Coming Soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startVirtualCamera() {
        val intent = Intent(this, MockCameraService::class.java)
        startForegroundService(intent)
        isCameraActive = true
        Toast.makeText(this, "✅ Virtual Camera Started\n\nGo to WhatsApp/Messenger and start video call", Toast.LENGTH_LONG).show()
        Toast.makeText(this, "Setup: Settings → Developer Options → Select mock camera app → WB Camera", Toast.LENGTH_LONG).show()
    }

    private fun stopVirtualCamera() {
        val intent = Intent(this, MockCameraService::class.java)
        stopService(intent)
        isCameraActive = false
        Toast.makeText(this, "❌ Virtual Camera Stopped", Toast.LENGTH_SHORT).show()
    }

    private fun selectVideoFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "video/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val videoUri = data?.data
            videoUri?.let {
                Toast.makeText(this, "Video Selected! Playing as camera feed", Toast.LENGTH_SHORT).show()
                startVideoAsCamera(it)
            }
        }
    }

    private fun startVideoAsCamera(videoUri: Uri) {
        val intent = Intent(this, VideoPlaybackService::class.java)
        intent.putExtra("video_uri", videoUri.toString())
        startForegroundService(intent)
        Toast.makeText(this, "Playing video as camera feed", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isCameraActive) stopVirtualCamera()
    }
}
