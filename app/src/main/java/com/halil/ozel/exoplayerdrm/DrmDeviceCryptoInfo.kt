package com.halil.ozel.exoplayerdrm

import android.media.MediaDrm
import android.media.UnsupportedSchemeException
import androidx.media3.common.C
import java.util.UUID

/**
 * Reads scheme UUID plus Widevine/ClearKey [MediaDrm] properties that this device actually
 * advertises. Missing properties are reported as `n/a` — they are optional vendor strings,
 * not something this sample invents.
 *
 * PlayReady is listed as unsupported on phones; this method does not open a PlayReady CDM.
 */
object DrmDeviceCryptoInfo {

    private val QUERY_PROPERTIES = listOf(
        "securityLevel",
        "hdcpLevel",
        "maxHdcpLevel",
        "version",
        "systemId"
    )

    fun summarize(): String {
        return buildString {
            appendLine(summarizeScheme(C.WIDEVINE_UUID))
            append(summarizeScheme(C.CLEARKEY_UUID))
        }
    }

    fun summarizeScheme(uuid: UUID): String {
        val name = DrmSchemeLabels.nameFor(uuid)
        val supported = try {
            MediaDrm.isCryptoSchemeSupported(uuid)
        } catch (error: Throwable) {
            return "$name $uuid: scheme query failed (${error.javaClass.simpleName})"
        }
        if (!supported) {
            return "$name $uuid: CDM not present on this device"
        }
        val mediaDrm = try {
            MediaDrm(uuid)
        } catch (_: UnsupportedSchemeException) {
            return "$name $uuid: MediaDrm open failed"
        }
        try {
            val properties = QUERY_PROPERTIES.joinToString(", ") { key ->
                "$key=${readProperty(mediaDrm, key)}"
            }
            return "$name $uuid: $properties"
        } finally {
            mediaDrm.release()
        }
    }

    private fun readProperty(mediaDrm: MediaDrm, key: String): String {
        return try {
            val value = mediaDrm.getPropertyString(key)
            if (value.isNullOrBlank()) "n/a" else value
        } catch (_: Exception) {
            "n/a"
        }
    }
}
