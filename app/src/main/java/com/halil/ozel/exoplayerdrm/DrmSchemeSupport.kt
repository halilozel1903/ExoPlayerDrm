package com.halil.ozel.exoplayerdrm

import android.media.MediaDrm
import androidx.media3.common.C

/**
 * Queries the platform CDM. Widevine is typical on Google Play system images;
 * many AOSP emulators report unsupported, which is a real device limitation.
 */
object DrmSchemeSupport {

    fun isWidevineSupported(): Boolean {
        return MediaDrm.isCryptoSchemeSupported(C.WIDEVINE_UUID)
    }

    fun isClearKeySupported(): Boolean {
        return MediaDrm.isCryptoSchemeSupported(C.CLEARKEY_UUID)
    }
}
