package uber.kidinov.flickrloader.common.picture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import uber.kidinov.flickrloader.common.android.resize
import uber.kidinov.flickrloader.common.util.Async
import java.io.InterruptedIOException
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Future
import java.util.concurrent.FutureTask

class PictureLoader(
    private val async: Async,
    private val diskCache: DiskCache
) {
    private val bcgFutureMap by lazy { ConcurrentHashMap<Int, Future<*>>() }
    private val uiRunnableMap by lazy { ConcurrentHashMap<Int, Runnable>() }

    private val memoryCache: LruCache<String, Bitmap> =
        object : LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 1024).toInt() / 4) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int = bitmap.byteCount / 1024
        }

    fun loadPicture(url: String, viewId: Int, progressCallback: LoadingProgress) {
        progressCallback.onLoadingStarted()

//        val inMemoryBitmap = memoryCache.get(url)
//        if (inMemoryBitmap != null) {
//            progressCallback.onResult(inMemoryBitmap)
//            return
//        }
//
//        val inDiskBitmap = diskCache.get(url)
//        if (inDiskBitmap != null) {
//            memoryCache.put(url, inDiskBitmap)
//            progressCallback.onResult(inDiskBitmap)
//            return
//        }

        bcgFutureMap[viewId]?.run {
            cancel(true)
            bcgFutureMap.remove(viewId)
        }

        uiRunnableMap[viewId]?.run {
            async.removeUiRunnable(this)
            uiRunnableMap.remove(viewId)
        }

        println("PictureLoader bcgFutureMap size - ${bcgFutureMap.size}")
        println("PictureLoader uiRunnableMap size - ${uiRunnableMap.size}")

        val future = async.doOnBcg(FutureTask {
            val picUrl = URL(url)
            val bitmapResult = runCatching {
                BitmapFactory.decodeStream(picUrl.openConnection().getInputStream())
                    ?: throw InterruptedIOException()
            }

            println("PictureLoader bitmapResult - $bitmapResult")
            if (bitmapResult.exceptionOrNull() is InterruptedIOException || Thread.currentThread().isInterrupted)
                return@FutureTask

            val toRunOnUI = if (bitmapResult.isSuccess) {
                val networkBitmap = bitmapResult.getOrThrow().resize(200, 200)
                memoryCache.put(url, networkBitmap)
                diskCache.put(url, networkBitmap)
                if (Thread.currentThread().isInterrupted) return@FutureTask
                Runnable { progressCallback.onResult(networkBitmap) }
            } else {
                Runnable { progressCallback.onError() }
            }
            uiRunnableMap[viewId] = toRunOnUI
            async.doOnUi(toRunOnUI)
        })

        bcgFutureMap[viewId] = future
    }
}

interface LoadingProgress {
    fun onLoadingStarted()

    fun onResult(bitmap: Bitmap)

    fun onError()
}