package uber.kidinov.flickrloader.loader.view

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import uber.kidinov.flickrloader.R
import uber.kidinov.flickrloader.common.servicelocator.loader
import uber.kidinov.flickrloader.common.util.OnEndScrollListener
import uber.kidinov.flickrloader.common.util.TextWatcherAdapter
import uber.kidinov.flickrloader.loader.LoaderContract

class LoaderActivity : Activity(), LoaderContract.View {
    private val module by lazy { loader(this) }
    private val presenter = module.presenter
    private val adapter = module.adapter

    private val etQuery by lazy { findViewById<EditText>(R.id.et_query) }
    private val tvSummary by lazy { findViewById<TextView>(R.id.tv_summary) }
    private val btnSearch by lazy { findViewById<ImageButton>(R.id.btn_search) }
    private val gvPictures by lazy { findViewById<GridView>(R.id.gv_pictures) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.page = presenter.lastPage
    }

    override fun showError(error: String?) {
        Toast.makeText(this, getString(R.string.error_message).plus("; Log: $error"), Toast.LENGTH_SHORT).show()
    }

    override fun updateList() {
        adapter.notifyDataSetInvalidated()
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun showSummary(pageNum: Int, pagesAmount: Int) {
        tvSummary.text = "$pageNum/$pagesAmount"
    }

    private fun initViews() {
        btnSearch.setOnClickListener { presenter.onQueryChanged(etQuery.text.toString()) }
        etQuery.addTextChangedListener(object : TextWatcherAdapter {
            override fun afterTextChanged(s: Editable) {
                btnSearch.isEnabled = s.isNotEmpty()
            }
        })

        gvPictures.adapter = adapter
        gvPictures.setOnScrollListener(OnEndScrollListener { presenter.onScrolledDown(etQuery.text.toString()) })
    }

    companion object {
        private const val OUT_STATE_PAGE = "lastPage"
        private var Bundle.page
            get() = getInt(OUT_STATE_PAGE)
            set(value) = putInt(OUT_STATE_PAGE, value)
    }
}
