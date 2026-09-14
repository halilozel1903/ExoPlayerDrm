package com.halil.ozel.exoplayerdrm

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes

/**
 * Builds [MediaItem]s using Media3's current DRM configuration API.
 *
 * Passing a [MediaItem.DrmConfiguration] is enough for ExoPlayer to construct a
 * [androidx.media3.exoplayer.drm.DefaultDrmSessionManager] internally. Do not
 * invent license URLs: Widevine and ClearKey both need a real license endpoint
 * that matches the stream.
 */
object DrmMediaItems {

    fun widevineDash(manifestUri: String, licenseUri: String): MediaItem {
        return MediaItem.Builder()
            .setUri(manifestUri)
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .setDrmConfiguration(
                MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                    .setLicenseUri(licenseUri)
                    .setMultiSession(true)
                    .build()
            )
            .build()
    }

    fun clearKeyDash(manifestUri: String, licenseUri: String): MediaItem {
        return MediaItem.Builder()
            .setUri(manifestUri)
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .setDrmConfiguration(
                MediaItem.DrmConfiguration.Builder(C.CLEARKEY_UUID)
                    .setLicenseUri(licenseUri)
                    .build()
            )
            .build()
    }

    fun clearDash(manifestUri: String): MediaItem {
        return MediaItem.Builder()
            .setUri(manifestUri)
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .build()
    }
}
