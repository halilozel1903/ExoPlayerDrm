package com.halil.ozel.exoplayerdrm

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LicenseRequestHeadersTest {

    @Test
    fun emptySpecYieldsNoHeaders() {
        assertTrue(LicenseRequestHeaders.parse("").isEmpty())
        assertTrue(LicenseRequestHeaders.parse("   ").isEmpty())
    }

    @Test
    fun parsesPipeSeparatedNameValuePairs() {
        val headers = LicenseRequestHeaders.parse(
            "Authorization=Bearer test-token|X-Custom=abc"
        )
        assertEquals("Bearer test-token", headers["Authorization"])
        assertEquals("abc", headers["X-Custom"])
        assertEquals(2, headers.size)
    }

    @Test
    fun ignoresEntriesWithoutEquals() {
        val headers = LicenseRequestHeaders.parse("valid=1|not-a-header")
        assertEquals(mapOf("valid" to "1"), headers)
    }
}
