package me.yonniton.waveform.ui.main

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import me.yonniton.waveform.WaveformViewerNavigator

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

    fun isFileMp3(contentResolver: ContentResolver, fileUri: Uri?): Boolean {
        return fileUri?.let {
            "audio/mpeg" == contentResolver.getType(it)
        } ?: false
    }

    fun prepareWaveform(fileUri: Uri) {
        navigator.showWaveformViewer(fileUri)
    }
}
