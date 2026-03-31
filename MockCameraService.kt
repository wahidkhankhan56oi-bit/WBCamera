package com.wahid.wbcamera

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class MockCameraService : Service() {

    private val CHANNEL_ID = "wb_camera_channel"
    private val NOTIFICATION_ID = 1
    private var cameraDevice: CameraDevice? = null
    private var cameraManager: CameraManager? = null
    private var imageReader: ImageReader? = null
    private var captureSession: CameraCaptureSession? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        setupCamera()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "WB Camera Service", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("WB Camera")
        .setContentText("Virtual Camera Active - Ready for Video Calls")
        .setSmallIcon(android.R.drawable.ic_menu_camera)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .build()

    private fun setupCamera() {
        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            val cameraId = cameraManager?.cameraIdList?.get(0)
            if (cameraId != null) {
                cameraManager?.openCamera(cameraId, object : CameraDevice.StateCallback() {
                    override fun onOpened(camera: CameraDevice) {
                        cameraDevice = camera
                        Log.d("WBCamera", "Camera Opened Successfully")
                        createVirtualCameraSurface()
                    }
                    override fun onDisconnected(camera: CameraDevice) {
                        Log.d("WBCamera", "Camera Disconnected")
                    }
                    override fun onError(camera: CameraDevice, error: Int) {
                        Log.d("WBCamera", "Camera Error: $error")
                    }
                }, null)
            }
        } catch (e: Exception) {
            Log.e("WBCamera", "Camera Error: ${e.message}")
        }
    }

    private fun createVirtualCameraSurface() {
        imageReader = ImageReader.newInstance(640, 480, android.graphics.ImageFormat.YUV_420_888, 2)
        try {
            cameraDevice?.createCaptureSession(
                listOf(imageReader!!.surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        captureSession = session
                        startCapture()
                    }
                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.d("WBCamera", "Session Failed")
                    }
                }, null
            )
        } catch (e: Exception) {
            Log.e("WBCamera", "Create Session Error: ${e.message}")
        }
    }

    private fun startCapture() {
        try {
            val captureRequest = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequest?.addTarget(imageReader!!.surface)
            captureSession?.setRepeatingRequest(captureRequest?.build()!!, null, null)
            Log.d("WBCamera", "Virtual Camera Active")
        } catch (e: Exception) {
            Log.e("WBCamera", "Start Capture Error: ${e.message}")
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        captureSession?.close()
        cameraDevice?.close()
        imageReader?.close()
        Log.d("WBCamera", "Service Destroyed")
    }
}
