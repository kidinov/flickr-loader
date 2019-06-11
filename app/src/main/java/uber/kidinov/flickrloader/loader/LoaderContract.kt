package uber.kidinov.flickrloader.loader

import uber.kidinov.flickrloader.loader.model.Photos

interface LoaderContract {
    interface View {
        fun showError(error: String?)
        fun showPhotos()
    }

    interface Presenter {
        val photosToShow: MutableList<String>
        var page: Int

        fun onQueryChanged(query: String)
        fun onScrolledDown(query: String)
        fun getCount(): Int
        fun bindPicture(position: Int, itemViewImpl: ItemView)
    }

    interface Repo {
        fun fetchPhotoUrls(page: Int, query: String, result: (Result<Photos>) -> Unit)
    }

    interface ItemView {
        fun bindPicture(url: String)
    }
}