package uber.kidinov.flickrloader.common.android

import android.content.res.Resources
import android.graphics.Bitmap
import java.util.concurrent.atomic.AtomicInteger


val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

fun Bitmap.resize(maxWidth: Int, maxHeight: Int): Bitmap {
    val ratioBitmap = width.toFloat() / height.toFloat()
    val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

    var finalWidth = maxWidth
    var finalHeight = maxHeight
    if (ratioMax > ratioBitmap) finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
    else finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
    val res = Bitmap.createScaledBitmap(this, finalWidth, finalHeight, true)
    this.recycle()
    return res
}