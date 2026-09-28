# ExoPlayer DRM (AndroidX Media3)

Sample Android app that plays **DASH** with **Widevine** and **ClearKey** using
[AndroidX Media3](https://developer.android.com/media/media3) **1.11.1**
(`androidx.media3:media3-exoplayer`, `media3-exoplayer-dash`, `media3-ui`).

ExoPlayer 2 (`com.google.android.exoplayer`) is not used. DRM is configured with
the current Media3 APIs: `MediaItem.DrmConfiguration`, `DefaultDrmSessionManager`,
`HttpMediaDrmCallback`, and (for developer-supplied ClearKey JSON)
`LocalMediaDrmCallback`. Player failures in the DRM range are shown as
`PlaybackException.ERROR_CODE_DRM_*`.

## What works out of the box vs what you must supply

| Piece | Out of the box | You must supply |
| --- | --- | --- |
| Media3 player + DASH | Yes | — |
| Clear DASH (Tears of Steel) | Yes | — |
| Widevine **Google test** DASH + UAT license proxy | Yes (labeled as test) | Nothing, but the proxy is **not** a production license server; it can fail, rate-limit, or disappear |
| Custom Widevine (`drm.widevine.licenseUri`) | UI state **MISSING LICENSE URL** until set | A real license server URL that matches **your** content (optional manifest + license request headers) |
| `MediaItem.DrmConfiguration` / `DefaultDrmSessionManager` | Yes (code) | Matching license URI for your content |
| Widevine CDM | Device-dependent | Google Play system images usually include it; many AOSP emulators do not (`MediaDrm.isCryptoSchemeSupported`) |
| ClearKey HTTP license | API + empty Gradle placeholders | ClearKey-protected manifest **and** `drm.clearkey.licenseUri` |
| ClearKey local keys | API + `LocalMediaDrmCallback` | Your own W3C ClearKey JSON via `drm.clearkey.keysFile` (gitignored). **No keys are hardcoded** |
| Production DRM / PlayReady on a phone | No | PlayReady SL2000 is documented for Android TV, not this sample |

This sample does **not** invent license servers, wrap keys, or fake `MediaDrm`.

## UI states

The label above the status text is one of:

- **CLEAR** — unencrypted Google test DASH
- **WIDEVINE · GOOGLE TEST** — Media3 demo Tears of Steel + `proxy.uat.widevine.com`
- **WIDEVINE · CUSTOM LICENSE** — Gradle `drm.widevine.licenseUri` is set
- **MISSING LICENSE URL** — custom Widevine or ClearKey selected without a license URL or keys JSON; playback is **not** started
- **CLEARKEY** — developer-supplied license URL or local keys JSON
- **CDM UNSUPPORTED** — `MediaDrm.isCryptoSchemeSupported` is false (no fake fallback)
- **DRM ERROR** — `ERROR_CODE_DRM_*` from the player
- **PLAYBACK ERROR** — other Media3 `PlaybackException` codes

## Supported DRM schemes (Media3 / Android)

From [Media3 DRM documentation](https://developer.android.com/media/media3/exoplayer/drm):

| Scheme | Typical API level | Formats |
| --- | --- | --- |
| Widevine `cenc` | 19+ | DASH, HLS (FMP4) |
| Widevine `cbcs` | 25+ | DASH, HLS (FMP4) |
| ClearKey `cenc` | 21+ | DASH |
| PlayReady SL2000 | Android TV | DASH, SmoothStreaming, HLS (FMP4) |

PlayReady is **not** implemented here; phones do not expose that stack the way Android TV does.

## Default test content

The Widevine and clear DASH URLs are the **Tears of Steel** assets published for the
[AndroidX Media3 demo](https://github.com/androidx/media/blob/release/demos/main/src/main/assets/media.exolist.json):

- Encrypted: `https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd`
- Clear: `https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd`
- Widevine license (Google UAT **test** proxy):  
  `https://proxy.uat.widevine.com/proxy?video_id=2015_tears&provider=widevine_test`

Those URLs are used **only** by the two “Google test” radios. Custom Widevine does
not silently fall back to the UAT proxy.

## Build and run

Requirements:

- Android Studio Ladybug / AGP 8.11-compatible IDE, or JDK 17+
- `compileSdk` **36** (required by Media3 1.11.1 AARs) and Kotlin **2.2.10**
- Android device or emulator, **API 24+**
- For Widevine: a device/emulator image that includes Widevine (Google Play system images are more likely than AOSP)

```bash
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
```

In the app:

1. **Widevine DASH — Google test (MediaItem.DrmConfiguration)** — recommended Media3 path.
2. **Widevine DASH — Google test (DefaultDrmSessionManager)** — same stream with
   `HttpMediaDrmCallback` installed on `DefaultMediaSourceFactory`.
3. **Custom Widevine DASH** — requires `drm.widevine.licenseUri`; otherwise **MISSING LICENSE URL**.
4. **Clear DASH** — same title without DRM.
5. **ClearKey DASH** — requires your license URL or keys JSON.

## Supplying your own license server or ClearKey keys

Add properties to `gradle.properties` (or pass `-P` on the Gradle command line) and rebuild:

```properties
drm.widevine.manifestUri=https://your-cdn.example/stream.mpd
drm.widevine.licenseUri=https://your-widevine-license.example/license
drm.widevine.licenseRequestHeaders=Authorization=Bearer your-token

drm.clearkey.manifestUri=https://your-cdn.example/clearkey.mpd
drm.clearkey.licenseUri=https://your-clearkey-license.example/license
```

For ClearKey **without** a license HTTP server, put a W3C ClearKey response in a
local file (already gitignored as `local-clearkey.json`) and point Gradle at it:

```properties
drm.clearkey.manifestUri=https://your-cdn.example/clearkey.mpd
drm.clearkey.keysFile=local-clearkey.json
```

Expected JSON shape (fill in **your** `k` / `kid` values; do not commit production keys):

```json
{"keys":[{"kty":"oct","k":"<base64url-key>","kid":"<base64url-kid>"}]}
```

License request headers also belong on
`MediaItem.DrmConfiguration.Builder.setLicenseRequestHeaders` or
`HttpMediaDrmCallback.setKeyRequestProperty`. This sample does not add fake auth.

## Media3 version

| Module | Version | Maven |
| --- | --- | --- |
| `androidx.media3:media3-exoplayer` | 1.11.1 | [Google Maven](https://dl.google.com/dl/android/maven2/androidx/media3/media3-exoplayer/1.11.1/media3-exoplayer-1.11.1.pom) |
| `androidx.media3:media3-exoplayer-dash` | 1.11.1 | same group |
| `androidx.media3:media3-ui` | 1.11.1 | same group |

Declared in `gradle/libs.versions.toml`.

## Docs

- [Media3 DRM](https://developer.android.com/media/media3/exoplayer/drm)
- [Media3 releases](https://developer.android.com/jetpack/androidx/releases/media3)
- [Media3 migration](https://developer.android.com/media/media3/exoplayer/migration-guide)
