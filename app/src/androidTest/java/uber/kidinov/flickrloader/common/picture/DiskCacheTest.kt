package uber.kidinov.flickrloader.common.picture

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import uber.kidinov.flickrloader.common.config.FlickrConfiguration

@RunWith(AndroidJUnit4::class)
class DiskCacheTest {
    private val config = FlickrConfiguration
    private lateinit var diskCache: DiskCache

    @Before
    fun init() {
        diskCache = DiskCache(config, InstrumentationRegistry.getInstrumentation().targetContext)
        diskCache.clear()
    }

    @Test
    fun putShouldRemoveOldFiles() {
        // GIVEN
        val testKeyOne = "testKeyOne"
        val testKeyTwo = "testKeyTwo"
        val testBmp = generateBitmap(5, 5)
        diskCache.put(testKeyOne, testBmp)
        diskCache.put(testKeyTwo, testBmp)

        // last modified of file is not precise
        Thread.sleep(1000)

        for (i in 0 until config.DISK_CACHE_FILES_AMOUNT) {
            diskCache.put(i.toString(), testBmp)
        }

        // WHEN && THEN
        diskCache.get(testKeyOne) shouldEqual null
        diskCache.get(testKeyTwo) shouldEqual null
    }

    private fun generateBitmap(width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.RED
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        return bitmap
    }
}