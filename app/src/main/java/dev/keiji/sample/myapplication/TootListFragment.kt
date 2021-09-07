package dev.keiji.sample.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.keiji.sample.myapplication.databinding.FragmentMainBinding
import dev.keiji.sample.mastodonclient.Toot

import com.squareup.moshi.Json
import dev.keiji.sample.myapplication.R
import dev.keiji.sample.myapplication.MastodonApi
import dev.keiji.sample.myapplication.databinding.FragmentTootListBinding
import io.keiji.sample.mastodonclient.TootListViewModel
import io.keiji.sample.mastodonclient.TootListViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TootListFragment : Fragment(R.layout.fragment_toot_list) {

    private var binding: FragmentTootListBinding? = null
    private lateinit var adapter: TootListAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private val viewModel: TootListViewModel by viewModels {
        TootListViewModelFactory(
            BuildConfig.INSTANCE_URL,
            lifecycleScope,
            requireContext()
        )
    }

    private val loadNextScrollListner = object : RecyclerView.
    OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val isLoadingSnapshot = viewModel.isLoading.value ?: return
            if (isLoadingSnapshot || !viewModel.hasNext) {
                return
            }

            val visibleItemCount = recyclerView.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            if ((totalItemCount - visibleItemCount) <=
                firstVisibleItemPosition
            ) {
                viewModel.loadNext()
            }
            viewModel.isLoading.observe(viewLifecycleOwner, Observer {
                binding?.swipeRefreshLayout?.isRefreshing = it
            })
        }
    }

    companion object {
        val TAG = TootListFragment::class.java.simpleName

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tootListSnapshot = viewModel.tootList.value ?: ArrayList<Toot>().also {
            viewModel.tootList.value = it
        }
        adapter = TootListAdapter(layoutInflater, tootListSnapshot)
        layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        val bindingData: FragmentTootListBinding? = DataBindingUtil.bind(view)
        binding = bindingData ?: return

        bindingData.recyclerView.also {
            it.layoutManager = layoutManager
            it.adapter = adapter
            it.addOnScrollListener(loadNextScrollListner)
        }
        bindingData.swipeRefreshLayout.setOnRefreshListener {
            viewModel.clear()
            viewModel.loadNext()
        }

        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            binding?.swipeRefreshLayout?.isRefreshing = it
        })
        viewModel.tootList.observe(viewLifecycleOwner, Observer {
            adapter.notifyDataSetChanged()
        })

        viewLifecycleOwner.lifecycle.addObserver(viewModel)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.unbind()
    }
}