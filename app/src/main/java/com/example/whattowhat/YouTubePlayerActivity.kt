package com.example.whattowhat

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker

class YouTubePlayerActivity : AppCompatActivity() {

    private lateinit var youtubePlayerView: YouTubePlayerView
    private var youTubePlayer: YouTubePlayer? = null
    private var isVideoPlaying: Boolean = false
    private var currentPlaybackPosition: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube_player)

        youtubePlayerView = findViewById(R.id.youTubePlayerView)

        lifecycle.addObserver(youtubePlayerView)

        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(player: YouTubePlayer) {
                val videoId = intent.getStringExtra("VIDEO_ID")
                videoId?.let {
                    player.loadVideo(it, 0f)
                }
                youTubePlayer = player
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                isVideoPlaying = state == PlayerConstants.PlayerState.PLAYING
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                currentPlaybackPosition = second
            }
        })
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        when (event.keyCode) {
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                if (event.action == KeyEvent.ACTION_DOWN) {
                    youTubePlayer?.let { player ->
                        if (isVideoPlaying) {
                            player.pause()
                        } else {
                            player.play()
                        }
                    }
                }
                return true
            }

            KeyEvent.KEYCODE_MEDIA_REWIND -> {
                if (event.action == KeyEvent.ACTION_DOWN) {
                    youTubePlayer?.let { player ->
                        player.seekTo(currentPlaybackPosition - 10) // Rewind by 10 seconds
                    }
                }
                return true
            }

            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                if (event.action == KeyEvent.ACTION_DOWN) {
                    youTubePlayer?.let { player ->
                        player.seekTo(currentPlaybackPosition + 10) // Fast forward by 10 seconds
                    }
                }
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }
}
