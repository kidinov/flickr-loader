package uber.kidinov.flickrloader.common.picture

import android.graphics.Bitmap
import android.graphics.BitmapFactory

class BitmapPool {
    private val pool = LinkedHashMap<Key, LinkedHashSet<Bitmap>>()

    @Synchronized
    fun getBitmap(config: BitmapFactory.Options): Bitmap? {
        println("getBitmap ${config.outWidth} ${config.outHeight}")

        val key = Key(config.outWidth * config.outWidth)
        val bitmaps = pool[key]
        println("bitmaps size - ${bitmaps?.size}")
        if (bitmaps == null || bitmaps.size < 15) return null

        val inPool = bitmaps.first()
        bitmaps.remove(inPool)
        bitmaps.add(inPool)

        return inPool
    }

    @Synchronized
    fun putBitmap(bitmap: Bitmap) {
        println("putBitmap ${bitmap.config}")
        val key = Key(bitmap.height * bitmap.width)
        val group = pool[key]
        if (group == null) pool[key] = LinkedHashSet()
        pool[key]!!.add(bitmap)
    }

    data class Key(val size: Int)
}
