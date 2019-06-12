package uber.kidinov.flickrloader.common.picture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import uber.kidinov.flickrloader.common.util.Async
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

class PictureLoader(
    private val async: Async,
    private val diskCache: DiskCache
) {
    private val currentlyTargetedViews by lazy { ConcurrentHashMap<Int, Int>() }

    private val memoryCache: LruCache<String, Bitmap> =
        object :
            LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 1024).toInt() / 1000) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int = bitmap.byteCount / 1024
        }

    fun loadPicture(url: String, viewId: Int, progressCallback: LoadingProgress) {
        async.doOnBcg {
            val inMemoryBitmap = memoryCache.get(url)
            if (inMemoryBitmap != null) {
                async.doOnUi { progressCallback.onResult(inMemoryBitmap) }
                return@doOnBcg
            }

            if (currentlyTargetedViews.contains(viewId)) {
                currentlyTargetedViews[viewId] = currentlyTargetedViews[viewId]!!.plus(1)
            } else {
                currentlyTargetedViews[viewId] = 1
            }
            println("add id $viewId")
            async.doOnUi { progressCallback.onLoadingStarted() }
            val inDiskBitmap = diskCache.get(url)
            if (inDiskBitmap != null) {
                if (currentlyTargetedViews[viewId]!! > 1) {
                    currentlyTargetedViews.remove(viewId)
                    return@doOnBcg
                }

                currentlyTargetedViews.remove(viewId)

                println("return from disk $url")
                async.doOnUi { progressCallback.onResult(inDiskBitmap) }
                return@doOnBcg
            }

            val picUrl = URL(url)
            println("load $url")
            val res =
                runCatching { BitmapFactory.decodeStream(picUrl.openConnection().getInputStream()) }
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