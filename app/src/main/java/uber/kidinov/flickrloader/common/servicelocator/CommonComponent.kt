package uber.kidinov.flickrloader.common.servicelocator

import android.app.Application
import android.os.Handler
import android.os.Looper
import uber.kidinov.flickrloader.common.config.Configuration
import uber.kidinov.flickrloader.common.network.Api
import uber.kidinov.flickrloader.common.network.FlickrApi
import uber.kidinov.flickrloader.common.network.NetworkExecutorImpl
import uber.kidinov.flickrloader.common.picture.BitmapDecoder
import uber.kidinov.flickrloader.common.picture.BitmapPool
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
    val bitmapDecoder: BitmapDecoder
}

object CommonModule : CommonComponent {
    lateinit var application: Application

    private val networkExecutor = NetworkExecutorImpl()
    private val bitmapPool = BitmapPool()

    override val bitmapDecoder by lazy { BitmapDecoder(bitmapPool) }
    override val configuration by lazy { Configuration }
    override val api: Api by lazy { FlickrApi(networkExecutor, configuration) }
    override val diskCache: DiskCache by lazy { DiskCache(configuration, bitmapDecoder, application) }
    override val pictureLoader by lazy { PictureLoader(async, diskCache, configuration, bitmapDecoder) }
    override val async by lazy {
        AsyncImpl(
            HandlerWrapperImpl(Handler(Looper.getMainLooper())),
            Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors())
        )
    }
}