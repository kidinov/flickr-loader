package uber.kidinov.flickrloader.common.servicelocator

import android.app.Application
import android.os.Handler
import android.os.Looper
import uber.kidinov.flickrloader.common.config.Configuration
import uber.kidinov.flickrloader.common.network.Api
import uber.kidinov.flickrloader.common.network.FlickrApi
import uber.kidinov.flickrloader.common.network.NetworkExecutorImpl
import uber.kidinov.flickrloader.common.picture.DiskCache
import uber.kidinov.flickrloader.common.picture.PictureLoader
import uber.kidinov.flickrloader.common.util.Async
import uber.kidinov.flickrloader.common.util.AsyncImpl
import uber.kidinov.flickrloader.common.util.HandlerWrapperImpl
import java.util.concurrent.Executors

fun commons() = CommonModule

fun injectContextInGraph(application: Application) {
    CommonModule.application = application
}

interface CommonComponent {
    val api: Api
    val configuration: Configuration
    val pictureLoader: PictureLoader
    val async: Async
    val diskCache: DiskCache
}

object CommonModule : CommonComponent {
    lateinit var application: Application

    private val networkExecutor = NetworkExecutorImpl()

    override val configuration by lazy { Configuration }
    override val api: Api by lazy { FlickrApi(networkExecutor, configuration) }
    override val diskCache: DiskCache by lazy { DiskCache(configuration, application) }
    override val pictureLoader by lazy { PictureLoader(async, diskCache, configuration) }
    override val async by lazy {
        AsyncImpl(
            HandlerWrapperImpl(Handler(Looper.getMainLooper())),
            Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors())
        )
    }
}