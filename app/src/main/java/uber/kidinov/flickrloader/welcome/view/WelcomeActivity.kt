package uber.kidinov.flickrloader.welcome.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import uber.kidinov.flickrloader.R
import uber.kidinov.flickrloader.common.servicelocator.injectFlickr
import uber.kidinov.flickrloader.common.servicelocator.injectGliphy
import uber.kidinov.flickrloader.loader.view.LoaderActivity

class WelcomeActivity : Activity() {
    private val btnGiphy by lazy { findViewById<Button>(R.id.btn_giphy) }
    private val btnFlickr by lazy { findViewById<Button>(R.id.btn_flickr) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        initViews()
    }

    private fun initViews() {
        btnGiphy.setOnClickListener {
            injectGliphy()
            startActivity(Intent(this, LoaderActivity::class.java))
        }
        btnFlickr.setOnClickListener {
            injectFlickr()
            startActivity(Intent(this, LoaderActivity::class.java))
        }
    }
}
