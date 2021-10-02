package dev.keiji.sample.myapplication


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.keiji.sample.myapplication.ui.TootListFragment
import androidx.databinding.DataBindingUtil
import dev.keiji.sample.mastodonclient.R
import dev.keiji.sample.mastodonclient.databinding.ActivityMainBinding
import dev.keiji.sample.myapplication.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            val fragment = when (it.itemId) {
                R.id.menu_home -> TootListFragment()
                R.id.menu_public -> TootListFragment()
                else -> null
            }
            fragment ?: return@setOnNavigationItemSelectedListener false

            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    fragment,
                    TootListFragment.TAG
                )
                .commit()

            return@setOnNavigationItemSelectedListener true
        }

        if (savedInstanceState == null) {
            val fragment = TootListFragment()
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.fragment_container,
                    fragment,
                    TootListFragment.TAG
                )
                .commit()
        }
    }
}


