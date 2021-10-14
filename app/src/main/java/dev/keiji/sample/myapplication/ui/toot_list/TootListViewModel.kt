package io.keiji.sample.mastodonclient

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import dev.keiji.sample.mastodonclient.Account
import dev.keiji.sample.mastodonclient.Toot
import dev.keiji.sample.myapplication.repository.AccountRepository
import dev.keiji.sample.myapplication.repository.TootRepository
import dev.keiji.sample.myapplication.entity.UserCredential
import dev.keiji.sample.myapplication.repository.UserCredentialRepository
import dev.keiji.sample.myapplication.ui.TimelineType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class TootListViewModel(
    private val instanceUrl: String,
    private val username: String,
    private val timelineType: TimelineType,
    private val coroutineScope: CoroutineScope,
    application: Application
) : AndroidViewModel(application), LifecycleObserver {

    private val userCredentialRepository =
        UserCredentialRepository(
            application
        )
    private lateinit var tootRepository: TootRepository
    private lateinit var accountRepository: AccountRepository
    private lateinit var userCredential: UserCredential

    val loginRequired = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()
    val accountInfo = MutableLiveData<Account>()
    var hasNext = true
    val tootList = MutableLiveData<ArrayList<Toot>>()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        coroutineScope.launch {
            val credential = userCredentialRepository.find(instanceUrl, username)
            if (credential == null) {
                loginRequired.postValue(true)
                return@launch
            }

            tootRepository = TootRepository(credential)
            accountRepository = AccountRepository(credential)
            userCredential = credential

            loadNext()
        }
    }

    fun clear() {
        val tootListSnapshot = tootList.value ?: return
        tootListSnapshot.clear()
    }

    fun loadNext() {
        coroutineScope.launch {
            uppdateAccountInfo()
            isLoading.postValue(true)
            val tootListSnapshot = tootList.value ?: ArrayList()
            val maxId = tootListSnapshot.lastOrNull()?.id
            val tootListResponse = when (timelineType) {
                TimelineType.PublicTimeline -> {
                    tootRepository.fetchPublicTimeLine(
                        maxId = maxId,
                        onlyMedia = true
                    )
                }
                TimelineType.HomeTimeline -> {
                    tootRepository.fetchHomeTimeline(
                        maxId = maxId
                    )
                }
            }
            tootListSnapshot.addAll(tootListResponse)
            tootList.postValue(tootListSnapshot)
            hasNext = tootListResponse.isNotEmpty()
            isLoading.postValue(false)
        }
    }

    private suspend fun uppdateAccountInfo() {
        val accountInfoSnapshot = accountInfo.value
            ?: accountRepository.verifyAccountCredential()
        accountInfo.postValue(accountInfoSnapshot)
    }

    fun delete(toot: Toot) {
        coroutineScope.launch {
            tootRepository.delete(toot.id)
            val tootListSnapshot = tootList.value
            tootListSnapshot?.remove(toot)
            tootList.postValue(tootListSnapshot)
        }
    }
}