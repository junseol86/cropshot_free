package ko.hyeonmin.cropshotfree.gallery_utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import ko.hyeonmin.cropshotfree.R
import ko.hyeonmin.cropshotfree.activities.GalleryActivity

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
    var selectedList = ArrayList<Int>()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): GalleryViewHolder =
            GalleryViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.viewholder_gallery, null))

    override fun onBindViewHolder(holder: GalleryViewHolder?, position: Int) {

        if (bitmapMap.containsKey(position)) {
            itemBitmap = bitmapMap[position]
        } else {
            itemWidth = activity!!.galleryRv!!.width / 3 - (8 * density).toInt()
            orgBitmap = BitmapFactory.decodeFile(list!![position])

            if (orgBitmap!!.width.toFloat() / orgBitmap!!.height.toFloat() > 1.5) {
                cropSize = if (orgBitmap!!.width > itemWidth) itemWidth.toFloat() else orgBitmap!!.width.toFloat()
                orgBitmap = Bitmap.createBitmap(orgBitmap, ((orgBitmap!!.width - cropSize) / 2).toInt(), 0, cropSize.toInt(), orgBitmap!!.height)
            } else if (orgBitmap!!.height.toFloat() / orgBitmap!!.width.toFloat() > 1.5) {
                cropSize = if (orgBitmap!!.height > Math.max(orgBitmap!!.width, itemWidth) * 1.5f) Math.max(orgBitmap!!.width, itemWidth) * 1.5f else orgBitmap!!.height.toFloat()
                orgBitmap = Bitmap.createBitmap(orgBitmap, 0, ((orgBitmap!!.height - cropSize) / 2).toInt(), orgBitmap!!.width, cropSize.toInt())
            }

            itemBitmap = if (orgBitmap!!.width > itemWidth) {
                Bitmap.createScaledBitmap(orgBitmap, itemWidth, orgBitmap!!.height * itemWidth / orgBitmap!!.width, true)
            } else {
                orgBitmap
            }

            bitmapMap.put(position, itemBitmap!!)
        }

        holder?.iv?.setImageBitmap(itemBitmap)
        holder?.iv?.setOnClickListener{
            if (activity!!.selectOn) {
                if (selectedList.contains(position)) {
                    selectedList.remove(position)
                } else {
                    selectedList.add(position)
                }
                notifyDataSetChanged()
                activity?.galleryTopbar?.removeBtnRl?.visibility = if (selectedList.isEmpty()) View.GONE else View.VISIBLE
            } else {
                activity!!.pickAnImage(position)
            }
        }

        holder?.iv?.setOnLongClickListener {
            if (activity!!.selectOn) false
            activity?.setSelectOnOff(true)
            selectedList.add(position)
            false
        }

        holder?.selectOnOff?.visibility = if (activity!!.selectOn) View.VISIBLE else View.GONE
        if (activity!!.selectOn) {
            holder?.selectOnOff?.setImageResource(if (selectedList.contains(position)) R.drawable.select_on else R.drawable.select_off)
        }
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

}