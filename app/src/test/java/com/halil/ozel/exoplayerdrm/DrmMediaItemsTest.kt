package com.halil.ozel.exoplayerdrm

import androidx.media3.common.C
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DrmMediaItemsTest {

    @Test
    fun widevineDoesNotForceDefaultLicenseUriByDefault() {
        val item = DrmMediaItems.widevineDash(
            DemoStreams.WIDEVINE_DASH_CENC_H264,
            DemoStreams.WIDEVINE_UAT_LICENSE_URI
        )
        val drm = item.localConfiguration!!.drmConfiguration!!
        assertFalse(drm.forceDefaultLicenseUri)
        assertTrue(drm.multiSession)
    }

    @Test
    fun widevineCanForceTheAppLicenseUri() {
        val item = DrmMediaItems.widevineDash(
            manifestUri = DemoStreams.WIDEVINE_DASH_CENC_H264,
            licenseUri = DemoStreams.WIDEVINE_UAT_LICENSE_URI,
            forceDefaultLicenseUri = true
        )
        assertTrue(item.localConfiguration!!.drmConfiguration!!.forceDefaultLicenseUri)
    }
}
