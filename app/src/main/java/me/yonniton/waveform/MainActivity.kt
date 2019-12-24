package me.yonniton.waveform

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import me.yonniton.waveform.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
}

interface WaveformViewerNavigator {
    fun showFilePicker() {/* no-op*/}
    fun showWaveformViewer(mp3Uri: Uri) {/* no-op*/}
    fun hideWaveformViewer() {/* no-op*/}
}
