package dev.keiji.sample.myapplication.ui.toot_edit

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dev.keiji.sample.myapplication.BuildConfig
import dev.keiji.sample.myapplication.R
import dev.keiji.sample.myapplication.databinding.FragmentTootEditBinding

class TootEditFragment : Fragment(R.layout.fragment_toot_edit) {
    companion object {
        val TAG = TootEditFragment::class.java.simpleName
        fun newInstance(): TootEditFragment {
            return TootEditFragment()
        }
    }

    private var binding: FragmentTootEditBinding? = null
    private val viewModel: TootEditViewModel by viewModels {
        TootEditViewModelFactory(
            BuildConfig.INSTANCE_URL,
            BuildConfig.USERNAME,
            lifecycleScope,
            requireContext()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bindingData: FragmentTootEditBinding? = DataBindingUtil.bind(view)
        binding = bindingData ?: return

        bindingData.lifecycleOwner = viewLifecycleOwner
        bindingData.viewModel = viewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.unbind()
    }
}