package uber.kidinov.flickrloader.loader.view

import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import uber.kidinov.flickrloader.R
import uber.kidinov.flickrloader.common.android.dp
import uber.kidinov.flickrloader.common.picture.LoadingProgress
import uber.kidinov.flickrloader.common.picture.PictureLoader
import uber.kidinov.flickrloader.loader.LoaderContract
import java.util.concurrent.atomic.AtomicInteger

class LoaderAdapter(
    private val presenter: LoaderContract.Presenter,
    private val activity: LoaderActivity,
    private val pictureLoader: PictureLoader
) : BaseAdapter() {

    private val nextGeneratedId = AtomicInteger(1)
    private fun generateListViewId(): Int = nextGeneratedId.getAndIncrement()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageView = if (convertView == null) {
            ImageView(activity).apply {
                layoutParams = ViewGroup.LayoutParams(
                    parent.width / 3 - 4.dp,
                    parent.width / 3
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
                id = generateListViewId()
            }
        } else {
            convertView as ImageView
        }
        if (imageView.tag != getItemId(position)) {
            presenter.bindPicture(position, ItemViewImpl(imageView, pictureLoader))
            imageView.tag = getItemId(position)
        }
        return imageView
    }

    override fun hasStableIds() = true

    override fun getItem(position: Int): Any = presenter.getItem(position)

    override fun getItemId(position: Int): Long = presenter.getItemId(position)

    override fun getCount(): Int = presenter.getCount()

    class ItemViewImpl(
        private val imageView: ImageView,
        private val pictureLoader: PictureLoader
    ) : LoaderContract.ItemView {
        override fun bindPicture(url: String) {
            val progressCallback = object : LoadingProgress {
                override fun onLoadingStarted() {
                    imageView.setImageBitmap(null)
                    imageView.setBackgroundColor(Color.LTGRAY)
                }

                override fun onResult(bitmap: Bitmap) {
                    imageView.setImageBitmap(bitmap)
                }

                override fun onError() {
                    imageView.setImageResource(R.drawable.ic_loading_error)
                }
            }
            pictureLoader.loadPicture(url, imageView.id, progressCallback)
        }
    }
}