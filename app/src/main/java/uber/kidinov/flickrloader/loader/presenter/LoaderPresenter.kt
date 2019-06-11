package uber.kidinov.flickrloader.loader.presenter

import uber.kidinov.flickrloader.loader.LoaderContract
import uber.kidinov.flickrloader.loader.model.Photo

class LoaderPresenter(
    private val view: LoaderContract.View,
    private val repo: LoaderContract.Repo
) : LoaderContract.Presenter {
    private var loading = false

    override val photos = mutableListOf<Photo>()
    override var lastPage: Int = 1
    override var pages: Int = 1

    override fun onQueryChanged(query: String) {
        lastPage = 1
        pages = 1
        doFetch(lastPage, query) { photos.clear() }
    }

    override fun onScrolledDown(query: String) {
        doFetch(lastPage, query)
    }

    private fun doFetch(page: Int, query: String, onSuccessExt: (() -> Unit)? = null) {
        if (loading) return
        if (lastPage > pages) return

        loading = true
        repo.fetchPhotoUrls(page, query) { result ->
            loading = false

            if (result.isFailure) view.showError(result.exceptionOrNull()?.localizedMessage)
            else {
                onSuccessExt?.invoke()

                val photos = result.getOrThrow()
                this.photos.addAll(photos.photos)
                lastPage++
                pages = photos.pages

                view.showSummary(photos.page - 1, photos.pages)
                view.updateList()
            }
        }
    }

    override fun getItemId(position: Int): Long = photos[position].id

    override fun getCount(): Int = photos.size

    override fun bindPicture(position: Int, itemViewImpl: LoaderContract.ItemView) {
        itemViewImpl.bindPicture(photos[position].url)
    }
}