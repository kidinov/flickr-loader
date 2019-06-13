package uber.kidinov.flickrloader.loader

import uber.kidinov.flickrloader.loader.model.Photo
import uber.kidinov.flickrloader.loader.model.Photos
import java.io.Serializable

interface LoaderContract {
    interface View {
        fun showError(error: String?)
        fun requestListDataChanged()
        fun showSummary(pageNum: Int, pagesAmount: Int)
    }

    interface Presenter {
        var state: State

        fun onRecreated()

        fun onQueryChanged(query: String)
        fun onScrolledDown(query: String)
        fun getCount(): Int
        fun getItemId(position: Int): Long
        fun bindPicture(position: Int, itemView: ItemView)
        fun getItem(position: Int): Photo
    }

    interface Repo {
        fun fetchPhotoUrls(page: Int, query: String, result: (Result<Photos>) -> Unit)
    }

    interface ItemView {
        fun bindPicture(url: String)
    }

    data class State(
        var pageNum: Int,
        var pages: Int,
        var photos: MutableList<Photo>
    ) : Serializable
}