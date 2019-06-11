package uber.kidinov.flickrloader.loader.view

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import uber.kidinov.flickrloader.R
import uber.kidinov.flickrloader.common.picture.PictureLoader
import uber.kidinov.flickrloader.common.util.dp
import uber.kidinov.flickrloader.loader.LoaderContract

class LoaderAdapter(
    private val presenter: LoaderContract.Presenter,
    private val activity: LoaderActivity,
    private val pictureLoader: PictureLoader
) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageView = if (convertView == null) {
            ImageView(activity).apply {
                layoutParams = ViewGroup.LayoutParams(
                    parent.width / 3 - 4.dp,
                    parent.width / 3
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        } else {
            convertView as ImageView
        }
        presenter.bindPicture(position, ItemViewImpl(imageView, pictureLoader))
        return imageView
    }

    override fun getItem(position: Int): Any = Unit

    override fun getItemId(position: Int): Long = 0

    override fun getCount(): Int = presenter.getCount()

    class ItemViewImpl(
        private val imageView: ImageView,
        private val pictureLoader: PictureLoader
    ) : LoaderContract.ItemView {
        override fun bindPicture(url: String) {
            pictureLoader.loadPicture(
                imageView,
                url,
                R.drawable.ic_loading_progress,
                R.drawable.ic_loading_error
            )
        }
    }
}