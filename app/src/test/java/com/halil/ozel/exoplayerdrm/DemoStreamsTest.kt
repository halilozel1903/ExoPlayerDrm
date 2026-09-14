package com.halil.ozel.exoplayerdrm

import org.junit.Assert.assertTrue
import org.junit.Test

class DemoStreamsTest {

    @Test
    fun widevineTestAssetsUseHttpsGoogleHost() {
        assertTrue(DemoStreams.WIDEVINE_DASH_CENC_H264.startsWith("https://storage.googleapis.com/wvmedia/"))
        assertTrue(DemoStreams.CLEAR_DASH_H264.startsWith("https://storage.googleapis.com/wvmedia/"))
        assertTrue(DemoStreams.WIDEVINE_UAT_LICENSE_URI.startsWith("https://proxy.uat.widevine.com/"))
    }
}
