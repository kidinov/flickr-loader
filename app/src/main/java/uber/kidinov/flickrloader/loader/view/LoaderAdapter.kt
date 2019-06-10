package uber.kidinov.flickrloader.loader.view

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import uber.kidinov.flickrloader.loader.LoaderContract
import uber.kidinov.flickrloader.loader.presenter.LoaderPresenter

class LoaderAdapter(
    private val presenter: LoaderPresenter,
    private val activity: LoaderActivity
) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView: ImageView

        if (convertView == null) {
            imageView = ImageView(activity)
            imageView.layoutParams = ViewGroup.LayoutParams(130, 130)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setPadding(16, 16, 16, 16)
        } else {
            imageView = convertView as ImageView
        }
        presenter.bindView(position, object: LoaderContract.ItemView())
        imageView.setImageResource(imageIDs[position])
        return imageView
    }

    override fun getItem(position: Int): Any = Unit

    override fun getItemId(position: Int): Long = 0

    override fun getCount(): Int = presenter.getCount()

    class ItemViewImpl(imageView: ImageView): LoaderContract.ItemView {
        override fun bindPicture(url: String) {
        }
    }
}