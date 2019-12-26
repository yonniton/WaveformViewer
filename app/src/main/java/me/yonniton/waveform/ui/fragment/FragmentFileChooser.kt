package me.yonniton.waveform.ui.fragment

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import me.yonniton.waveform.R
import me.yonniton.waveform.WaveformViewerNavigator
import me.yonniton.waveform.databinding.FragmentFileChooserBinding
import me.yonniton.waveform.ui.main.MainFragment
import me.yonniton.waveform.ui.main.MainViewModel
import me.yonniton.waveform.ui.main.MainViewModel.Companion.RESULT_CODE_FILE_CHOOSER

class FragmentFileChooser : Fragment() {

    companion object {
        fun newInstance() = FragmentFileChooser()
    }

    private lateinit var viewModel: MainViewModel

    private val navigator = object : WaveformViewerNavigator {
        override fun showFilePicker() {
            startActivityForResult(MainViewModel.INTENT_PICK_MP3, RESULT_CODE_FILE_CHOOSER)
        }

        override fun showWaveformViewer(mp3Uri: Uri) {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .setTransition(TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container, MainFragment.newInstance())
                .commit()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, save: Bundle?): View {
        viewModel = ViewModelProviders.of(requireActivity())
            .get(MainViewModel::class.java)
            .also { vm -> vm.navigator = navigator }
        return DataBindingUtil.inflate<FragmentFileChooserBinding>(inflater, R.layout.fragment_file_chooser, container, false)
            .also { binding -> binding.viewModel = viewModel }
            .root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_CODE_FILE_CHOOSER && resultCode == AppCompatActivity.RESULT_OK) {
            val fileUri = data?.data
            val mp3Uri = fileUri?.takeIf { viewModel.isFileMp3(requireActivity().contentResolver, it) }
            mp3Uri?.also { viewModel.prepareWaveform(it) }
                ?: run { Toast.makeText(context, "file chooser result has a missing or invalid file Uri[$fileUri]", Toast.LENGTH_SHORT).show() }
        }
    }
}
