package me.yonniton.waveform.common

import android.app.Activity
import android.content.Intent
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

object AudioChooser {

    /** a convenience [Intent] for invoking a native audio-chooser */
    val INTENT_CHOOSE_AUDIO: Intent = Intent()
        .apply {
            type = "audio/*"
            action = Intent.ACTION_GET_CONTENT
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        }
        .let { iChooserTarget -> Intent.createChooser(iChooserTarget, "Choose audio") }

    /** result code for [INTENT_CHOOSE_AUDIO] */
    internal const val RESULT_CODE_AUDIO_CHOOSER = 7878

    fun show(activity: Activity) {
        ActivityCompat.startActivityForResult(activity, INTENT_CHOOSE_AUDIO, RESULT_CODE_AUDIO_CHOOSER, null)
    }

    fun show(fragment: Fragment) {
        fragment.startActivityForResult(INTENT_CHOOSE_AUDIO, RESULT_CODE_AUDIO_CHOOSER)
    }
}
