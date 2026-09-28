package com.halil.ozel.exoplayerdrm

import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.annotation.OptIn
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.drm.DrmSessionManager
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.util.EventLogger
import com.halil.ozel.exoplayerdrm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var player: ExoPlayer? = null
    private val sessionLogLines = ArrayDeque<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.cryptoInfo.text = try {
            DrmDeviceCryptoInfo.summarize()
        } catch (error: Throwable) {
            getString(R.string.crypto_info_unavailable, error.javaClass.simpleName)
        }
        binding.streamGroup.setOnCheckedChangeListener { _, _ -> startPlayback() }
        binding.forceDefaultLicense.setOnCheckedChangeListener { _, _ -> startPlayback() }
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
        resetSessionLog()
        when (binding.streamGroup.checkedRadioButtonId) {
            R.id.streamWidevineMediaItem -> playGoogleWidevineTest(useSessionManager = false)
            R.id.streamWidevineSessionManager -> playGoogleWidevineTest(useSessionManager = true)
            R.id.streamCustomWidevine -> playCustomWidevine()
            R.id.streamClear -> playClearDash()
            R.id.streamClearKey -> playClearKey()
        }
    }

    @OptIn(UnstableApi::class)
    private fun playGoogleWidevineTest(useSessionManager: Boolean) {
        if (!ensureWidevineCdm()) return
        val headers = customLicenseHeaders()
        val forceDefault = forceDefaultLicenseUri()
        if (useSessionManager) {
            val mediaItem = DrmMediaItems.clearDash(DemoStreams.WIDEVINE_DASH_CENC_H264)
            val drmSessionManager = DrmSessionManagers.widevine(
                licenseUri = DemoStreams.WIDEVINE_UAT_LICENSE_URI,
                licenseRequestHeaders = headers,
                forceDefaultLicenseUri = forceDefault
            )
            player = playerWithDrmManager(drmSessionManager).also { exoPlayer ->
                attachPlayer(exoPlayer, mediaItem)
            }
            showState(
                R.string.state_widevine_test,
                R.color.statusWidevineTest,
                statusWithForceFlag(getString(R.string.status_playing_widevine_manager))
            )
        } else {
            val mediaItem = DrmMediaItems.widevineDash(
                manifestUri = DemoStreams.WIDEVINE_DASH_CENC_H264,
                licenseUri = DemoStreams.WIDEVINE_UAT_LICENSE_URI,
                licenseRequestHeaders = headers,
                forceDefaultLicenseUri = forceDefault
            )
            player = ExoPlayer.Builder(this).build().also { exoPlayer ->
                attachPlayer(exoPlayer, mediaItem)
            }
            showState(
                R.string.state_widevine_test,
                R.color.statusWidevineTest,
                statusWithForceFlag(getString(R.string.status_playing_widevine))
            )
        }
    }

    private fun playCustomWidevine() {
        val licenseUri = BuildConfig.WIDEVINE_LICENSE_URI.trim()
        if (licenseUri.isEmpty()) {
            showState(
                R.string.state_missing_license,
                R.color.statusMissing,
                getString(R.string.status_missing_widevine_license)
            )
            return
        }
        if (!ensureWidevineCdm()) return
        val manifestUri = BuildConfig.WIDEVINE_MANIFEST_URI.trim()
            .ifBlank { DemoStreams.WIDEVINE_DASH_CENC_H264 }
        val mediaItem = DrmMediaItems.widevineDash(
            manifestUri = manifestUri,
            licenseUri = licenseUri,
            licenseRequestHeaders = customLicenseHeaders(),
            forceDefaultLicenseUri = forceDefaultLicenseUri()
        )
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            attachPlayer(exoPlayer, mediaItem)
        }
        showState(
            R.string.state_custom_widevine,
            R.color.statusCustom,
            statusWithForceFlag(getString(R.string.status_playing_custom_widevine, licenseUri))
        )
    }

    private fun playClearDash() {
        val mediaItem = DrmMediaItems.clearDash(DemoStreams.CLEAR_DASH_H264)
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            attachPlayer(exoPlayer, mediaItem)
        }
        showState(
            R.string.state_clear,
            R.color.statusClear,
            getString(R.string.status_playing_clear)
        )
    }

    @OptIn(UnstableApi::class)
    private fun playClearKey() {
        val manifestUri = BuildConfig.CLEARKEY_MANIFEST_URI.trim()
        val licenseUri = BuildConfig.CLEARKEY_LICENSE_URI.trim()
        val keysJson = BuildConfig.CLEARKEY_KEYS_JSON.trim()
        if (manifestUri.isEmpty() || (licenseUri.isEmpty() && keysJson.isEmpty())) {
            showState(
                R.string.state_missing_license,
                R.color.statusMissing,
                getString(R.string.status_clearkey_missing)
            )
            return
        }
        if (!DrmSchemeSupport.isClearKeySupported()) {
            showState(
                R.string.state_scheme_unsupported,
                R.color.statusError,
                getString(R.string.status_clearkey_unsupported)
            )
            return
        }
        if (keysJson.isNotEmpty()) {
            val mediaItem = DrmMediaItems.clearKeyDashLocalKeys(manifestUri)
            player = playerWithDrmManager(DrmSessionManagers.clearKeyLocal(keysJson))
                .also { exoPlayer -> attachPlayer(exoPlayer, mediaItem) }
            showState(
                R.string.state_clearkey,
                R.color.statusCustom,
                getString(R.string.status_playing_clearkey_local)
            )
            return
        }
        val mediaItem = DrmMediaItems.clearKeyDash(
            manifestUri,
            licenseUri,
            forceDefaultLicenseUri = forceDefaultLicenseUri()
        )
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            attachPlayer(exoPlayer, mediaItem)
        }
        showState(
            R.string.state_clearkey,
            R.color.statusCustom,
            statusWithForceFlag(getString(R.string.status_playing_clearkey, licenseUri))
        )
    }

    @OptIn(UnstableApi::class)
    private fun playerWithDrmManager(drmSessionManager: DrmSessionManager): ExoPlayer {
        val mediaSourceFactory = DefaultMediaSourceFactory(this)
            .setDrmSessionManagerProvider { drmSessionManager }
        return ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
    }

    @OptIn(UnstableApi::class)
    private fun attachPlayer(exoPlayer: ExoPlayer, mediaItem: MediaItem) {
        binding.playerView.player = exoPlayer
        val drmLogger = DrmSessionLifecycleLogger { message ->
            binding.drmSessionLog.post { appendSessionLog(message) }
        }
        exoPlayer.addAnalyticsListener(DrmSessionAnalyticsForwarder(drmLogger))
        exoPlayer.addAnalyticsListener(EventLogger(TAG))
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                val state = if (DrmPlaybackErrors.isDrmError(error.errorCode)) {
                    R.string.state_drm_error
                } else {
                    R.string.state_playback_error
                }
                showState(
                    state,
                    R.color.statusError,
                    DrmPlaybackErrors.describe(this@MainActivity, error)
                )
            }
        })
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.playWhenReady = true
        exoPlayer.prepare()
    }

    private fun ensureWidevineCdm(): Boolean {
        if (DrmSchemeSupport.isWidevineSupported()) return true
        showState(
            R.string.state_scheme_unsupported,
            R.color.statusError,
            getString(R.string.status_widevine_unsupported)
        )
        return false
    }

    private fun customLicenseHeaders(): Map<String, String> {
        return LicenseRequestHeaders.parse(BuildConfig.WIDEVINE_LICENSE_REQUEST_HEADERS)
    }

    private fun showState(
        @StringRes stateRes: Int,
        @ColorRes colorRes: Int,
        detail: String
    ) {
        val color = ContextCompat.getColor(this, colorRes)
        binding.playbackState.setText(stateRes)
        binding.playbackState.setTextColor(color)
        binding.status.text = detail
    }

    private fun releasePlayer() {
        binding.playerView.player = null
        player?.release()
        player = null
    }

    private fun forceDefaultLicenseUri(): Boolean = binding.forceDefaultLicense.isChecked

    private fun statusWithForceFlag(base: String): String {
        if (!forceDefaultLicenseUri()) return base
        return base + getString(R.string.status_force_license_suffix)
    }

    private fun resetSessionLog() {
        sessionLogLines.clear()
        binding.drmSessionLog.setText(R.string.drm_session_log_empty)
    }

    private fun appendSessionLog(message: String) {
        sessionLogLines.addLast(message)
        while (sessionLogLines.size > MAX_SESSION_LOG_LINES) {
            sessionLogLines.removeFirst()
        }
        binding.drmSessionLog.text = sessionLogLines.joinToString("\n")
    }

    private companion object {
        const val TAG = "ExoPlayerDrm"
        const val MAX_SESSION_LOG_LINES = 8
    }
}
