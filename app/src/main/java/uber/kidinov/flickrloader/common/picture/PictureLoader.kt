package uber.kidinov.flickrloader.common.picture

import android.graphics.Bitmap
import android.util.LruCache
import uber.kidinov.flickrloader.common.android.downloadBitmapFromURL
import uber.kidinov.flickrloader.common.config.Configuration
import uber.kidinov.flickrloader.common.util.Async
import java.io.InterruptedIOException
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Future
import java.util.concurrent.FutureTask

class PictureLoader(
    private val async: Async,
    private val diskCache: DiskCache,
    private val config: Configuration,
    private val bitmapDecoder: BitmapDecoder
) {
    private val bcgFutureMap by lazy { ConcurrentHashMap<Int, Future<*>>() }
    private val uiRunnableMap by lazy { ConcurrentHashMap<Int, Runnable>() }

    private val memoryCache: LruCache<String, Bitmap> =
        object : LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 1024).toInt() / 4) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int = bitmap.byteCount / 1024
        }

    fun loadPicture(url: String, viewId: Int, progressCallback: LoadingProgress) {
        stopBcgRequest(viewId)
        stopUiRequest(viewId)

        // Check memory cache
        memoryCache.get(url)?.run {
            progressCallback.onResult(this)
            return
        }

        progressCallback.onLoadingStarted()
        val future = async.doOnBcg(FutureTask {
            // Check disk cache
//            diskCache.get(url)?.run {
//                memoryCache.put(url, this)
//                if (Thread.currentThread().isInterrupted) return@FutureTask
//                progressCallback.onResult(this)
//                return@FutureTask
//            }

            // Download picture
            val bitmapResult = runCatching { URL(url).downloadBitmapFromURL(config.PIC_TARGET_SIZE, bitmapDecoder) }

            if (bitmapResult.exceptionOrNull() is InterruptedIOException
                || Thread.currentThread().isInterrupted
            ) {
                return@FutureTask
            }

            val runOnUI = if (bitmapResult.isSuccess) {
                val networkBitmap = bitmapResult.getOrThrow()
                memoryCache.put(url, networkBitmap)
                diskCache.put(url, networkBitmap)
                if (Thread.currentThread().isInterrupted) return@FutureTask
                Runnable { progressCallback.onResult(networkBitmap) }
            } else {
                bitmapResult.exceptionOrNull()?.printStackTrace()
                Runnable { progressCallback.onError() }
            }
            uiRunnableMap[viewId] = runOnUI
            async.doOnUi(runOnUI)
        })

        bcgFutureMap[viewId] = future
    }

    private fun stopUiRequest(viewId: Int) {
        uiRunnableMap[viewId]?.run {
            async.removeUiRunnable(this)
            uiRunnableMap.remove(viewId)
        }
    }

    private fun stopBcgRequest(viewId: Int) {
        bcgFutureMap[viewId]?.run {
            cancel(true)
            bcgFutureMap.remove(viewId)
        }
    }
}

interface LoadingProgress {
    fun onLoadingStarted()
    fun onResult(bitmap: Bitmap)
    fun onError()
}