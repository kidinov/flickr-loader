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
    private val uiHandler: HandlerWrapper,
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

interface HandlerWrapper {
    fun post(task: Runnable)
    fun removeCallbacks(task: Runnable)
}

class HandlerWrapperImpl(private val handler: Handler) : HandlerWrapper {
    override fun post(task: Runnable) {
        handler.post(task)
    }

    override fun removeCallbacks(task: Runnable) {
        handler.removeCallbacks(task)
    }
}