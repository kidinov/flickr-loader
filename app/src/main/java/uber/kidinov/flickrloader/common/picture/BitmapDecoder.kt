package uber.kidinov.flickrloader.common.picture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build

class BitmapDecoder(private val bitmapPool: BitmapPool) {
    fun decodeFile(pathName: String): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(pathName, options)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            options.inMutable = true
            val inBitmap = bitmapPool.getBitmap(options)
            if (inBitmap != null && canUseForInBitmap(inBitmap, options)) {
                options.inBitmap = inBitmap
            }
        }
        options.inJustDecodeBounds = false
        val resBmp = try {
            BitmapFactory.decodeFile(pathName, options)
        } catch (e: Exception) {
            println(e)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                options.inBitmap = null
            }
            BitmapFactory.decodeFile(pathName, options)
        }
        bitmapPool.putBitmap(resBmp)
        return resBmp
    }

    fun decodeByteArray(data: ByteArray, offset: Int, length: Int, size: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        println("size before - ${data.size}")
        println("inSampleSize - ${options.inSampleSize}")
        BitmapFactory.decodeByteArray(data, offset, length, options)
        println("size before - ${options.outWidth} ${options.outHeight}")
        options.inSampleSize = calculateInSampleSize(options, size, size)
        BitmapFactory.decodeByteArray(data, offset, length, options)
        println("inSampleSize - ${options.inSampleSize}")
        val inBitmap = bitmapPool.getBitmap(options)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            options.inMutable = true
            if (inBitmap != null && canUseForInBitmap(inBitmap, options)) {
                println("decodeByteArray inBitmap - $inBitmap")
                options.inBitmap = inBitmap
            }
        }
        options.inJustDecodeBounds = false
        val resBmp = try {
            BitmapFactory.decodeByteArray(data, offset, length, options)
        } catch (e: Exception) {
            println(e)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                options.inBitmap = null
            }
            BitmapFactory.decodeByteArray(data, offset, length, options)
        }
        if (inBitmap == null) bitmapPool.putBitmap(resBmp)
        return resBmp
    }

    fun canUseForInBitmap(
        candidate: Bitmap, targetOptions: BitmapFactory.Options
    ): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // From Android 4.4 (KitKat) onward we can re-use if the byte size of
            // the new bitmap is smaller than the reusable bitmap candidate
            // allocation byte count.
            val width = targetOptions.outWidth / targetOptions.inSampleSize
            val height = targetOptions.outHeight / targetOptions.inSampleSize
            val byteCount = width * height * getBytesPerPixel(candidate.config)

            try {
                return byteCount <= candidate.allocationByteCount
            } catch (e: NullPointerException) {
                return byteCount <= candidate.height * candidate.rowBytes
            }

        }
        // On earlier versions, the dimensions must match exactly and the inSampleSize must be 1
        return (candidate.width == targetOptions.outWidth
                && candidate.height == targetOptions.outHeight
                && targetOptions.inSampleSize == 1)
    }

    private fun getBytesPerPixel(config: Bitmap.Config?): Int {
        var config = config
        // A bitmap by decoding a gif has null "config" in certain environments.
        if (config == null) {
            config = Bitmap.Config.ARGB_8888
        }

        val bytesPerPixel: Int
        bytesPerPixel = when (config) {
            Bitmap.Config.ALPHA_8 -> 1
            Bitmap.Config.RGB_565, Bitmap.Config.ARGB_4444 -> 2
            Bitmap.Config.ARGB_8888 -> 4
            else -> 4
        }
        return bytesPerPixel
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

}