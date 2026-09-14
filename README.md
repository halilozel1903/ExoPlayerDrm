# ExoPlayer DRM (AndroidX Media3)

Sample Android app that plays **DASH** with **Widevine** and **ClearKey** using
[AndroidX Media3](https://developer.android.com/media/media3) **1.11.1**
(`androidx.media3:media3-exoplayer`, `media3-exoplayer-dash`, `media3-ui`).

ExoPlayer 2 (`com.google.android.exoplayer`) is not used. DRM is configured with
the current Media3 APIs: `MediaItem.DrmConfiguration`, and optionally an explicit
`DefaultDrmSessionManager` + `HttpMediaDrmCallback`.

## What this sample demonstrates vs what you must supply

| Piece | Out of the box | You must supply |
| --- | --- | --- |
| Media3 player + DASH | Yes | — |
| `MediaItem.DrmConfiguration` for Widevine / ClearKey | Yes (code) | Matching license URI for **your** content |
| `DefaultDrmSessionManager` HTTP license callback | Yes (code) | Same license URI as above |
| Widevine CDM on the device | Device-dependent (most phones) | Hardware/security level is not controllable from the app |
| Google Widevine **test** DASH + UAT license proxy | Used as the default stream | **Not** a production license server; it can fail, rate-limit, or disappear |
| ClearKey playback | API only | ClearKey-protected manifest **and** a license URL (or your own `MediaDrmCallback`) that returns keys for that stream |
| Production DRM (Widevine/PlayReady from a CDN) | No | Your packager, license service, and auth headers |

This sample does **not** invent license servers, wrap keys, or fake `MediaDrm`.
ClearKey keys are **not** hardcoded.

## Supported DRM schemes (Media3 / Android)

From [Media3 DRM documentation](https://developer.android.com/media/media3/exoplayer/drm):

| Scheme | Typical API level | Formats |
| --- | --- | --- |
| Widevine `cenc` | 19+ | DASH, HLS (FMP4) |
| Widevine `cbcs` | 25+ | DASH, HLS (FMP4) |
| ClearKey `cenc` | 21+ | DASH |
| PlayReady SL2000 | Android TV | DASH, SmoothStreaming, HLS (FMP4) |

PlayReady is not demonstrated here; it is not available on standard phones.

## Default test content

The Widevine and clear DASH URLs are the **Tears of Steel** assets published for the
[AndroidX Media3 demo](https://github.com/androidx/media/blob/release/demos/main/src/main/assets/media.exolist.json):

- Encrypted: `https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd`
- Clear: `https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd`
- Widevine license (Google UAT **test** proxy):  
  `https://proxy.uat.widevine.com/proxy?video_id=2015_tears&provider=widevine_test`

If Widevine playback fails, the UI shows the Media3 `PlaybackException` error code.
Common causes: the UAT proxy rejecting the device, no Widevine CDM, network blocks,
or a security-level mismatch. That is expected for a public test proxy.

## Build and run

Requirements:

- Android Studio Ladybug / AGP 8.11-compatible IDE, or JDK 17+
- Android device or emulator, **API 24+**
- For Widevine: a device/emulator image that includes Widevine (Google Play system images are more likely than AOSP)

```bash
./gradlew :app:assembleDebug
```

Install the debug APK on a device, or run the `app` configuration from Android Studio.

In the app:

1. **Widevine DASH (MediaItem.DrmConfiguration)** — recommended Media3 path. The player
   builds `DefaultDrmSessionManager` from the media item.
2. **Widevine DASH (DefaultDrmSessionManager)** — same stream, but the app installs
   `HttpMediaDrmCallback` itself via `DefaultMediaSourceFactory.setDrmSessionManagerProvider`.
3. **Clear DASH** — same title without DRM (no license server).
4. **ClearKey DASH** — disabled until you set Gradle properties (below).

## Supplying your own license server

Add properties to `gradle.properties` (or pass `-P` on the Gradle command line) and rebuild:

```properties
drm.widevine.manifestUri=https://your-cdn.example/stream.mpd
drm.widevine.licenseUri=https://your-widevine-license.example/license

drm.clearkey.manifestUri=https://your-cdn.example/clearkey.mpd
drm.clearkey.licenseUri=https://your-clearkey-license.example/license
```

Empty ClearKey properties leave that radio option as a documented no-op so the sample
never pretends to decrypt without keys.

License request headers (tokens, cookies) belong on `MediaItem.DrmConfiguration.Builder.setLicenseRequestHeaders`
or on a custom `MediaDrmCallback`. This sample does not add fake auth.

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
