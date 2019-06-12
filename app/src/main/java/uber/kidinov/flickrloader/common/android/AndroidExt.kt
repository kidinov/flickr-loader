package uber.kidinov.flickrloader.common.android

import android.content.res.Resources
import java.util.concurrent.atomic.AtomicInteger

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

val nextGeneratedId = AtomicInteger(1)
fun generateListViewId(): Int = nextGeneratedId.getAndIncrement()