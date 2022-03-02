## What is DRM? 🤔

![Screenshot](https://miro.medium.com/max/1400/1*LtFxnStWjom2xYQvZsHFow.png)

Digital rights management (DRM) is a way to protect copyrights for digital media. This approach includes the use of technologies that limit the copying and use of copyrighted works and proprietary software.

In a way, digital rights management allows publishers or authors to control what paying users can do with their works. For companies, implementing digital rights management systems or processes can help to prevent users from accessing or using certain assets, allowing the organization to avoid legal issues that arise from unauthorized use. Today, DRM is playing a growing role in data security.

With the rise of peer-to-peer file exchange services such as torrent sites, online piracy has been the bane of copyrighted material. DRM technologies do not catch those who engage in piracy. Instead, they make it impossible to steal or share the content in the first place.


For more please read this article : https://streaminglearningcenter.com/articles/what-is-drm.html <br> <br>

## Digital rights management - ExoPlayer 📺

ExoPlayer uses Android’s ```MediaDrm``` API to support DRM protected playbacks. 

The minimum Android versions required for different supported DRM schemes, along with the streaming formats for which they’re supported, are:

DRM scheme	 | Android version number	 | Android API level | Supported formats
------------ | ------------- | ------------ | -------------
Widevine “cenc”	 | 4.4	| 19 | DASH, HLS (FMP4 only)
Widevine “cbcs” | 7.1 | 25 | DASH, HLS (FMP4 only)
ClearKey | 5.0 | 21 | DASH
PlayReady SL2000 | AndroidTV | AndroidTV	| DASH, SmoothStreaming, HLS (FMP4 only) 

<br>

In order to play DRM protected content with ExoPlayer, the UUID of the DRM system and the license server URI should be specified when building a media item. 
The player will then use these properties to build a default implementation of ```DrmSessionManager```, called ```DefaultDrmSessionManager```, 
that’s suitable for most use cases. For some use cases additional DRM properties may be necessary, as outlined in the sections below.

For more please read document : https://exoplayer.dev/drm.html <br> <br>

## How to use DRM in ExoPlayer ⁉️

In order to play a Drm video in Exoplayer, we need to have a DASH(.mdp) type video url. We will decode the encrypted video and play it. 

First of all, we can start our example by following the steps below.

### Step - 1️⃣

We are creating an Android project in the Kotlin language.

### Step - 2️⃣

We add the internet permission to the Android Manifest file.

```kotlin
<uses-permission android:name="android.permission.INTERNET"/>
```

### Step - 3️⃣

We add the link of the ExoPlayer library to the .build gradle file.

```kotlin 
implementation 'com.google.android.exoplayer:exoplayer:2.10.1'
```


### Step - 4️⃣

If not enabled already, you need to turn on Java 8 support in all build.gradle files depending on ExoPlayer, by adding the following to the android section:

```kotlin
compileOptions {
        targetCompatibility JavaVersion.VERSION_1_8
}
```

### Step - 5️⃣

We add playerView to the ```activity_main.xml``` file.


```kotlin 
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <com.google.android.exoplayer2.ui.PlayerView
        android:focusable="true"
        android:id="@+id/playerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```


### Step - 6️⃣

Player, url and Track Selector variables have been defined.

```kotlin
private var playerView: PlayerView? = null
private var player: SimpleExoPlayer? = null
private var trackSelector: DefaultTrackSelector? = null
private var url: String? = null
```

### Step - 7️⃣

A function has been created for the Media Source operation.

   ```kotlin
    private fun buildDashMediaSource(uri: Uri): DashMediaSource {
        val userAgent = "ExoPlayer-Drm"
        val dashChunkSourceFactory: DashChunkSource.Factory = DefaultDashChunkSource.Factory(
                DefaultHttpDataSourceFactory("userAgent", DefaultBandwidthMeter()))
        val manifestDataSourceFactory = DefaultHttpDataSourceFactory(userAgent)
        return DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory).createMediaSource(uri)
    }
 ```
 
 ### Step - 8️⃣
 
 We have created a method in which the necessary operations are performed to play a drm type video.
 
   ```kotlin
    @Throws(UnsupportedDrmException::class)
    private fun buildDrmSessionManager(
            uuid: UUID?, licenseUrl: String, multiSession: Boolean): DefaultDrmSessionManager<FrameworkMediaCrypto?> {
        val licenseDataSourceFactory: HttpDataSource.Factory = DefaultHttpDataSourceFactory(Util.getUserAgent(this, application.packageName))
        val drmCallback = HttpMediaDrmCallback(licenseUrl, licenseDataSourceFactory)
        val mediaDrm = FrameworkMediaDrm.newInstance(uuid)
        return DefaultDrmSessionManager(uuid, mediaDrm, drmCallback, null, multiSession)
    }
 ```
 
  ### Step - 9️⃣
  
  Added url, drm license to play. It is made ready to be played.
  
  Drm License Url : https://proxy.uat.widevine.com/proxy?provider=widevine_test
  
  Drm Url : https://bitmovin-a.akamaihd.net/content/art-of-motion_drm/mpds/11331.mpd
  
 ```kotlin 
  private fun initializePlayer() {

        url = "https://bitmovin-a.akamaihd.net/content/art-of-motion_drm/mpds/11331.mpd"
        var drmSessionManager: DefaultDrmSessionManager<FrameworkMediaCrypto?>? = null
        val drmLicenseUrl = "https://proxy.uat.widevine.com/proxy?provider=widevine_test"
        val drmSchemeUuid = Util.getDrmUuid(C.WIDEVINE_UUID.toString())


        try {
            drmSessionManager = buildDrmSessionManager(
                    drmSchemeUuid, drmLicenseUrl, true)
        } catch (e: UnsupportedDrmException) {
            e.printStackTrace()
        }


        if (player == null) {
            trackSelector = DefaultTrackSelector()
            trackSelector!!.setParameters(trackSelector!!.buildUponParameters().setMaxVideoSize(200, 200))
            player = ExoPlayerFactory.newSimpleInstance(applicationContext, trackSelector, DefaultLoadControl(), drmSessionManager)

            // Bind the player to the view.
            playerView!!.player = player

            player!!.playWhenReady = true
        }

        // Build the media item.
        val dashMediaSource = buildDashMediaSource(Uri.parse(url))

        // Prepare the player.
        player!!.prepare(dashMediaSource, true, false)
    }
  ```

### Step - 🔟

Call the ```initializePlayer()``` function inside `onCreate.`

  ```kotlin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        playerView = findViewById(R.id.playerView)

        initializePlayer()
    }
```
<br>

### Result 📌

Yes ✅ The url in DRM type played smoothly. All dash type contents are played on the player with the Drm setting.

<img src="https://github.com/halilozel1903/ExoPlayerDrm/blob/master/dash_drm1.png" width="250" /> <img src="https://github.com/halilozel1903/ExoPlayerDrm/blob/master/dash_drm2.png" width="250" /> <br>

<br>

**But** ```drmSessionManager```  cannot be played when not in use.

```kotlin
 trackSelector!!.setParameters(trackSelector!!.buildUponParameters().setMaxVideoSize(200, 200))
 ``` 
<br>

<img src="https://github.com/halilozel1903/ExoPlayerDrm/blob/master/drm_not_config1.png" width="250" /> <img src="https://github.com/halilozel1903/ExoPlayerDrm/blob/master/drm_not_config2.png" width="250" /> <br>

<br>

## Resources 📚
- https://exoplayer.dev/drm.html
- https://streaminglearningcenter.com/articles/what-is-drm.html
- https://digitalguardian.com/blog/what-digital-rights-management
- https://bitmovin.com/demos/stream-test?format=dash&manifest=https%3A%2F%2Fbitmovin-a.akamaihd.net%2Fcontent%2Fart-of-motion_drm%2Fmpds%2F11331.mpd
<br>

## License 📋
```
MIT License

Copyright (c) 2022 Halil OZEL

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
