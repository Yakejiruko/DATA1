package dev.keiji.sample.myapplication

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.keiji.sample.myapplication.entity.Toot
import kotlinx.coroutines.CoroutineScope

class TootDetailViewModelFactory(
    private val toot: Toot?,
    private val coroutineScope: CoroutineScope,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TootDetailViewModel (
            toot,
            coroutineScope,
            context.applicationContext as Application
        ) as T
    }
}