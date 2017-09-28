package ko.hyeonmin.cropshotfree.gallery_utils

import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v4.content.FileProvider
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import ko.hyeonmin.cropshotfree.R
import ko.hyeonmin.cropshotfree.activities.GalleryActivity
import java.io.File

/**
 * Created by junse on 2017-09-27.
 */
class GalleryPagerAdapter(val activity: GalleryActivity): PagerAdapter() {
    private var inflater = LayoutInflater.from(activity)
    var view: View? = null
    var image: ImageView? = null

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
//        return super.instantiateItem(container, position)
        view = inflater.inflate(R.layout.viewpage_gallery, container, false)
        image = view!!.findViewById(R.id.gallery_page_image) as ImageView
        image?.setImageBitmap(BitmapFactory.decodeFile(activity.list[position]))
        image?.setOnClickListener{

            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.setDataAndType(FileProvider.getUriForFile(
                    activity,
                    "ko.hyeonmin.cropshotfree.provider",
                    File(activity.list[position])),
                    "image/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            activity.startActivity(intent)
        }
        container?.addView(view)
        return view!!
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
//        super.destroyItem(container, position, `object`)
        container?.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return activity.list.size
    }
}