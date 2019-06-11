package uber.kidinov.flickrloader.common.picture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import uber.kidinov.flickrloader.common.util.Async
import uber.kidinov.flickrloader.common.util.BcgPriority
import java.net.URL
import java.util.concurrent.ConcurrentLinkedQueue

class PictureLoader(
    private val async: Async,
    private val diskCache: DiskCache
) {
    private val currentlyLoadingQueue by lazy { ConcurrentLinkedQueue<String>() }

    private val memoryCache: LruCache<String, Bitmap> =
        object : LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 1024).toInt() / 1000) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int = bitmap.byteCount / 1024
        }

    fun loadPicture(url: String, progressCallback: LoadingProgress) {
        async.doOnBcg {
            val inMemoryBitmap = memoryCache.get(url)
            if (inMemoryBitmap != null) {
                async.doOnUi { progressCallback.onResult(inMemoryBitmap) }
                return@doOnBcg
            }

            val inDiskBitmap = diskCache.get(url)
            if (inDiskBitmap != null) {
                async.doOnUi { progressCallback.onResult(inDiskBitmap) }
                return@doOnBcg
            }

            async.doOnUi { progressCallback.onLoadingStarted() }
            val picUrl = URL(url)
            println("load $url")
            val res = runCatching { BitmapFactory.decodeStream(picUrl.openConnection().getInputStream()) }

            println("load ${currentlyLoadingQueue.joinToString(",")}")
            if (currentlyLoadingQueue.contains(url)) return@doOnBcg
            currentlyLoadingQueue.add(url)
            if (res.isSuccess) {
                val networkBitmap = res.getOrThrow()
                memoryCache.put(url, networkBitmap)
                diskCache.put(url, networkBitmap)
                async.doOnUi { progressCallback.onResult(networkBitmap) }
            } else async.doOnUi { progressCallback.onResult(null) }
        }
    }
}

interface LoadingProgress {
    fun onLoadingStarted()

    fun onResult(bitmap: Bitmap?)
}