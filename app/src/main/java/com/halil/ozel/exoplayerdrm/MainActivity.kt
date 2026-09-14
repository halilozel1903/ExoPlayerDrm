package com.halil.ozel.exoplayerdrm

import android.os.Bundle
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.halil.ozel.exoplayerdrm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.streamGroup.setOnCheckedChangeListener { _, _ -> startPlayback() }
    }

    override fun onStart() {
        super.onStart()
        startPlayback()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun startPlayback() {
        releasePlayer()
        when (binding.streamGroup.checkedRadioButtonId) {
            R.id.streamWidevineMediaItem -> playWidevineWithMediaItem()
            R.id.streamWidevineSessionManager -> playWidevineWithSessionManager()
            R.id.streamClear -> playClearDash()
            R.id.streamClearKey -> playClearKey()
        }
    }

    private fun playWidevineWithMediaItem() {
        val mediaItem = DrmMediaItems.widevineDash(
            manifestUri = widevineManifestUri(),
            licenseUri = widevineLicenseUri()
        )
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            attachPlayer(exoPlayer, mediaItem)
        }
        binding.status.setText(R.string.status_playing_widevine)
    }

    @OptIn(UnstableApi::class)
    private fun playWidevineWithSessionManager() {
        val mediaItem = DrmMediaItems.clearDash(widevineManifestUri())
        val drmSessionManager = DrmSessionManagers.widevine(widevineLicenseUri())
        val mediaSourceFactory = DefaultMediaSourceFactory(this)
            .setDrmSessionManagerProvider { drmSessionManager }
        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
            .also { exoPlayer -> attachPlayer(exoPlayer, mediaItem) }
        binding.status.setText(R.string.status_playing_widevine_manager)
    }

    private fun playClearDash() {
        val mediaItem = DrmMediaItems.clearDash(DemoStreams.CLEAR_DASH_H264)
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            attachPlayer(exoPlayer, mediaItem)
        }
        binding.status.setText(R.string.status_playing_clear)
    }

    private fun playClearKey() {
        val manifestUri = BuildConfig.CLEARKEY_MANIFEST_URI
        val licenseUri = BuildConfig.CLEARKEY_LICENSE_URI
        if (manifestUri.isBlank() || licenseUri.isBlank()) {
            binding.status.setText(R.string.status_clearkey_missing)
            return
        }
        val mediaItem = DrmMediaItems.clearKeyDash(manifestUri, licenseUri)
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            attachPlayer(exoPlayer, mediaItem)
        }
        binding.status.text = getString(
            R.string.status_playing_clearkey,
            licenseUri
        )
    }

    private fun attachPlayer(exoPlayer: ExoPlayer, mediaItem: MediaItem) {
        binding.playerView.player = exoPlayer
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                binding.status.text = getString(R.string.status_error, error.errorCodeName)
            }
        })
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.playWhenReady = true
        exoPlayer.prepare()
    }

    private fun releasePlayer() {
        binding.playerView.player = null
        player?.release()
        player = null
    }

    private fun widevineManifestUri(): String {
        return BuildConfig.WIDEVINE_MANIFEST_URI.ifBlank { DemoStreams.WIDEVINE_DASH_CENC_H264 }
    }

    private fun widevineLicenseUri(): String {
        return BuildConfig.WIDEVINE_LICENSE_URI.ifBlank { DemoStreams.WIDEVINE_UAT_LICENSE_URI }
    }
}
