package uber.kidinov.flickrloader.common.android

import android.content.res.Resources
import android.graphics.Bitmap
import uber.kidinov.flickrloader.common.picture.BitmapDecoder
import java.net.URL


val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

fun URL.downloadBitmapFromURL(size: Int, decoder: BitmapDecoder): Bitmap =
    with(openConnection()) {
        connect()
        val bytes = getInputStream().use { it.readBytes() }
        decoder.decodeByteArray(bytes, 0, bytes.size, size)
    }