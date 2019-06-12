package uber.kidinov.flickrloader.common.util

import android.os.Handler
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

interface Async {
    fun doOnBcg(task: Runnable): Future<*>
    fun doOnUi(task: Runnable)
    fun removeUiRunnable(task: Runnable)
}

class AsyncImpl(
    private val uiHandler: Handler,
    private val executor: ExecutorService
) : Async {
    override fun doOnBcg(task: Runnable): Future<*> = executor.submit(task)

    override fun doOnUi(task: Runnable) {
        uiHandler.post(task)
    }

    override fun removeUiRunnable(task: Runnable) {
        uiHandler.removeCallbacks(task)
    }
}