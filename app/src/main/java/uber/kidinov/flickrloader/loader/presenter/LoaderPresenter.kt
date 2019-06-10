package uber.kidinov.flickrloader.loader.presenter

import uber.kidinov.flickrloader.loader.LoaderContract

class LoaderPresenter(
    private val view: LoaderContract.View,
    private val repo: LoaderContract.Repo
) : LoaderContract.Presenter {
    override fun onQueryChanged(query: String) {
        doFetch(0, query)
    }

    override fun onScrolledDown(page: Int, query: String) {
        doFetch(page, query)
    }

    private fun doFetch(page: Int, query: String) {
        repo.fetchPhotoUrls(page, query) { result ->
            if (result.isFailure) view.showError(result.exceptionOrNull()?.localizedMessage)
            else view.showPhotos(result.getOrThrow())
        }
    }
}