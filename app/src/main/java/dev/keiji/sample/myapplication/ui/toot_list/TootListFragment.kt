package dev.keiji.sample.myapplication.ui.toot_list

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dev.keiji.sample.myapplication.entity.Toot

import dev.keiji.sample.myapplication.entity.Account
import dev.keiji.sample.myapplication.BuildConfig
import dev.keiji.sample.myapplication.R
import dev.keiji.sample.myapplication.ui.toot_edit.TootEditActivity
import dev.keiji.sample.myapplication.databinding.FragmentTootListBinding
import dev.keiji.sample.myapplication.ui.TimelineType
import dev.keiji.sample.myapplication.ui.TootListAdapter
import dev.keiji.sample.myapplication.ui.login.LoginActivity
import dev.keiji.sample.myapplication.ui.toot_detail.TootDetailActivity
import dev.keiji.sample.myapplication.TootListViewModel
import io.keiji.sample.mastodonclient.TootListViewModelFactory


class TootListFragment : Fragment(R.layout.fragment_toot_list),
    TootListAdapter.Callback {

    private var binding: FragmentTootListBinding? = null
    private lateinit var adapter: TootListAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var timelineType = TimelineType.PublicTimeline

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().also {
            val typeOrdinal = it.getInt(
                BUNDLE_KEY_TIMELINE_TYPE_ORDINAL,
                TimelineType.PublicTimeline.ordinal
            )
            timelineType = TimelineType.values()[typeOrdinal]
        }
    }

    private val viewModel: TootListViewModel by viewModels {
        TootListViewModelFactory(
            BuildConfig.INSTANCE_URL,
            BuildConfig.USERNAME,
            timelineType,
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
        private const val REQUEST_CODE_TOOT_EDIT = 0x01
        private const val REQUEST_CODE_LOGIN = 0x02
        private const val BUNDLE_KEY_TIMELINE_TYPE_ORDINAL = "timeline_type_ordinal"

        @JvmStatic
        fun newInstance(timelineType: TimelineType): TootListFragment {
            val args = Bundle().apply {
                putInt(BUNDLE_KEY_TIMELINE_TYPE_ORDINAL, timelineType.ordinal)
            }
            return TootListFragment().apply {
                arguments = args
            }
        }

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

        bindingData.fab.setOnClickListener {
            launchTootEditActivity()
        }

        viewModel.loginRequired.observe(viewLifecycleOwner, Observer {
            if (it) {
                launchLoginActivity()
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            binding?.swipeRefreshLayout?.isRefreshing = it
        })
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            Snackbar.make(bindingData.swipeRefreshLayout,it,Snackbar.LENGTH_LONG)
                .show()
        })
        viewModel.accountInfo.observe(viewLifecycleOwner, Observer {
            showAccountInfo(it)
        })
        viewModel.tootList.observe(viewLifecycleOwner, Observer {
            adapter.notifyDataSetChanged()
        })

        viewLifecycleOwner.lifecycle.addObserver(viewModel)
    }

    private fun launchLoginActivity() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_LOGIN)
    }

    private fun launchTootEditActivity() {
        val intent = TootEditActivity.newIntent(requireContext())
        startActivityForResult(intent, REQUEST_CODE_TOOT_EDIT)
    }

    private fun showAccountInfo(accountInfo: Account) {
        val activity = requireActivity()
        if (activity is AppCompatActivity) {
            activity.supportActionBar?.subtitle = accountInfo.username
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_TOOT_EDIT && resultCode == Activity.RESULT_OK) {
            viewModel.clear()
            viewModel.loadNext()
        }

        if (requestCode == REQUEST_CODE_LOGIN) {
            handleLoginActivityResult(resultCode)
        }
    }

    private fun handleLoginActivityResult(resultCode: Int) {
        when (resultCode) {
            Activity.RESULT_OK -> viewModel.reloadUserCredential()
            else -> {
                Toast.makeText(
                    requireContext(),
                    "??????????????????????????????????????????",
                    Toast.LENGTH_LONG
                ).show()
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.unbind()
    }

    override fun openDetail(toot: Toot) {
        val intent = TootDetailActivity.newIntent(requireContext(), toot)
        startActivity(intent)
    }

    override fun delete(toot: Toot) {
        viewModel.delete(toot)
    }
}
