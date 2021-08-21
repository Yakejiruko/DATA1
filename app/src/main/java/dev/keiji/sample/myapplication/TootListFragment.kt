package dev.keiji.sample.myapplication

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.keiji.sample.myapplication.databinding.FragmentMainBinding
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.atomic.AtomicBoolean
import dev.keiji.sample.mastodonclient.Toot

import com.squareup.moshi.Json
import dev.keiji.sample.myapplication.R
import android.util.Log
import dev.keiji.sample.myapplication.MastodonApi
import dev.keiji.sample.myapplication.databinding.FragmentTootListBinding
import kotlinx.coroutines.*


class TootListFragment : Fragment(R.layout.fragment_toot_list) {

    private lateinit var layoutManager: LinearLayoutManager

    private var isLoading = AtomicBoolean()
    private var hasNext = AtomicBoolean() . apply{ set(true) }

    private val loadNextScrollListner = object : RecyclerView.
    OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (isLoading.get() || !hasNext.get()) {
                return
            }

            val visibleItemCount = recyclerView.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.
                    findFirstVisibleItemPosition()

            if ((totalItemCount - visibleItemCount) <=
                    firstVisibleItemPosition) {
                loadNext()
            }
        }
    }

    companion object {
        val TAG = TootListFragment::class.java.simpleName
        private const val API_BASE_URL = "https://androidbook2020.keiji.io"
    }

    private var binding: FragmentTootListBinding? = null

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
    private val api = retrofit.create(MastodonApi::class.java)

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private lateinit var adapter: TootListAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private val tootList = ArrayList<Toot>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TootListAdapter(layoutInflater, tootList)
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

        laodNext()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.unbind()
    }

    private fun loadNext() {
        coroutineScope.launch{
            isLoading.set(true)

            val tootListResponse = api.fetchPublicTimeline(
                maxId = tootList.lastOrNull() ?.id,
                onlyMedia = true
            )

            tootList.addAll(tootListResponse.filter { !it.sensitive})
            reloadTootList()

            isLoading.set(false)
            hasNext.set(tootListResponse.isNotEmpty())
        }
    }

    private suspend fun reloadTootList() = withContext(Dispatchers.Main) {
        adapter.notifyDataSetChanged()
    }
}