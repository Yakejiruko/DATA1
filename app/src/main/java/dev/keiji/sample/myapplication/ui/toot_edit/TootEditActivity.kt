package dev.keiji.sample.myapplication.ui.toot_edit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.keiji.sample.myapplication.R

class TootEditActivity : AppCompatActivity() {
    companion object {
        val TAG = TootEditActivity::class.java.simpleName
        fun newInstance(context: Context): Intent {
            return Intent(context, TootEditActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toot_edit)

        if (savedInstanceState == null) {
            val fragment = TootEditFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, TootEditFragment.TAG)
                .commit()
        }
    }
}