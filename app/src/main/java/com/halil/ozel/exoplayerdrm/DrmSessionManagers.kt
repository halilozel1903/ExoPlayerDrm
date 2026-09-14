package com.halil.ozel.exoplayerdrm

import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.drm.DefaultDrmSessionManager
import androidx.media3.exoplayer.drm.DrmSessionManager
import androidx.media3.exoplayer.drm.FrameworkMediaDrm
import androidx.media3.exoplayer.drm.HttpMediaDrmCallback
import java.util.UUID

/**
 * Explicit [DefaultDrmSessionManager] construction for apps that need a custom
 * [androidx.media3.exoplayer.drm.MediaDrmCallback] (headers, provisioning, etc.).
 *
 * Most apps should prefer [MediaItem.DrmConfiguration] instead; ExoPlayer already
 * builds a DefaultDrmSessionManager from that config.
 */
object DrmSessionManagers {

    @OptIn(UnstableApi::class)
    fun httpLicenseServer(schemeUuid: UUID, licenseUri: String): DrmSessionManager {
        val callback = HttpMediaDrmCallback(
            licenseUri,
            DefaultHttpDataSource.Factory().setUserAgent(USER_AGENT)
        )
        return DefaultDrmSessionManager.Builder()
            .setUuidAndExoMediaDrmProvider(schemeUuid, FrameworkMediaDrm.DEFAULT_PROVIDER)
            .setMultiSession(true)
            .build(callback)
    }

    @OptIn(UnstableApi::class)
    fun widevine(licenseUri: String): DrmSessionManager {
        return httpLicenseServer(C.WIDEVINE_UUID, licenseUri)
    }

    private const val USER_AGENT = "ExoPlayerDrm-Media3"
}
