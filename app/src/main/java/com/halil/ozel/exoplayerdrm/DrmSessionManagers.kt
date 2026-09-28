package com.halil.ozel.exoplayerdrm

import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.drm.DefaultDrmSessionManager
import androidx.media3.exoplayer.drm.DrmSessionManager
import androidx.media3.exoplayer.drm.FrameworkMediaDrm
import androidx.media3.exoplayer.drm.HttpMediaDrmCallback
import androidx.media3.exoplayer.drm.LocalMediaDrmCallback
import java.nio.charset.StandardCharsets
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
    fun httpLicenseServer(
        schemeUuid: UUID,
        licenseUri: String,
        licenseRequestHeaders: Map<String, String> = emptyMap(),
        forceDefaultLicenseUri: Boolean = false
    ): DrmSessionManager {
        val callback = HttpMediaDrmCallback(
            licenseUri,
            forceDefaultLicenseUri,
            DefaultHttpDataSource.Factory().setUserAgent(USER_AGENT)
        )
        licenseRequestHeaders.forEach { (name, value) ->
            callback.setKeyRequestProperty(name, value)
        }
        return DefaultDrmSessionManager.Builder()
            .setUuidAndExoMediaDrmProvider(schemeUuid, FrameworkMediaDrm.DEFAULT_PROVIDER)
            .setMultiSession(true)
            .setForceDefaultLicenseUri(forceDefaultLicenseUri)
            .setLoadErrorHandlingPolicy(LicenseHttpRetryPolicy())
            .build(callback)
    }

    @OptIn(UnstableApi::class)
    fun widevine(
        licenseUri: String,
        licenseRequestHeaders: Map<String, String> = emptyMap(),
        forceDefaultLicenseUri: Boolean = false
    ): DrmSessionManager {
        return httpLicenseServer(
            C.WIDEVINE_UUID,
            licenseUri,
            licenseRequestHeaders,
            forceDefaultLicenseUri
        )
    }

    /**
     * ClearKey with a developer-supplied W3C key response body. [keysJson] must be
     * provided at build time; this method does not embed sample production keys.
     */
    @OptIn(UnstableApi::class)
    fun clearKeyLocal(keysJson: String): DrmSessionManager {
        val callback = LocalMediaDrmCallback(keysJson.toByteArray(StandardCharsets.UTF_8))
        return DefaultDrmSessionManager.Builder()
            .setUuidAndExoMediaDrmProvider(C.CLEARKEY_UUID, FrameworkMediaDrm.DEFAULT_PROVIDER)
            .build(callback)
    }

    private const val USER_AGENT = "ExoPlayerDrm-Media3"
}
