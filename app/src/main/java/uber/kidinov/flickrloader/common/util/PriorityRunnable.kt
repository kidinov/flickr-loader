package uber.kidinov.flickrloader.common.util

class PriorityRunnable(
    val prior: Int,
    private val task: () -> Unit
) : Runnable {
    override fun run() = task()
}

class ComparePriority : Comparator<Runnable> {
    override fun compare(o1: Runnable, o2: Runnable): Int {
        return (o2 as PriorityRunnable).prior.compareTo((o1 as PriorityRunnable).prior)
    }
}