package com.halil.ozel.exoplayerdrm

/**
 * Parses optional Widevine license HTTP headers from a Gradle property.
 *
 * Format: `Header-Name=value|Another-Header=value`. Empty input yields no headers.
 * Do not put production tokens in the public repo.
 */
object LicenseRequestHeaders {

    fun parse(spec: String): Map<String, String> {
        if (spec.isBlank()) return emptyMap()
        return spec.split('|')
            .mapNotNull { part ->
                val trimmed = part.trim()
                val separator = trimmed.indexOf('=')
                if (separator <= 0) {
                    null
                } else {
                    val name = trimmed.substring(0, separator).trim()
                    val value = trimmed.substring(separator + 1).trim()
                    if (name.isEmpty()) null else name to value
                }
            }
            .toMap()
    }
}
