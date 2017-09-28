package ko.hyeonmin.cropshotfree.gallery_utils

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.content.FileProvider
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ko.hyeonmin.cropshotfree.R
import ko.hyeonmin.cropshotfree.activities.GalleryActivity
import java.io.File

/**
 * Created by junse on 2017-09-26.
 */
class GalleryRecyclerAdapter(activity: GalleryActivity, list: ArrayList<String>) : RecyclerView.Adapter<GalleryViewHolder>() {

    private var list: ArrayList<String>? = list
    var activity: GalleryActivity? = activity
    var itemWidth = 0
    var orgBitmap: Bitmap? = null
    var itemBitmap: Bitmap? = null
    var cropSize = 0f
    var density = activity.resources.displayMetrics.density
    var bitmapMap = HashMap<Int, Bitmap>()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): GalleryViewHolder =
            GalleryViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.viewholder_gallery, null))

    override fun onBindViewHolder(holder: GalleryViewHolder?, position: Int) {

        if (bitmapMap.containsKey(position)) {
            itemBitmap = bitmapMap[position]
        } else {
            itemWidth = activity!!.galleryRv!!.width / 3 - (8 * density).toInt()
            orgBitmap = BitmapFactory.decodeFile(list!![position])

            if (orgBitmap!!.width.toFloat() / orgBitmap!!.height.toFloat() > 1.5) {
                cropSize = orgBitmap!!.height * 1.5f
                orgBitmap = Bitmap.createBitmap(orgBitmap, ((orgBitmap!!.width - cropSize) / 2).toInt(), 0, cropSize.toInt(), orgBitmap!!.height)
            } else if (orgBitmap!!.height.toFloat() / orgBitmap!!.width.toFloat() > 1.5) {
                cropSize = orgBitmap!!.width * 1.5f
                if (cropSize < itemWidth && orgBitmap!!.height >= itemWidth)
                    cropSize = itemWidth.toFloat() - 8 * density
                orgBitmap = Bitmap.createBitmap(orgBitmap, 0, ((orgBitmap!!.height - cropSize) / 2).toInt(), orgBitmap!!.width, cropSize.toInt())
            }

            if (orgBitmap!!.width > itemWidth) {
                itemBitmap = Bitmap.createScaledBitmap(orgBitmap, itemWidth, orgBitmap!!.height * itemWidth / orgBitmap!!.width, true)
            } else {
                itemBitmap = orgBitmap
            }

            bitmapMap.put(position, itemBitmap!!)
        }

        holder?.iv?.setImageBitmap(itemBitmap)
        holder?.iv?.setOnClickListener{

            activity!!.pickAnImage(position)

        }
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

}