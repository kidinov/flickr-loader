package uber.kidinov.flickrloader.loader.util

import uber.kidinov.flickrloader.common.util.HandlerWrapper
import java.util.Collections
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

fun currentThreadExecutorService(): ExecutorService = object : AbstractExecutorService() {
    @Volatile
    private var terminated: Boolean = false

    override fun shutdown() {
        terminated = true
    }

    override fun isShutdown(): Boolean {
        return terminated
    }

    override fun isTerminated(): Boolean {
        return terminated
    }

    @Throws(InterruptedException::class)
    override fun awaitTermination(theTimeout: Long, theUnit: TimeUnit): Boolean {
        shutdown()
        return terminated
    }

    override fun shutdownNow(): List<Runnable> {
        return Collections.emptyList()
    }

    override fun execute(theCommand: Runnable) {
        theCommand.run()
    }
}

fun currentHandlerWrapper(): HandlerWrapper = object : HandlerWrapper {
    override fun post(task: Runnable) {
        task.run()
    }

    override fun removeCallbacks(task: Runnable) {
        task.run()
    }

}