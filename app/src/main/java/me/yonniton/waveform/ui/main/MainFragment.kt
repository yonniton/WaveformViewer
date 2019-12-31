package me.yonniton.waveform.ui.main

import android.net.Uri
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import me.yonniton.waveform.R
import me.yonniton.waveform.databinding.MainFragmentBinding

class MainFragment : Fragment() {

    companion object {
        /** [Uri] for [MainViewModel.mp3Uri] */
        private const val EXTRA_MP3_URI = "EXTRA_MP3_URI"

        fun newInstance(mp3Uri: Uri) = MainFragment().apply {
            arguments = Bundle().also { bundle ->
                bundle.putParcelable(EXTRA_MP3_URI, mp3Uri)
            }
        }
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, save: Bundle?): View {
        viewModel = ViewModelProviders.of(requireActivity())
            .get(MainViewModel::class.java)
        arguments?.getParcelable<Uri>(EXTRA_MP3_URI)
            ?.also { viewModel.mp3Uri = it }
            ?: run { Toast.makeText(context, "missing MP3 Uri", Toast.LENGTH_SHORT).show() }
        return DataBindingUtil.inflate<MainFragmentBinding>(inflater, R.layout.main_fragment, container, false)
            .also { binding -> binding.viewModel = viewModel }
            .root
    }
}
