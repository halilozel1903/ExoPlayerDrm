package com.halil.ozel.exoplayerdrm

import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.upstream.DefaultLoadErrorHandlingPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(UnstableApi::class)
class LicenseHttpRetryPolicyTest {

    @Test
    fun drmLoadsRetryMoreThanTheDefaultMediaPolicy() {
        val policy = LicenseHttpRetryPolicy()
        val baseline = DefaultLoadErrorHandlingPolicy()
        assertEquals(
            LicenseHttpRetryPolicy.DEFAULT_DRM_RETRIES,
            policy.getMinimumLoadableRetryCount(C.DATA_TYPE_DRM)
        )
        assertTrue(
            policy.getMinimumLoadableRetryCount(C.DATA_TYPE_DRM) >
                baseline.getMinimumLoadableRetryCount(C.DATA_TYPE_DRM)
        )
    }

    @Test
    fun nonDrmLoadsKeepTheParentRetryCount() {
        val policy = LicenseHttpRetryPolicy()
        val baseline = DefaultLoadErrorHandlingPolicy()
        assertEquals(
            baseline.getMinimumLoadableRetryCount(C.DATA_TYPE_MEDIA),
            policy.getMinimumLoadableRetryCount(C.DATA_TYPE_MEDIA)
        )
    }
}
