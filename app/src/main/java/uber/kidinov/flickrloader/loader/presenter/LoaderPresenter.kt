package uber.kidinov.flickrloader.loader.presenter

import uber.kidinov.flickrloader.loader.LoaderContract
import uber.kidinov.flickrloader.loader.model.Photo

class LoaderPresenter(
    private val view: LoaderContract.View,
    private val repo: LoaderContract.Repo
) : LoaderContract.Presenter {
    private var loading = false

    override var state: LoaderContract.State = LoaderContract.State(
        0,
        0,
        mutableListOf()
    )

    override fun onRecreated() {
        updateList(state.pageNum - 1, state.pages)
    }

    override fun onQueryChanged(query: String) {
        with(state) {
            pageNum = 0
            pages = 0
            photos.clear()
        }
        updateList(state.pageNum, state.pages)
        doFetch(state.pageNum, query)
    }

    override fun onScrolledDown(query: String) {
        doFetch(state.pageNum, query)
    }

    override fun getItemId(position: Int): Long = state.photos[position].id

    override fun getCount(): Int = state.photos.size

    override fun bindPicture(position: Int, itemView: LoaderContract.ItemView) {
        itemView.bindPicture(state.photos[position].url)
    }

    override fun getItem(position: Int): Photo = state.photos[position]

    private fun doFetch(page: Int, query: String) {
        if (loading) return
        if (state.pageNum > state.pages) return

        loading = true
        repo.fetchPhotoUrls(page, query) { result ->
            loading = false

            if (result.isSuccess) {
                val response = result.getOrThrow()
                with(state) {
                    pageNum += 1
                    pages = response.pages
                    photos.addAll(response.photos)
                }

                updateList(state.pageNum - 1, state.pages)
            } else {
                view.showError(result.exceptionOrNull()?.localizedMessage)
            }
        }
    }

    private fun updateList(pageNum: Int, pagesAmount: Int) {
        view.showSummary(pageNum, pagesAmount)
        view.requestListDataChanged()
    }
}