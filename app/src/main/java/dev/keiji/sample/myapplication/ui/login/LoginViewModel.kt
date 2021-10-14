package dev.keiji.sample.myapplication.ui.login

import android.app.Application
import android.text.Editable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dev.keiji.sample.myapplication.repository.TootRepository
import dev.keiji.sample.myapplication.repository.UserCredentialRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class LoginViewModel (
    private val instanceUrl: String,
    private val coroutineScope: CoroutineScope,
    application: Application
) : AndroidViewModel(application) {

}