package uber.kidinov.flickrloader.common.servicelocator

import uber.kidinov.flickrloader.common.config.Configuration
import uber.kidinov.flickrloader.common.network.Api
import uber.kidinov.flickrloader.common.network.FlickrApi
import uber.kidinov.flickrloader.common.network.NetworkExecutorImpl

fun commons() = CommonModule

interface CommonComponent {
    val api: Api
    val configuration: Configuration
}

object CommonModule : CommonComponent {
    private val executor = NetworkExecutorImpl()
    private val config = Configuration()

    override val configuration: Configuration
        get() = config
    override val api: Api
        get() = FlickrApi(executor, config)
}