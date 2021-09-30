package dev.keiji.sample.myapplication.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.keiji.sample.mastodonclient.Toot

import dev.keiji.sample.mastodonclient.Account
import dev.keiji.sample.myapplication.BuildConfig
import dev.keiji.sample.myapplication.R
import dev.keiji.sample.myapplication.TootDetailFragment
import dev.keiji.sample.myapplication.databinding.FragmentTootListBinding
import io.keiji.sample.mastodonclient.TootListViewModel
import io.keiji.sample.mastodonclient.TootListViewModelFactory


class TootListFragment : Fragment(R.layout.fragment_toot_list),
    TootListAdapter.Callback {

    private var binding: FragmentTootListBinding? = null
    private lateinit var adapter: TootListAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private val viewModel: TootListViewModel by viewModels {
        TootListViewModelFactory(
            BuildConfig.INSTANCE_URL,
            BuildConfig.USERNAME,
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
        adapter = TootListAdapter(
            layoutInflater,
            tootListSnapshot,
            this
        )
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
        viewModel.accountInfo.observe(viewLifecycleOwner, Observer {
            showAccountInfo(it)
        })
        viewModel.tootList.observe(viewLifecycleOwner, Observer {
            adapter.notifyDataSetChanged()
        })

        viewLifecycleOwner.lifecycle.addObserver(viewModel)
    }

    private fun showAccountInfo(accountInfo: Account) {
        val activity = requireActivity()
        if (activity is AppCompatActivity) {
            activity.supportActionBar?.subtitle = accountInfo.username
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.unbind()
    }

    override fun openDetail(toot: Toot) {
        val fragment = TootDetailFragment.newInstance(toot)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(TootDetailFragment.TAG)
            .commit()
    }
}
