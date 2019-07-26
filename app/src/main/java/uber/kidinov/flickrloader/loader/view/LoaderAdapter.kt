package uber.kidinov.flickrloader.loader.view

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import uber.kidinov.flickrloader.R
import uber.kidinov.flickrloader.common.android.dp
import uber.kidinov.flickrloader.loader.LoaderContract
import java.util.concurrent.atomic.AtomicInteger

class LoaderAdapter(
    private val presenter: LoaderContract.Presenter,
    private val activity: LoaderActivity
) : BaseAdapter() {
    private val nextGeneratedId = AtomicInteger(0)
    private fun generateListViewId(): Int = nextGeneratedId.getAndIncrement()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageView = if (convertView == null) {
            ImageView(activity).apply {
                buildAndSetLayoutParams(parent)
                scaleType = ImageView.ScaleType.CENTER_CROP
                id = generateListViewId()
            }
        } else {
            convertView as ImageView
        }
        if (imageView.width == 0) imageView.buildAndSetLayoutParams(parent)

        if (imageView.tag != getItemId(position)) {
            presenter.bindPicture(position, ItemViewImpl(imageView))
            imageView.tag = getItemId(position)
        }
        return imageView
    }

    private fun ImageView.buildAndSetLayoutParams(parent: ViewGroup) {
        layoutParams = ViewGroup.LayoutParams(
            parent.width / 3 - 4.dp,
            parent.width / 3
        )
    }

    override fun hasStableIds() = true

    override fun getItem(position: Int): Any = presenter.getItem(position)

    override fun getItemId(position: Int): Long = presenter.getItemId(position)

    override fun getCount(): Int = presenter.getCount()
}

class ItemViewImpl(
    private val imageView: ImageView
) : LoaderContract.ItemView {
    override fun bindPicture(url: String) {
        Glide
            .with(imageView.context)
            .load(url)
            .centerCrop()
            .placeholder(R.drawable.ic_loading_error)
            .into(imageView)
    }
}