package me.yonniton.waveform

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.yonniton.waveform.noisealert.ui.NoiseAlertFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(save: Bundle?) {
        super.onCreate(save)
        setContentView(R.layout.main_activity)
        if (save == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, NoiseAlertFragment())
                .commit()
        }
    }
}

interface WaveformViewerNavigator {
    fun showFilePicker() {/* no-op*/}
    fun showWaveformViewer(mp3Uri: Uri) {/* no-op*/}
    fun hideWaveformViewer() {/* no-op*/}
}
