package uber.kidinov.flickrloader.common.picture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import uber.kidinov.flickrloader.common.config.Configuration
import java.io.File
import java.io.FileFilter
import java.io.FileOutputStream
import java.math.BigInteger
import java.security.MessageDigest

const val FILE_EXT = "jpg"

class DiskCache(
    private val config: Configuration,
    context: Context
) {
    private val lock = Any()
    private val cacheDir = context.cacheDir

    fun init() {
        synchronized(lock) {
            if (!cacheDir.exists()) cacheDir.mkdir()
        }
    }

    fun get(key: String): Bitmap? {
        synchronized(lock) {
            val file = getFile(key)

            return if (file.exists()) {
                file.setLastModified(System.currentTimeMillis())
                BitmapFactory.decodeFile(file.absolutePath)
            } else null
        }
    }

    fun put(key: String, bitmap: Bitmap) {
        synchronized(lock) {
            val file = getFile(key)
            if (file.exists()) {
                return
            }
            file.setLastModified(System.currentTimeMillis())
            FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out) }
            evictOldFiles()
        }
    }

    fun clear() {
        cacheDir
            .listFiles(FileFilter { it.isFile && it.extension == FILE_EXT })
            ?.forEach { it.delete() }
    }

    private fun evictOldFiles() {
        val files = cacheDir
            .listFiles(FileFilter { it.isFile && it.extension == FILE_EXT })
            .sortedByDescending { it.lastModified() }
        var size = files.size
        while (size >= config.DISK_CACHE_FILES_AMOUNT) {
            size--
            files[size].delete()
        }
    }

    private fun getFile(key: String) = File(cacheDir, "${key.md5()}.$FILE_EXT")

    private fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }
}