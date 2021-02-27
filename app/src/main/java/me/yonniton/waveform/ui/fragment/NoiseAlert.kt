package me.yonniton.waveform.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import me.yonniton.waveform.R
import me.yonniton.waveform.databinding.NoiseAlertBinding
import me.yonniton.waveform.ui.SoundLevelView
import me.yonniton.waveform.ui.main.SoundMeter

/**
 * source: [Android Example](https://androidexample.com/Detect_Noise_Or_Blow_Sound_-_Set_Sound_Frequency_Thersold/index.php?view=article_discription&aid=108&aaid=130)
 */
class NoiseAlert : Fragment() {

    companion object {
        private const val WAKELOCK_TIMEOUT = 6 * 60 * 60 * 1000L
        private const val POLL_INTERVAL = 300
    }

    /**
     * running state
     */
    private var isRunning = false

    /**
     * config state
     */
    private var noiseThreshold = 0
    private var wakeLock: WakeLock? = null
    private val handler = Handler()

    private lateinit var statusView: TextView
    private lateinit var display: SoundLevelView

    private lateinit var soundMeter: SoundMeter

    /****************** Define runnable thread again and again detect noise  */
    private val sleepTask = Runnable {
        Log.i("NoiseAlert", "runnable mSleepTask")
        start()
    }

    /** [Runnable] to monitor audio */
    private val pollTask: Runnable = object : Runnable {
        override fun run() {
            val amplitude = soundMeter.amplitude
            Log.i("NoiseAlert", "runnable mPollTask")
            updateDisplay("Monitoring audio...", amplitude)
            if (amplitude > noiseThreshold) {
                callForHelp()
                Log.i("NoiseAlert", "==== onCreate ===")
            }

            // Runnable(mPollTask) will again execute after POLL_INTERVAL
            handler.postDelayed(this, POLL_INTERVAL.toLong())
        }
    }

    /**
     * Called when the activity is first created.
     */
    @SuppressLint("InvalidWakeLockTag")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, save: Bundle?): View {
        super.onCreateView(inflater, container, save)

        val binding = DataBindingUtil.inflate<NoiseAlertBinding>(inflater, R.layout.noise_alert, container, false)
        statusView = binding.status
        display = binding.volume

        soundMeter = SoundMeter()
        wakeLock = getSystemService(requireContext(), PowerManager::class.java)
            ?.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "NoiseAlert")

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.i("NoiseAlert", "==== onResume ===")
        initializeApplicationConstants()
        display.setLevel(0, noiseThreshold)
        if (!isRunning) {
            isRunning = true
            start()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.i("NoiseAlert", "==== onStop ===")

        // stop noise monitoring
        stop()
    }

    private fun start() {
        Log.i("NoiseAlert", "==== start ===")
        soundMeter.start()
        wakeLock?.takeUnless { it.isHeld }
            ?.also { it.acquire(WAKELOCK_TIMEOUT) }

        // start noise-monitoring
        // Runnable(mPollTask) will execute after POLL_INTERVAL
        handler.postDelayed(pollTask, POLL_INTERVAL.toLong())
    }

    private fun stop() {
        Log.i("NoiseAlert", "==== Stop Noise Monitoring===")
        wakeLock?.takeIf { it.isHeld }
            ?.also { it.release() }
        handler.removeCallbacks(sleepTask)
        handler.removeCallbacks(pollTask)
        soundMeter.stop()
        display.setLevel(0, 0)
        updateDisplay("stopped...", 0.0)
        isRunning = false
    }

    private fun initializeApplicationConstants() {
        noiseThreshold = 5
    }

    private fun updateDisplay(status: String, signalEMA: Double) {
        statusView.text = status
        display.setLevel(signalEMA.toInt(), noiseThreshold)
    }

    private fun callForHelp() {

//        stop()

        // show alert when noise threshold crossed
        Toast.makeText(
            requireContext(), "Noise Threshold Crossed",
            Toast.LENGTH_LONG
        ).show()
    }
}
