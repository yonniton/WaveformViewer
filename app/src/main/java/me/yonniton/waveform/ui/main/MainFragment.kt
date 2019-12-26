package me.yonniton.waveform.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import me.yonniton.waveform.R
import me.yonniton.waveform.databinding.MainFragmentBinding

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, save: Bundle?): View {
        viewModel = ViewModelProviders.of(requireActivity())
            .get(MainViewModel::class.java)
        return DataBindingUtil.inflate<MainFragmentBinding>(inflater, R.layout.main_fragment, container, false)
            .also { binding -> binding.viewModel = viewModel }
            .root
    }
}
