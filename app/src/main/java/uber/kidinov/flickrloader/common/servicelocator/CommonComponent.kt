package uber.kidinov.flickrloader.common.servicelocator

import android.app.Application
import android.os.Handler
import android.os.Looper
import uber.kidinov.flickrloader.common.async.Async
import uber.kidinov.flickrloader.common.async.AsyncImpl
import uber.kidinov.flickrloader.common.config.Configuration
import uber.kidinov.flickrloader.common.network.Api
import uber.kidinov.flickrloader.common.network.FlickrApi
import uber.kidinov.flickrloader.common.network.NetworkExecutorImpl
import uber.kidinov.flickrloader.common.picture.PictureLoader
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
}

object CommonModule : CommonComponent {
    lateinit var application: Application

    private val executor = NetworkExecutorImpl()

    override val configuration by lazy { Configuration }

    override val api: Api by lazy { FlickrApi(executor, configuration) }
    override val pictureLoader by lazy { PictureLoader(application, async) }
    override val async by lazy {
        AsyncImpl(
            Handler(Looper.getMainLooper()),
            Executors.newScheduledThreadPool(2 * Runtime.getRuntime().availableProcessors())
        )
    }
}