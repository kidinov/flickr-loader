package uber.kidinov.flickrloader.common.async

import android.os.Handler
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

interface Async {
    fun doAsync(task: () -> Unit): Future<Unit>

    fun onUiThread(func: () -> Unit)
}

class AsyncImpl(
    private val uiHandler: Handler,
    private val executor: ExecutorService
) : Async {
    override fun doAsync(task: () -> Unit): Future<Unit> = executor.submit { task() } as Future<Unit>

    override fun onUiThread(func: () -> Unit) {
        uiHandler.post { func() }
    }
}