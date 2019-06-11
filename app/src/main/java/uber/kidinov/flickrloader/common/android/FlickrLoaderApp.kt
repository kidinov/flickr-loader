package uber.kidinov.flickrloader.common.android

import android.app.Application
import uber.kidinov.flickrloader.common.servicelocator.injectContextInGraph

class FlickrLoaderApp : Application() {
    override fun onCreate() {
        super.onCreate()

        injectContextInGraph(this)
    }
}