package uber.kidinov.flickrloader.common.picture

import android.graphics.Bitmap
import android.util.LruCache

class BitmapPool {
    private val pool: LruCache<Key, Bitmap> =
        object : LruCache<Key, Bitmap>((Runtime.getRuntime().maxMemory() / 1024).toInt() / 10) {
            override fun sizeOf(key: Key, bitmap: Bitmap): Int = bitmap.byteCount / 1024
        }

    @Synchronized
    fun getBitmap(width: Int, height: Int): Bitmap? {
        println("getBitmap $width $height")
        return pool.get(Key(width, height))
    }

    @Synchronized
    fun putBitmap(bitmap: Bitmap) {
        println("putBitmap ${bitmap.width} ${bitmap.height}")
        pool.put(Key(bitmap.width, bitmap.height), bitmap)
    }

    data class Key(val width: Int, val height: Int)
}
