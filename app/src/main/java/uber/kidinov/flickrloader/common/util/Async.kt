package uber.kidinov.flickrloader.common.util

import android.os.Handler
import java.util.concurrent.ExecutorService

enum class BcgPriority(val value: Int) {
    LOW(0), HIGH(1)
}

interface Async {
    fun doOnBcg(priority: BcgPriority = BcgPriority.LOW, task: () -> Unit)
    fun doOnUi(func: () -> Unit)
}

class AsyncImpl(
    private val uiHandler: Handler,
    private val executor: ExecutorService
) : Async {
    override fun doOnBcg(priority: BcgPriority, task: () -> Unit) = executor.execute(
        PriorityRunnable(priority.value, task)
    )

    override fun doOnUi(func: () -> Unit) {
        uiHandler.post { func() }
    }
}