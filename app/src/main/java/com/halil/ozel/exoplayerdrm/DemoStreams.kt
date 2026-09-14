package com.halil.ozel.exoplayerdrm

/**
 * Test streams published by Google for the AndroidX Media3 demo app
 * (`demos/main/src/main/assets/media.exolist.json` on the media `release` branch).
 *
 * These are **not** production DRM credentials. The Widevine license URI is Google's
 * UAT test proxy; it can rate-limit, change, or stop serving licenses. For your own
 * content you must supply a matching license server URL from your DRM provider.
 */
object DemoStreams {
    const val WIDEVINE_DASH_CENC_H264: String =
        "https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd"

    const val CLEAR_DASH_H264: String =
        "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd"

    const val WIDEVINE_UAT_LICENSE_URI: String =
        "https://proxy.uat.widevine.com/proxy?video_id=2015_tears&provider=widevine_test"
}
