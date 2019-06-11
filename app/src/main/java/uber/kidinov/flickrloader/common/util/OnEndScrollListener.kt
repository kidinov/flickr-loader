package uber.kidinov.flickrloader.common.util

import android.widget.AbsListView

class OnEndScrollListener(
    private val onEnd: () -> Unit
) : AbsListView.OnScrollListener {
    override fun onScroll(
        view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int,
        totalItemCount: Int
    ) {
        val position = firstVisibleItem + visibleItemCount
        val limit = totalItemCount - 6

        if (position >= limit && totalItemCount > 0) onEnd()
    }

    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
    }


}