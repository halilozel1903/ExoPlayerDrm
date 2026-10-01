package com.halil.ozel.exoplayerdrm

import android.content.Context
import androidx.media3.common.PlaybackException

/**
 * Maps Media3 [PlaybackException] `ERROR_CODE_DRM_*` values to copy the user can act on.
 *
 * These codes come from the player / CDM / license HTTP callback. This sample does not
 * invent a successful license response when acquisition fails.
 */
object DrmPlaybackErrors {

    private const val DRM_ERROR_CODE_MIN = 6000
    private const val DRM_ERROR_CODE_MAX = 6999

    fun describe(context: Context, error: PlaybackException): String {
        val drmCopy = userMessageResId(error.errorCode)?.let { context.getString(it) }
        return if (drmCopy != null) {
            context.getString(R.string.status_drm_error, error.errorCodeName, drmCopy)
        } else {
            context.getString(R.string.status_error, error.errorCodeName)
        }
    }

    fun isDrmError(errorCode: Int): Boolean {
        return errorCode in DRM_ERROR_CODE_MIN..DRM_ERROR_CODE_MAX
    }

    fun userMessageResId(errorCode: Int): Int? {
        return when (errorCode) {
            PlaybackException.ERROR_CODE_DRM_UNSPECIFIED -> R.string.drm_error_unspecified
            PlaybackException.ERROR_CODE_DRM_SCHEME_UNSUPPORTED ->
                R.string.drm_error_scheme_unsupported
            PlaybackException.ERROR_CODE_DRM_PROVISIONING_FAILED ->
                R.string.drm_error_provisioning_failed
            PlaybackException.ERROR_CODE_DRM_CONTENT_ERROR -> R.string.drm_error_content
            PlaybackException.ERROR_CODE_DRM_LICENSE_ACQUISITION_FAILED ->
                R.string.drm_error_license_acquisition
            PlaybackException.ERROR_CODE_DRM_DISALLOWED_OPERATION ->
                R.string.drm_error_disallowed_operation
            PlaybackException.ERROR_CODE_DRM_SYSTEM_ERROR -> R.string.drm_error_system
            PlaybackException.ERROR_CODE_DRM_DEVICE_REVOKED -> R.string.drm_error_device_revoked
            PlaybackException.ERROR_CODE_DRM_LICENSE_EXPIRED -> R.string.drm_error_license_expired
            else -> if (isDrmError(errorCode)) R.string.drm_error_unspecified else null
        }
    }
}
