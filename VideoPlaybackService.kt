package com.wahid.wbcamera

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player

class VideoPlaybackService : Service() {

    private val CHANNEL_ID = "video_playback_channel"
    private val NOTIFICATION_ID = 2
    private var exoPlayer: ExoPlayer? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Video Playback", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val videoUri = intent?.getStringExtra("video_uri")
        videoUri?.let {
            startForeground(NOTIFICATION_ID, createNotification())
            startVideoPlayback(Uri.parse(it))
        }
        return START_STICKY
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("WB Camera")
        .setContentText("Playing video as camera feed")
        .setSmallIcon(android.R.drawable.ic_media_play)
        .build()

    private fun startVideoPlayback(videoUri: Uri) {
        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_ENDED -> {
                        exoPlayer?.seekTo(0)
                        exoPlayer?.play()
                    }
                    else -> {}
                }
            }
            override fun onPlayerError(error: PlaybackException) {
                stopSelf()
            }
        })
        val mediaItem = MediaItem.fromUri(videoUri)
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        exoPlayer?.play()
        exoPlayer?.repeatMode = Player.REPEAT_MODE_ONE
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
        exoPlayer = null
    }
}
