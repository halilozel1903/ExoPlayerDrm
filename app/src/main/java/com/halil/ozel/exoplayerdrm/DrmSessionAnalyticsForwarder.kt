package com.halil.ozel.exoplayerdrm

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.drm.DrmSessionEventListener
import androidx.media3.exoplayer.drm.KeyRequestInfo

/**
 * Forwards ExoPlayer DRM analytics to a [DrmSessionEventListener].
 *
 * Media3 does not expose `ExoPlayer.addDrmSessionEventListener`; the player reports
 * the same callbacks through [AnalyticsListener].
 */
@OptIn(UnstableApi::class)
class DrmSessionAnalyticsForwarder(
    private val drmListener: DrmSessionEventListener
) : AnalyticsListener {

    override fun onDrmSessionAcquired(eventTime: AnalyticsListener.EventTime, state: Int) {
        drmListener.onDrmSessionAcquired(eventTime.windowIndex, eventTime.mediaPeriodId, state)
    }

    override fun onDrmKeysLoaded(eventTime: AnalyticsListener.EventTime, keyRequestInfo: KeyRequestInfo) {
        drmListener.onDrmKeysLoaded(eventTime.windowIndex, eventTime.mediaPeriodId, keyRequestInfo)
    }

    override fun onDrmSessionManagerError(eventTime: AnalyticsListener.EventTime, error: Exception) {
        drmListener.onDrmSessionManagerError(eventTime.windowIndex, eventTime.mediaPeriodId, error)
    }

    override fun onDrmKeysRestored(eventTime: AnalyticsListener.EventTime) {
        drmListener.onDrmKeysRestored(eventTime.windowIndex, eventTime.mediaPeriodId)
    }

    override fun onDrmKeysRemoved(eventTime: AnalyticsListener.EventTime) {
        drmListener.onDrmKeysRemoved(eventTime.windowIndex, eventTime.mediaPeriodId)
    }

    override fun onDrmSessionReleased(eventTime: AnalyticsListener.EventTime) {
        drmListener.onDrmSessionReleased(eventTime.windowIndex, eventTime.mediaPeriodId)
    }
}
