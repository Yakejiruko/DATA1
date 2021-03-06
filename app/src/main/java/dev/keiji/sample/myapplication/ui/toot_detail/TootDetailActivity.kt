package dev.keiji.sample.myapplication.ui.toot_detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.keiji.sample.myapplication.entity.Toot
import dev.keiji.sample.myapplication.R
import dev.keiji.sample.myapplication.TootDetailFragment

class TootDetailActivity : AppCompatActivity() {
    companion object {
        private const val KEY_TOOT = "key_toot"
        @JvmStatic
        fun newIntent(context: Context, toot: Toot) : Intent {
            return Intent(context, TootDetailActivity::class.java).apply {
                putExtra(KEY_TOOT, toot)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toot_detail)

        val toot = intent?.getParcelableExtra<Toot>(KEY_TOOT) ?:return

        if(savedInstanceState == null) {
            val fragment = TootDetailFragment.newInstance(toot)
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.fragment_container,
                    fragment,
                    TootDetailFragment.TAG
                )
                .commit()
        }
    }
}