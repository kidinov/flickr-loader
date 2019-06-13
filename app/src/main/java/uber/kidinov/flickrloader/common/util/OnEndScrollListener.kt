package uber.kidinov.flickrloader.common.util

import android.widget.AbsListView
import uber.kidinov.flickrloader.common.config.Configuration

class OnEndScrollListener(
    private val config: Configuration,
    private val onEnd: () -> Unit
) : AbsListView.OnScrollListener {
    override fun onScroll(
        view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int,
        totalItemCount: Int
    ) {
        val position = firstVisibleItem + visibleItemCount
        val limit = totalItemCount - config.SCROLLING_THRESHOLD

        if (position >= limit && totalItemCount > 0) onEnd()
    }

    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
    }
}