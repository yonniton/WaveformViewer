package me.yonniton.waveform.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import me.yonniton.waveform.NoiseAlertService
import me.yonniton.waveform.NoiseAlertServiceBinder
import me.yonniton.waveform.R
import me.yonniton.waveform.databinding.NoiseAlertBinding
import me.yonniton.waveform.ui.main.NoiseAlertViewModel

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, save: Bundle?): View {
        super.onCreateView(inflater, container, save)

        requireActivity().requestPermissions()

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
            it.bindService(
                Intent(it, NoiseAlertService::class.java),
                serviceConnection,
                Context.BIND_AUTO_CREATE
            )
        } ?: Log.w("NoiseAlertFragment", "NoiseAlertService failed, missing Context")
    }

    private fun Activity.requestPermissions() {
        listOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE)
            .filter { permission ->
                ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED
            }
            .fold(
                mutableListOf<String>(),
                { deniedPermissions, deniedPermission ->
                    deniedPermissions.apply { add(deniedPermission) }
                }
            )
            .toTypedArray()
            .takeIf { it.isNotEmpty() }
            ?.also { deniedPermissions ->
                ActivityCompat.requestPermissions(this, deniedPermissions, 0)
            }
    }
}
