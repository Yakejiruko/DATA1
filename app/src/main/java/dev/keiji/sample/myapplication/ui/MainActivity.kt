package dev.keiji.sample.myapplication.ui


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import dev.keiji.sample.myapplication.R
import dev.keiji.sample.myapplication.databinding.ActivityMainBinding
import dev.keiji.sample.myapplication.ui.toot_list.TootListFragment


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this,
                R.layout.activity_main
            )

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            val fragment = when (it.itemId) {
                R.id.menu_home -> {
                    TootListFragment.newInstance(TimelineType.HomeTimeline)
                }
                R.id.menu_public -> {
                    TootListFragment.newInstance(TimelineType.PublicTimeline)
                }
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
            val fragment = TootListFragment.newInstance(
                TimelineType.HomeTimeline
            )
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


