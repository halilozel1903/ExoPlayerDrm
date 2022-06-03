package com.halil.ozel.exoplayerdrm

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.dash.DashChunkSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.MimeTypes
import com.halil.ozel.exoplayerdrm.databinding.ActivityMainBinding

/** DRM URL : https://bitmovin-a.akamaihd.net/content/art-of-motion_drm/mpds/11331.mpd **/
/** NON DRM URL : https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd **/

class MainActivity : Activity() {

    private lateinit var playerView: ExoPlayer
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initializePlayer()
    }


    private fun initializePlayer() {
        val url = "https://bitmovin-a.akamaihd.net/content/art-of-motion_drm/mpds/11331.mpd"
        val drmLicenseUrl = "https://proxy.uat.widevine.com/proxy?provider=widevine_test"
        val drmSchemeUuid = C.WIDEVINE_UUID // DRM Type
        val userAgent = "ExoPlayer-Drm"

        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(userAgent)
            .setTransferListener(
                DefaultBandwidthMeter.Builder(this)
                    .setResetOnNetworkTypeChange(false)
                    .build()
            )

        val dashChunkSourceFactory: DashChunkSource.Factory = DefaultDashChunkSource.Factory(
            defaultHttpDataSourceFactory
        )
        val manifestDataSourceFactory = DefaultHttpDataSource.Factory().setUserAgent(userAgent)
        val dashMediaSource =
            DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory)
                .createMediaSource(
                    MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        // DRM Configuration
                        .setDrmConfiguration(
                            MediaItem.DrmConfiguration.Builder(drmSchemeUuid)
                                .setLicenseUri(drmLicenseUrl).build()
                        )
                        .setMimeType(MimeTypes.APPLICATION_MPD)
                        .setTag(null)
                        .build()
                )


        // Prepare the player.
        playerView = ExoPlayer.Builder(this)
            .setSeekForwardIncrementMs(10000)
            .setSeekBackIncrementMs(10000)
            .build()
        playerView.playWhenReady = true
        binding.playerView.player = playerView
        playerView.setMediaSource(dashMediaSource, true)
        playerView.prepare()
    }

    override fun onPause() {
        super.onPause()
        playerView.playWhenReady = false
    }
}