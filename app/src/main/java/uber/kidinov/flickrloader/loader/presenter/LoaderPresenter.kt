package uber.kidinov.flickrloader.loader.presenter

import uber.kidinov.flickrloader.loader.LoaderContract
import uber.kidinov.flickrloader.loader.model.Photo

class LoaderPresenter(
    private val view: LoaderContract.View,
    private val repo: LoaderContract.Repo
) : LoaderContract.Presenter {
    private var loading = false

    override var state: LoaderContract.State = LoaderContract.State(
        mutableListOf<Photo>(),
        1,
        1
    )

    override fun onQueryChanged(query: String) {
        state.lastPage = 1
        state.pages = 1
        state.photos.clear()
        updateList(0, 0)
        doFetch(state.lastPage, query)
    }

    override fun onScrolledDown(query: String) {
        doFetch(state.lastPage, query)
    }

    private fun doFetch(page: Int, query: String) {
        if (loading) return
        if (state.lastPage > state.pages) return

        loading = true
        repo.fetchPhotoUrls(page, query) { result ->
            loading = false

            if (result.isFailure) view.showError(result.exceptionOrNull()?.localizedMessage)
            else {
                val photos = result.getOrThrow()
                state.photos.addAll(photos.photos)
                state.lastPage++
                state.pages = photos.pages

                updateList(photos.page - 1, photos.pages)
            }
        }
    }

    private fun updateList(pageNum: Int, pagesAmount: Int) {
        view.showSummary(pageNum, pagesAmount)
        view.updateList()
    }

    override fun getItemId(position: Int): Long = state.photos[position].id

    override fun getCount(): Int = state.photos.size

    override fun bindPicture(position: Int, itemViewImpl: LoaderContract.ItemView) {
        itemViewImpl.bindPicture(state.photos[position].url)
    }

    override fun getItem(position: Int): String = state.photos[position].url
}