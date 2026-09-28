package com.halil.ozel.exoplayerdrm

import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.upstream.DefaultLoadErrorHandlingPolicy

/**
 * Extra retries for DRM license/provision HTTP inside [androidx.media3.exoplayer.drm.DefaultDrmSessionManager].
 *
 * This policy is installed only when the app constructs that manager itself. The
 * MediaItem.DrmConfiguration player path still uses ExoPlayer's default DRM load policy.
 */
@OptIn(UnstableApi::class)
class LicenseHttpRetryPolicy(
    private val drmMinimumRetries: Int = DEFAULT_DRM_RETRIES
) : DefaultLoadErrorHandlingPolicy() {

    override fun getMinimumLoadableRetryCount(dataType: Int): Int {
        if (dataType == C.DATA_TYPE_DRM) {
            return drmMinimumRetries
        }
        return super.getMinimumLoadableRetryCount(dataType)
    }

    companion object {
        const val DEFAULT_DRM_RETRIES = 4
    }
}
