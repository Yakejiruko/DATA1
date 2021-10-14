package dev.keiji.sample.myapplication.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.keiji.sample.myapplication.R

class LoginActivity: AppCompatActivity(R.layout.activity_login) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val fragment = LoginFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container,fragment,LoginFragment.TAG)
                .commit()
        }
    }
}