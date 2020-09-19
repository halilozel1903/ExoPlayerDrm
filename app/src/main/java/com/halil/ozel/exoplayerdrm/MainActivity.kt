package com.halil.ozel.exoplayerdrm

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.drm.*
import com.google.android.exoplayer2.source.dash.DashChunkSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.util.Util
import java.util.*

// DRM URL : https://bitmovin-a.akamaihd.net/content/art-of-motion_drm/mpds/11331.mpd

// NON DRM URL : https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd

class MainActivity : Activity() {

    private var playerView: PlayerView? = null
    private var player: SimpleExoPlayer? = null
    private var trackSelector: DefaultTrackSelector? = null
    private var url: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        playerView = findViewById(R.id.playerView)

        initializePlayer()
    }

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

    // Set the media item to be played.
    private fun buildDashMediaSource(uri: Uri): DashMediaSource {
        val userAgent = "ExoPlayer-Drm"
        val dashChunkSourceFactory: DashChunkSource.Factory = DefaultDashChunkSource.Factory(
                DefaultHttpDataSourceFactory("userAgent", DefaultBandwidthMeter()))
        val manifestDataSourceFactory = DefaultHttpDataSourceFactory(userAgent)
        return DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory).createMediaSource(uri)
    }

    // Drm Manager
    @Throws(UnsupportedDrmException::class)
    private fun buildDrmSessionManager(
            uuid: UUID?, licenseUrl: String, multiSession: Boolean): DefaultDrmSessionManager<FrameworkMediaCrypto?> {
        val licenseDataSourceFactory: HttpDataSource.Factory = DefaultHttpDataSourceFactory(Util.getUserAgent(this, application.packageName))
        val drmCallback = HttpMediaDrmCallback(licenseUrl, licenseDataSourceFactory)
        val mediaDrm = FrameworkMediaDrm.newInstance(uuid)
        return DefaultDrmSessionManager(uuid, mediaDrm, drmCallback, null, multiSession)
    }

    override fun onPause() {
        super.onPause()
        player!!.playWhenReady = false
    }
}