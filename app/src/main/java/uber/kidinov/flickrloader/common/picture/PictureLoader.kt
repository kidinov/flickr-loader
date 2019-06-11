package uber.kidinov.flickrloader.common.picture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import android.widget.ImageView
import uber.kidinov.flickrloader.common.async.Async
import java.net.URL

class PictureLoader(
    private val async: Async,
    private val diskCache: DiskCache
) {
    private val memoryCache: LruCache<String, Bitmap> =
        object : LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 1024).toInt() / 8) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int = bitmap.byteCount / 1024
        }

    fun loadPicture(imageView: ImageView, url: String, progress: Int, fallback: Int) {
        imageView.setImageResource(progress)

        async.doOnBcg {
            val inMemoryBitmap = memoryCache.get(url)
            if (inMemoryBitmap != null) {
                async.doOnUi { imageView.setImageBitmap(inMemoryBitmap) }
            } else {
                val inDiskBitmap = diskCache.get(url)
                if (inDiskBitmap != null) {
                    async.doOnUi { imageView.setImageBitmap(inDiskBitmap) }
                } else {
                    val picUrl = URL(url)
                    val res = runCatching { BitmapFactory.decodeStream(picUrl.openConnection().getInputStream()) }
                    async.doOnUi {
                        if (res.isSuccess) {
                            val networkBitmap = res.getOrThrow()
                            memoryCache.put(url, networkBitmap)
                            diskCache.put(url, networkBitmap)
                            imageView.setImageBitmap(networkBitmap)
                        } else imageView.setImageResource(fallback)
                    }
                }
            }
        }
    }
}