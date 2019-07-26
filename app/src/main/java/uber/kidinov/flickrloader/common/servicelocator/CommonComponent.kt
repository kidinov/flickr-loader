package uber.kidinov.flickrloader.common.servicelocator

import android.app.Application
import android.os.Handler
import android.os.Looper
import uber.kidinov.flickrloader.common.config.Configuration
import uber.kidinov.flickrloader.common.config.FlickrConfiguration
import uber.kidinov.flickrloader.common.config.GiphyConfigration
import uber.kidinov.flickrloader.common.network.Api
import uber.kidinov.flickrloader.common.network.FlickrApi
import uber.kidinov.flickrloader.common.network.GiphiApi
import uber.kidinov.flickrloader.common.network.NetworkExecutorImpl
import uber.kidinov.flickrloader.common.util.Async
import uber.kidinov.flickrloader.common.util.AsyncImpl
import uber.kidinov.flickrloader.common.util.HandlerWrapperImpl
import uber.kidinov.flickrloader.loader.model.FlickrMapperImpl
import uber.kidinov.flickrloader.loader.model.GiphyMapperImpl
import uber.kidinov.flickrloader.loader.model.PhotosMapper
import java.util.concurrent.Executors

fun commons() = CommonModule

fun injectContextInGraph(application: Application) {
    CommonModule.application = application
}

fun injectFlickr() {
    CommonModule.config = FlickrConfiguration()
    CommonModule.photosMapperInternal = FlickrMapperImpl()
    CommonModule.apiInternal = FlickrApi(CommonModule.networkExecutor, CommonModule.configuration)
}

fun injectGliphy() {
    CommonModule.config = GiphyConfigration()
    CommonModule.photosMapperInternal = GiphyMapperImpl()
    CommonModule.apiInternal = GiphiApi(CommonModule.networkExecutor, CommonModule.configuration)
}

interface CommonComponent {
    val api: Api
    val configuration: Configuration
    val async: Async
    val photosMapper: PhotosMapper
}

object CommonModule : CommonComponent {
    lateinit var application: Application
    lateinit var config: Configuration
    lateinit var photosMapperInternal: PhotosMapper
    lateinit var apiInternal: Api

    val networkExecutor = NetworkExecutorImpl()

    override val photosMapper: PhotosMapper
        get() = photosMapperInternal

    override val configuration: Configuration
        get() = config
    override val api: Api
        get() = apiInternal
    override val async by lazy {
        AsyncImpl(
            HandlerWrapperImpl(Handler(Looper.getMainLooper())),
            Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors())
        )
    }
}