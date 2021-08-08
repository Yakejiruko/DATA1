package io.keiji.sample.myapplication

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import dev.keiji.sample.myapplication.R
import dev.keiji.sample.myapplication.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {
    private var binding: FragmentMainBinding? = null
    override fun onViewCreated (view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)
        binding?.textview?.text = "Hello Fragment"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.unbind()
    }
}