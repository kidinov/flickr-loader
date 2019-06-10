package uber.kidinov.flickrloader.loader

import android.os.Handler
import android.os.Looper
import uber.kidinov.flickrloader.common.async.AsyncImpl
import uber.kidinov.flickrloader.common.servicelocator.commons
import uber.kidinov.flickrloader.loader.model.LoaderRepo
import uber.kidinov.flickrloader.loader.model.PhotosMapperImpl
import uber.kidinov.flickrloader.loader.presenter.LoaderPresenter
import uber.kidinov.flickrloader.loader.view.LoaderActivity
import java.util.concurrent.Executors

fun loader(loaderActivity: LoaderActivity): LoaderComponent = LoaderModule(loaderActivity)

interface LoaderComponent {
    val presenter: LoaderContract.Presenter
    val view: LoaderContract.View
    val repo: LoaderContract.Repo
}

class LoaderModule(private val loaderActivity: LoaderActivity) : LoaderComponent {
    private val async = AsyncImpl(
        Handler(Looper.getMainLooper()),
        Executors.newScheduledThreadPool(2 * Runtime.getRuntime().availableProcessors())
    )
    private val photosMapper = PhotosMapperImpl(commons().configuration)
    private val loaderRepo = LoaderRepo(commons().api, photosMapper, async)

    override val presenter: LoaderContract.Presenter by lazy { LoaderPresenter(loaderActivity, loaderRepo) }
    override val view: LoaderContract.View
        get() = loaderActivity
    override val repo: LoaderContract.Repo
        get() = loaderRepo
}