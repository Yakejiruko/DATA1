package io.keiji.sample.myapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import dev.keiji.sample.myapplication.entity.Account
import dev.keiji.sample.myapplication.entity.Toot
import dev.keiji.sample.myapplication.repository.AccountRepository
import dev.keiji.sample.myapplication.repository.TootRepository
import dev.keiji.sample.myapplication.entity.UserCredential
import dev.keiji.sample.myapplication.repository.UserCredentialRepository
import dev.keiji.sample.myapplication.ui.TimelineType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.HttpURLConnection

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
    val errorMessage = MutableLiveData<String>()
    val accountInfo = MutableLiveData<Account>()
    var hasNext = true
    val tootList = MutableLiveData<ArrayList<Toot>>()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        reloadUserCredential()
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
            try {
                val tootListRespnse = when (timelineType) {
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

                tootListSnapshot.addAll(tootListRespnse)
                tootList.postValue(tootListSnapshot)
                hasNext = tootListRespnse.isNotEmpty()
            } catch (e: HttpException) {
                when (e.code()) {
                    HttpURLConnection.HTTP_FORBIDDEN -> {
                        errorMessage.postValue("必要な権限がありません")
                    }
                }
            } finally {
                isLoading.postValue(false)
            }
        }
    }


    tootListSnapshot.addAll(tootListResponse)
    tootList.postValue(tootListSnapshot)
    hasNext = tootListResponse.isNotEmpty()
    isLoading.postValue(false)
}
}

private suspend fun uppdateAccountInfo() {
    try {
        val accountInfoSnapshot = accountInfo.value
            ?: accountRepository.verifyAccountCredential()
        accountInfo.postValue(accountInfoSnapshot)
    } catch (e: HttpException) {
        when (e.code()) {
            HttpURLConnection.HTTP_FORBIDDEN -> {
                errorMessage.postValue("必要な権限がありません")
            }
        }
    }
}

fun delete(toot: Toot) {
    coroutineScope.launch {
        try {
            tootRepository.delete(toot.id)
            val tootListSnapshot = tootList.value
            tootListSnapshot?.remove(toot)
            tootList.postValue(tootListSnapshot)
        } catch (e: HttpException) {
            when (e.code()) {
                HttpURLConnection.HTTP_FORBIDDEN -> {
                    errorMessage.postValue("必要な権限がありません")
                }
            }
        }
    }
}


fun reloadUserCredential() {
    coroutineScope.launch {
        val credential = userCredentialRepository
            .find(instanceUrl, username)
        if (credential == null) {
            loginRequired.postValue(true)
            return@launch
        }

        tootRepository = TootRepository(credential)
        accountRepository = AccountRepository(credential)
        userCredential = credential

        clear()
        loadNext()
    }
}
}