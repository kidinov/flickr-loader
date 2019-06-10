package uber.kidinov.flickrloader.loader.view

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageButton
import android.widget.Toast
import uber.kidinov.flickrloader.R
import uber.kidinov.flickrloader.common.util.TextWatcherAdapter
import uber.kidinov.flickrloader.loader.LoaderContract
import uber.kidinov.flickrloader.loader.loader
import uber.kidinov.flickrloader.loader.model.Photos

class LoaderActivity : Activity(), LoaderContract.View {
    private val di by lazy { loader(this) }
    private val presenter = di.presenter

    private val etQuery by lazy { findViewById<EditText>(R.id.et_query) }
    private val btnSearch by lazy { findViewById<ImageButton>(R.id.btn_search) }
    private val gvPictures by lazy { findViewById<GridView>(R.id.gv_pictures) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    override fun showError(error: String?) {
        Toast.makeText(this, getString(R.string.error_message).plus("; Log: $error"), Toast.LENGTH_SHORT).show()
    }

    override fun showPhotos(photos: Photos) {
    }

    private fun initViews() {
        btnSearch.setOnClickListener { presenter.onQueryChanged(etQuery.text.toString()) }
        etQuery.addTextChangedListener(object : TextWatcherAdapter {
            override fun afterTextChanged(s: Editable) {
                btnSearch.isEnabled = s.isNotEmpty()
            }
        })
    }
}
