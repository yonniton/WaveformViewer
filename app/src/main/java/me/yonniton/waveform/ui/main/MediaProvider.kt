package me.yonniton.waveform.ui.main

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import me.yonniton.waveform.R

class MediaProvider(
    private val context: Context,
    uriAudio: Uri = Uri.fromFile(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES)
            .resolve("alert.mp3")
    )
) {

    var player: ExoPlayer? = null
        set(value) {
            field?.release()
            field = value
        }

    private var uriAudio: Uri

    init {
        this.player = SimpleExoPlayer.Builder(context)
            .build()
        this.uriAudio = uriAudio
    }

    fun setMediaSource(uri: Uri) {
        uri.takeIf {
            context.contentResolver.getType(it)
                ?.matches(Regex("""audio/\w+""")) == true
        }?.also {
            uriAudio = it
        } ?: run {
            Toast.makeText(
                context,
                "could not recognise Uri[$uri] as audio",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun play() {
        player
            ?.takeUnless {
                it.isPlaying
            } // ongoing playback uninterrupted
            ?.run {
                Util.getUserAgent(context, context.getString(R.string.app_name))
                    .let { userAgentString -> DefaultDataSourceFactory(context, userAgentString) }
                    .let { dataSourceFactory -> ProgressiveMediaSource.Factory(dataSourceFactory) }
                    .let { progressiveMediaSourceFactory -> progressiveMediaSourceFactory.createMediaSource(uriAudio) }
                    .also { mediaSource -> prepare(mediaSource) }
                playWhenReady = true
            }
    }

    fun stop() {
        player?.stop()
    }
}
