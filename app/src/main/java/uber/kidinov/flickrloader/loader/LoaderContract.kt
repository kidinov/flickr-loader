package uber.kidinov.flickrloader.loader

import uber.kidinov.flickrloader.loader.model.Photo
import uber.kidinov.flickrloader.loader.model.Photos
import java.io.Serializable

interface LoaderContract {
    interface View {
        fun showError(error: String?)
        fun updateList()
        fun showSummary(pageNum: Int, pagesAmount: Int)
    }

    interface Presenter {
        var state: State

        fun onQueryChanged(query: String)
        fun onScrolledDown(query: String)
        fun getCount(): Int
        fun getItemId(position: Int): Long
        fun bindPicture(position: Int, itemViewImpl: ItemView)
        fun getItem(position: Int): String
    }

    interface Repo {
        fun fetchPhotoUrls(page: Int, query: String, result: (Result<Photos>) -> Unit)
    }

    interface ItemView {
        fun bindPicture(url: String)
    }

    data class State(
        var photos: MutableList<Photo>,
        var pages: Int,
        var lastPage: Int
    ) : Serializable
}