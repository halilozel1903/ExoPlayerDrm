package com.halil.ozel.exoplayerdrm

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.drm.DrmSession
import androidx.media3.exoplayer.drm.DrmSessionEventListener
import androidx.media3.exoplayer.source.MediaSource

/**
 * Formats [DrmSessionEventListener] callbacks for on-screen / logcat diagnostics.
 *
 * These events do not contain license keys. ExoPlayer fires them as sessions are
 * acquired, keyed, and released — including retries that never reach [Player.Listener].
 */
@OptIn(UnstableApi::class)
class DrmSessionLifecycleLogger(
    private val onMessage: (String) -> Unit
) : DrmSessionEventListener {

    override fun onDrmSessionAcquired(
        windowIndex: Int,
        mediaPeriodId: MediaSource.MediaPeriodId?,
        state: Int
    ) {
        onMessage("DRM session acquired window=$windowIndex state=${DrmSessionStates.label(state)}")
    }

    override fun onDrmKeysLoaded(
        windowIndex: Int,
        mediaPeriodId: MediaSource.MediaPeriodId?
    ) {
        onMessage("DRM keys loaded window=$windowIndex")
    }

    override fun onDrmSessionManagerError(
        windowIndex: Int,
        mediaPeriodId: MediaSource.MediaPeriodId?,
        error: Exception
    ) {
        onMessage(
            "DRM session manager error window=$windowIndex ${error.javaClass.simpleName}: ${error.message}"
        )
    }

    override fun onDrmKeysRestored(
        windowIndex: Int,
        mediaPeriodId: MediaSource.MediaPeriodId?
    ) {
        onMessage("DRM keys restored window=$windowIndex")
    }

    override fun onDrmKeysRemoved(
        windowIndex: Int,
        mediaPeriodId: MediaSource.MediaPeriodId?
    ) {
        onMessage("DRM keys removed window=$windowIndex")
    }

    override fun onDrmSessionReleased(
        windowIndex: Int,
        mediaPeriodId: MediaSource.MediaPeriodId?
    ) {
        onMessage("DRM session released window=$windowIndex")
    }
}

@OptIn(UnstableApi::class)
object DrmSessionStates {
    fun label(state: Int): String {
        return when (state) {
            DrmSession.STATE_OPENING -> "OPENING"
            DrmSession.STATE_OPENED -> "OPENED"
            DrmSession.STATE_OPENED_WITH_KEYS -> "OPENED_WITH_KEYS"
            DrmSession.STATE_ERROR -> "ERROR"
            DrmSession.STATE_RELEASED -> "RELEASED"
            else -> state.toString()
        }
    }
}
