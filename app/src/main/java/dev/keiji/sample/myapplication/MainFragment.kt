package dev.keiji.sample.myapplication

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.squareup.moshi.Json
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.keiji.sample.myapplication.R
import dev.keiji.sample.myapplication.databinding.FragmentMainBinding
import retrofit2.Retrofit
import android.util.Log
import com.squareup.moshi.Moshi
import dev.keiji.sample.myapplication.entity.Toot
import dev.keiji.sample.myapplication.MastodonApi
import kotlinx.coroutines.launch
import retrofit2.converter.moshi.MoshiConverterFactory

class MainFragment : Fragment(R.layout.fragment_main) {

    companion object {
        private val TAG = MainFragment::class.java.simpleName
        private const val API_BASE_URL = "http://androidbook2020.keiji.io"
    }
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

    private val retrofit = Retrofit.Builder()

        .baseUrl(API_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
    private val api = retrofit.create(MastodonApi::class.java)

    private var binding: FragmentMainBinding? = null
    override fun onViewCreated (view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)
        binding?.button?.setOnClickListener{
            binding?.button?.text="clicked"

            CoroutineScope(Dispatchers.IO).launch {
                val tootList = api.fetchPublicTimeline()
                showTootList(tootList)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.unbind()
    }

    private suspend fun showTootList(
        tootList: List<Toot>
    ) = withContext(Dispatchers.Main) {
        val binding = binding ?: return@withContext
        val accountNameList = tootList.map {it.account.displayName}
        binding.button.text = accountNameList.joinToString("/n")
    }



}