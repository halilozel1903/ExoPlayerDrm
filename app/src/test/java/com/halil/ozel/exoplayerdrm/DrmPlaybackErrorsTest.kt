package com.halil.ozel.exoplayerdrm

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DrmPlaybackErrorsTest {

    @Test
    fun drmRangeIsSixThousand() {
        assertTrue(DrmPlaybackErrors.isDrmError(6000))
        assertTrue(DrmPlaybackErrors.isDrmError(6004))
        assertTrue(DrmPlaybackErrors.isDrmError(6008))
        assertFalse(DrmPlaybackErrors.isDrmError(2000))
        assertFalse(DrmPlaybackErrors.isDrmError(7000))
    }

    @Test
    fun mapsKnownDrmCodesToDistinctCopy() {
        // Integers match androidx.media3.common.PlaybackException ERROR_CODE_DRM_*.
        assertEquals(R.string.drm_error_unspecified, DrmPlaybackErrors.userMessageResId(6000))
        assertEquals(R.string.drm_error_scheme_unsupported, DrmPlaybackErrors.userMessageResId(6001))
        assertEquals(R.string.drm_error_provisioning_failed, DrmPlaybackErrors.userMessageResId(6002))
        assertEquals(R.string.drm_error_content, DrmPlaybackErrors.userMessageResId(6003))
        assertEquals(
            R.string.drm_error_license_acquisition,
            DrmPlaybackErrors.userMessageResId(6004)
        )
        assertEquals(
            R.string.drm_error_disallowed_operation,
            DrmPlaybackErrors.userMessageResId(6005)
        )
        assertEquals(R.string.drm_error_system, DrmPlaybackErrors.userMessageResId(6006))
        assertEquals(R.string.drm_error_device_revoked, DrmPlaybackErrors.userMessageResId(6007))
        assertEquals(R.string.drm_error_license_expired, DrmPlaybackErrors.userMessageResId(6008))
    }

    @Test
    fun nonDrmCodesHaveNoDrmCopy() {
        assertNull(DrmPlaybackErrors.userMessageResId(2004))
    }
}
