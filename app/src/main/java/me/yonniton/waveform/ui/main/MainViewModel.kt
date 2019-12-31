package me.yonniton.waveform.ui.main

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import com.google.android.exoplayer2.ExoPlayer
import me.yonniton.waveform.WaveformViewerNavigator
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.SimpleExoPlayer
import me.yonniton.waveform.R

class MainViewModel : ViewModel() {

    companion object {
        /** a convenience [Intent] for invoking a native MP3 chooser */
        val INTENT_PICK_MP3: Intent = Intent()
            .apply {
                type = "audio/mpeg"
                action = Intent.ACTION_GET_CONTENT
                addCategory(Intent.CATEGORY_OPENABLE)
                putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            }
            .let { iChooserTarget -> Intent.createChooser(iChooserTarget, "Pick a song") }

        /** result code for [INTENT_PICK_MP3] */
        internal const val RESULT_CODE_FILE_CHOOSER = 7878
    }

    lateinit var navigator: WaveformViewerNavigator

    private var player: ExoPlayer? = null
        set(value) {
            field?.release()
            field = value
        }

    internal var mp3Uri: Uri? = null
    val iconPlayPause = ObservableInt(android.R.drawable.ic_media_play)

    fun isFileMp3(contentResolver: ContentResolver, fileUri: Uri?): Boolean {
        return fileUri?.let {
            "audio/mpeg" == contentResolver.getType(it)
        } ?: false
    }

    private fun preparePlayback(context: Context) {
        if (mp3Uri == null) {
            "missing media Uri".also { errMsg ->
                System.err.println(errMsg)
                Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show()
            }
            return
        }

        cleanup()
        val mediaSource = Util.getUserAgent(context, context.getString(R.string.app_name))
            .let { userAgentString -> DefaultDataSourceFactory(context, userAgentString) }
            .let { dataSourceFactory -> ProgressiveMediaSource.Factory(dataSourceFactory) }
            .let { progressiveMediaSourceFactory -> progressiveMediaSourceFactory.createMediaSource(mp3Uri) }
        player = SimpleExoPlayer.Builder(context)
            .build()
            .apply { prepare(mediaSource) }
    }

    private fun ExoPlayer.startPlayback() {
        playWhenReady = true
        iconPlayPause.set(android.R.drawable.ic_media_pause)
    }

    private fun ExoPlayer.stopPlayback() {
        playWhenReady = false
        iconPlayPause.set(android.R.drawable.ic_media_play)
    }

    private fun cleanup() {
        player = null
    }

    fun togglePlayback(context: Context) {
        if (player == null) {
            cleanup()
            preparePlayback(context)
        }

        player?.apply {
            if (isPlaying) {
                stopPlayback()
            } else {
                startPlayback()
            }
        } ?: System.err.println("missing MediaPlayer instance")
    }

    override fun onCleared() {
        cleanup()
        super.onCleared()
    }
}
