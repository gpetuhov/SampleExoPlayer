package com.gpetuhov.android.sampleexoplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import android.net.Uri
import android.os.Build
import android.os.PersistableBundle
import android.view.View


class MainActivity : AppCompatActivity() {

    companion object {
        const val PLAYBACK_POSITION_KEY = "playback_position"
        const val CURRENT_WINDOW_KEY = "current_window"
        const val PLAYBACK_WHEN_READY_KEY = "playback_when_ready"
    }

    private var player: SimpleExoPlayer? = null
    private var playbackPosition: Long = 0
    private var currentWindow: Int = 0
    private var playWhenReady: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState?.apply {
            playbackPosition = getLong(PLAYBACK_POSITION_KEY, 0)
            currentWindow = getInt(CURRENT_WINDOW_KEY, 0)
            playWhenReady = getBoolean(PLAYBACK_WHEN_READY_KEY, false)
        }
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M || player == null) {
            initializePlayer()
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            releasePlayer()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            releasePlayer()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        outState?.apply {
            putLong(PLAYBACK_POSITION_KEY, playbackPosition)
            putInt(CURRENT_WINDOW_KEY, currentWindow)
            putBoolean(PLAYBACK_WHEN_READY_KEY, playWhenReady)
        }

        super.onSaveInstanceState(outState, outPersistentState)
    }

    private fun initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this)

        playerView.player = player

        val uri = Uri.parse(getString(R.string.media_url_mp4))

        val mediaSource = ExtractorMediaSource.Factory(
                DefaultHttpDataSourceFactory("exoplayer-codelab"))
                .createMediaSource(uri)

        player?.prepare(mediaSource, true, false)

        player?.playWhenReady = playWhenReady
        player?.seekTo(currentWindow, playbackPosition)
    }

    private fun hideSystemUi() {
        playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private fun releasePlayer() {
        playbackPosition = player?.currentPosition ?: 0
        currentWindow = player?.currentWindowIndex ?: 0
        playWhenReady = player?.playWhenReady ?: false
        player?.release()
        player = null
    }
}
