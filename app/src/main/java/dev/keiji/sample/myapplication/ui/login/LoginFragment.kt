package dev.keiji.sample.myapplication.ui.login

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import dev.keiji.sample.myapplication.BuildConfig
import dev.keiji.sample.myapplication.R
import dev.keiji.sample.myapplication.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {
    companion object {
        val TAG = LoginFragment::class.java.simpleName
    }

    private var binding: FragmentLoginBinding? = null
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(
            BuildConfig.INSTANCE_URL,
            lifecycleScope,
            requireContext()
        )
    }

    interface Callback {
        fun onAuthComplete()
    }

    private var callback: Callback? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is Callback) {
            callback = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bindingData: FragmentLoginBinding? = DataBindingUtil.bind(view)
        binding = bindingData ?: return

        viewModel.accessTokenSaved.observe(viewLifecycleOwner, Observer {
            callback?.onAuthComplete()
        })

        val authUri = Uri.parse(BuildConfig.INSTANCE_URL)
            .buildUpon()
            .appendPath("oauth")
            .appendPath("authorize")
            .appendQueryParameter("client_id", BuildConfig.CLIENT_KEY)
            .appendQueryParameter("redirect_uri", BuildConfig.CLIENT_REDIRECT_URI)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("scope", BuildConfig.CLIENT_SCOPES)
            .build()

        bindingData.webview.webViewClient = InnerWebViewClient()
        bindingData.webview.settings.javaScriptEnabled = true
        bindingData.webview.loadUrl(authUri.toString())
    }

    private class InnerWebViewClient : WebViewClient() {
    }
    private val onObtainCode = fun (code: String) {
        viewModel.requestAccessToken(
            BuildConfig.CLIENT_KEY,
            BuildConfig.CLIENT_SECRET,
            BuildConfig.CLIENT_REDIRECT_URI,
            BuildConfig.CLIENT_SCOPES,
            code
        )
    }
}