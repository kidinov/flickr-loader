package uber.kidinov.flickrloader.loader.view

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import uber.kidinov.flickrloader.R
import uber.kidinov.flickrloader.common.android.dp
import uber.kidinov.flickrloader.common.picture.LoadingProgress
import uber.kidinov.flickrloader.common.picture.PictureLoader
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
        Log.d("LoaderAdapter", "getView $convertView")
        presenter.bindPicture(position, ItemViewImpl(imageView, pictureLoader))
        return imageView
    }

    override fun hasStableIds() = true

    override fun getItem(position: Int): Any = Unit

    override fun getItemId(position: Int): Long = presenter.getItemId(position)

    override fun getCount(): Int = presenter.getCount()

    class ItemViewImpl(
        private val imageView: ImageView,
        private val pictureLoader: PictureLoader
    ) : LoaderContract.ItemView {
        override fun bindPicture(url: String) {
            val progressCallback = object : LoadingProgress {
                override fun onLoadingStarted() {
                    imageView.setImageResource(R.drawable.ic_loading_progress)
                }

                override fun onResult(bitmap: Bitmap?) {
                    if (bitmap == null) imageView.setImageResource(R.drawable.ic_loading_error)
                    else imageView.setImageBitmap(bitmap)
                }
            }
            pictureLoader.loadPicture(url, progressCallback)
        }
    }
}