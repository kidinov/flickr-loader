package uber.kidinov.flickrloader.common.util

import android.widget.AbsListView

class OnEndScrollListener(
    private val onEnd: () -> Unit
) : AbsListView.OnScrollListener {
    override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        val lastItem = firstVisibleItem + visibleItemCount
        if (lastItem == totalItemCount) {
            onEnd()
        }
    }

    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
    }
}