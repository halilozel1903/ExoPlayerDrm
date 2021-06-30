package com.halil.ozel.exoplayerdrm

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.dash.DashChunkSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.MimeTypes

// DRM URL : https://bitmovin-a.akamaihd.net/content/art-of-motion_drm/mpds/11331.mpd
// NON DRM URL : https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd

class MainActivity : Activity() {

    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        playerView = findViewById(R.id.playerView)

        initializePlayer()
    }

    private val context: Context
        get() = this.applicationContext as Context

    private fun initializePlayer() {

        val url = "https://bitmovin-a.akamaihd.net/content/art-of-motion_drm/mpds/11331.mpd"
        val drmLicenseUrl = "https://proxy.uat.widevine.com/proxy?provider=widevine_test"
        val drmSchemeUuid = C.WIDEVINE_UUID
        val userAgent = "ExoPlayer-Drm"
//        val userAgent = "userAgent"

        val trackSelector = DefaultTrackSelector(context)
        trackSelector.setParameters(
            trackSelector.buildUponParameters()
                .setMaxVideoSize(200, 200)
        )

        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(userAgent)
            .setTransferListener(
                DefaultBandwidthMeter.Builder(context)
                    .setResetOnNetworkTypeChange(false)
                    .build()
            )

        val dashChunkSourceFactory: DashChunkSource.Factory = DefaultDashChunkSource.Factory(
            defaultHttpDataSourceFactory
        )
        val manifestDataSourceFactory = DefaultHttpDataSource.Factory().setUserAgent(userAgent)
        val dashMediaSource = DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory)
            .createMediaSource(
                MediaItem.Builder()
                    .setUri(Uri.parse(url))
                    .setDrmUuid(drmSchemeUuid)
                    .setDrmMultiSession(true)
                    .setDrmLicenseUri(drmLicenseUrl)
                    .setMimeType(MimeTypes.APPLICATION_MPD)
                    .setTag(null)
                    .build()
            )

        val defaultLoadControl = DefaultLoadControl()
        val player: SimpleExoPlayer = SimpleExoPlayer.Builder(context)
            .setLoadControl(defaultLoadControl)
            .setTrackSelector(trackSelector)
            .build()

        // Bind the player to the view.
        playerView.player = player

        player.playWhenReady = true

        // Build the media item.

        // Prepare the player.
        player.setMediaSource(dashMediaSource, true)
        player.prepare()
    }

    override fun onPause() {
        super.onPause()
        playerView.player?.playWhenReady = false
    }
}