package uber.kidinov.flickrloader.common.android

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.net.URL


val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

fun BitmapFactory.Options.calculateInSampleSize(reqWidth: Int, reqHeight: Int): Int {
    val (height: Int, width: Int) = run { outHeight to outWidth }
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}

fun URL.downloadBitmapFromURL(size: Int): Bitmap {
    with(openConnection()) {
        connect()
        val bytes = getInputStream().use { it.readBytes() }
        BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, this)
            inSampleSize = calculateInSampleSize(size, size)
            inJustDecodeBounds = false
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, this)
        }
    }
}