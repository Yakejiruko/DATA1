package dev.keiji.sample.myapplication


import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragment = MainFragment()
            supportFragmentManager.beginTransAction()
                .add(
                    R.id.fragment_container,
                    fragment,
                    MainFragment::class.java.simpleName
                )
                .commit()
        }
    }
}
