package dev.keiji.sample.myapplication


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import io.keiji.sample.mastodonclient.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(
            this.parent
                    R.layout.activity_main
        )
        binding.textview.text = "Hello DataBinding!"
    }
}

