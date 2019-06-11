package uber.kidinov.flickrloader.loader.presenter

import uber.kidinov.flickrloader.loader.LoaderContract

class LoaderPresenter(
    private val view: LoaderContract.View,
    private val repo: LoaderContract.Repo
) : LoaderContract.Presenter {
    override val photosToShow = mutableListOf<String>()
    override var page: Int = 0

    override fun onQueryChanged(query: String) {
        doFetch(0, query) { photosToShow.clear() }
    }

    override fun onScrolledDown(query: String) {
        doFetch(page, query)
    }

    private fun doFetch(page: Int, query: String, onSuccess: (() -> Unit)? = null) {
        repo.fetchPhotoUrls(page, query) { result ->
            if (result.isFailure) view.showError(result.exceptionOrNull()?.localizedMessage)
            else {
                onSuccess?.invoke()
                photosToShow.addAll(result.getOrThrow().photos.map { it.url })
                view.showPhotos()
            }
        }
    }

    override fun getCount(): Int = photosToShow.size

    override fun bindPicture(position: Int, itemViewImpl: LoaderContract.ItemView) {
        itemViewImpl.bindPicture(photosToShow[position])
    }
}