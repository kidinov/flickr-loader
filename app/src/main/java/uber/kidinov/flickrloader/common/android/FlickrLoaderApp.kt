package uber.kidinov.flickrloader.common.android

import android.app.Application
import uber.kidinov.flickrloader.common.servicelocator.commons
import uber.kidinov.flickrloader.common.servicelocator.injectContextInGraph

class FlickrLoaderApp : Application() {
    private val diskCache by lazy { commons().diskCache }

    override fun onCreate() {
        super.onCreate()

        injectContextInGraph(this)
        diskCache.init()
    }
}