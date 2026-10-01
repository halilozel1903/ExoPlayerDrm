package com.halil.ozel.exoplayerdrm

import androidx.media3.common.C
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class DrmSchemeLabelsTest {

    @Test
    fun labelsWellKnownMedia3Uuids() {
        assertEquals("Widevine", DrmSchemeLabels.nameFor(C.WIDEVINE_UUID))
        assertEquals("ClearKey", DrmSchemeLabels.nameFor(C.CLEARKEY_UUID))
        assertEquals("PlayReady", DrmSchemeLabels.nameFor(C.PLAYREADY_UUID))
    }

    @Test
    fun unknownUuidIsPrintedLiterally() {
        val unknown = UUID.fromString("00000000-0000-0000-0000-000000000001")
        assertEquals(unknown.toString(), DrmSchemeLabels.nameFor(unknown))
    }

    @Test
    fun widevineUuidMatchesThePublishedCencScheme() {
        assertEquals(
            UUID.fromString("edef8ba9-79d6-4ace-a3c8-27dcd51d21ed"),
            C.WIDEVINE_UUID
        )
    }
}
