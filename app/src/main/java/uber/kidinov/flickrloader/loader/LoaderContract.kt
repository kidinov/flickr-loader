package uber.kidinov.flickrloader.loader

import uber.kidinov.flickrloader.loader.model.Photos

interface LoaderContract {
    interface View {
        fun showError(error: String?)
        fun showPhotos(photos: Photos)
    }

    interface Presenter {
        fun onQueryChanged(query: String)
        fun onScrolledDown(page: Int, query: String)
        fun getCount(): Int
    }

    interface Repo {
        fun fetchPhotoUrls(page: Int, query: String, result: (Result<Photos>) -> Unit)
    }

    interface ItemView {
        fun bindPicture(url: String)
    }
}