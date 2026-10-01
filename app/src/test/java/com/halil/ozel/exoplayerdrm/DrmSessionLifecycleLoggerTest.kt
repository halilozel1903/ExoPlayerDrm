package com.halil.ozel.exoplayerdrm

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.drm.DrmSession
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.atomic.AtomicReference

@OptIn(UnstableApi::class)
class DrmSessionLifecycleLoggerTest {

    @Test
    fun labelsKnownDrmSessionStates() {
        assertEquals("OPENING", DrmSessionStates.label(DrmSession.STATE_OPENING))
        assertEquals("OPENED", DrmSessionStates.label(DrmSession.STATE_OPENED))
        assertEquals("OPENED_WITH_KEYS", DrmSessionStates.label(DrmSession.STATE_OPENED_WITH_KEYS))
        assertEquals("ERROR", DrmSessionStates.label(DrmSession.STATE_ERROR))
        assertEquals("RELEASED", DrmSessionStates.label(DrmSession.STATE_RELEASED))
        assertEquals("99", DrmSessionStates.label(99))
    }

    @Test
    fun formatsAcquireAndErrorWithoutKeys() {
        val last = AtomicReference<String>()
        val logger = DrmSessionLifecycleLogger(last::set)

        logger.onDrmSessionAcquired(0, null, DrmSession.STATE_OPENED)
        assertEquals("DRM session acquired window=0 state=OPENED", last.get())

        logger.onDrmSessionManagerError(1, null, IllegalStateException("license HTTP 500"))
        assertEquals(
            "DRM session manager error window=1 IllegalStateException: license HTTP 500",
            last.get()
        )
    }
}
