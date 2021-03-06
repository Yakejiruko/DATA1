package dev.keiji.sample.myapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dev.keiji.sample.myapplication.entity.Toot
import kotlinx.coroutines.CoroutineScope

class TootDetailViewModel (
    private val tootData: Toot?,
    private val coroutineScope: CoroutineScope,
    application: Application
) : AndroidViewModel(application) {

    val toot = MutableLiveData<Toot> () .also {
        it.value = tootData
    }
}

