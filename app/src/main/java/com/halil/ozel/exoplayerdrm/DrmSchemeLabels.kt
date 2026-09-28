package com.halil.ozel.exoplayerdrm

import androidx.media3.common.C
import java.util.UUID

/** Maps Media3 DRM UUIDs to scheme names. Unknown UUIDs are shown as-is. */
object DrmSchemeLabels {

    fun nameFor(uuid: UUID): String {
        return when (uuid) {
            C.WIDEVINE_UUID -> "Widevine"
            C.CLEARKEY_UUID -> "ClearKey"
            C.PLAYREADY_UUID -> "PlayReady"
            else -> uuid.toString()
        }
    }
}
