package uber.kidinov.flickrloader.common.servicelocator

import uber.kidinov.flickrloader.loader.LoaderContract
import uber.kidinov.flickrloader.loader.model.LoaderRepo
import uber.kidinov.flickrloader.loader.model.PhotosMapperImpl
import uber.kidinov.flickrloader.loader.presenter.LoaderPresenter
import uber.kidinov.flickrloader.loader.view.LoaderActivity
import uber.kidinov.flickrloader.loader.view.LoaderAdapter

fun loader(loaderActivity: LoaderActivity) = LoaderModule(loaderActivity)

interface LoaderComponent {
    val presenter: LoaderContract.Presenter
    val view: LoaderContract.View
    val repo: LoaderContract.Repo
    val adapter: LoaderAdapter
}

class LoaderModule(private val activity: LoaderActivity) : LoaderComponent {
    private val photosMapper = PhotosMapperImpl(commons().configuration)

    override val presenter: LoaderContract.Presenter by lazy { LoaderPresenter(activity, repo) }
    override val adapter: LoaderAdapter by lazy {
        LoaderAdapter(
            presenter,
            activity,
            commons().pictureLoader
        )
    }
    override val repo: LoaderContract.Repo by lazy {
        LoaderRepo(
            commons().api,
            photosMapper,
            commons().async
        )
    }
    override val view: LoaderContract.View
        get() = activity
}