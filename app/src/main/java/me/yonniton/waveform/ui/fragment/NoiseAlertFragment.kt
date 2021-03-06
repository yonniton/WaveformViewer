package me.yonniton.waveform.ui.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import me.yonniton.waveform.NoiseAlertService
import me.yonniton.waveform.NoiseAlertServiceBinder
import me.yonniton.waveform.R
import me.yonniton.waveform.databinding.NoiseAlertBinding
import me.yonniton.waveform.ui.main.NoiseAlertViewModel


/**
 * source: [Android Example](https://androidexample.com/Detect_Noise_Or_Blow_Sound_-_Set_Sound_Frequency_Thersold/index.php?view=article_discription&aid=108&aaid=130)
 */
class NoiseAlertFragment : Fragment() {

    private var viewModel: NoiseAlertViewModel? = null

    private val serviceConnection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            viewModel?.noiseAlert = (binder as NoiseAlertServiceBinder).noiseAlert
        }

        override fun onServiceDisconnected(name: ComponentName) {
            viewModel?.noiseAlert = null
        }
    }

    /**
     * Called when the activity is first created.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, save: Bundle?): View {
        super.onCreateView(inflater, container, save)

        viewModel = ViewModelProviders.of(requireActivity())
            .get(NoiseAlertViewModel::class.java)

        return DataBindingUtil.inflate<NoiseAlertBinding>(inflater, R.layout.noise_alert, container, false).let { binding ->
            binding.viewModel = viewModel
            binding.root
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i("NoiseAlertFragment", "==== onResume ===")
        context?.also {
            it.startService(Intent(it, NoiseAlertService::class.java))
            it.bindService(
                Intent(it, NoiseAlertService::class.java),
                serviceConnection,
                Context.BIND_AUTO_CREATE
            )
        } ?: Log.w("NoiseAlertFragment", "NoiseAlertService failed, missing Context")
    }
}