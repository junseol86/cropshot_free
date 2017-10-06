package ko.hyeonmin.cropshotfree.gallery_utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.AsyncTask
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
    var density = activity.resources.displayMetrics.density
    var bitmapMap = HashMap<Int, Bitmap>()
    var selectedList = ArrayList<Int>()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): GalleryViewHolder =
            GalleryViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.viewholder_gallery, null))

    override fun onBindViewHolder(holder: GalleryViewHolder?, position: Int) {

        itemWidth = activity!!.galleryRv!!.width / 3 - (2 * density).toInt()

        holder?.iv?.visibility = View.GONE
        loadBitmapTask(list!!, bitmapMap, holder!!, itemWidth, position).execute()

        holder?.viewHolderRl?.setOnClickListener{
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

        holder?.viewHolderRl?.setOnLongClickListener {
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

    class loadBitmapTask(val list: ArrayList<String>, val bitmapMap: HashMap<Int, Bitmap>, val holder: GalleryViewHolder, val itemWidth: Int, val position: Int): AsyncTask<Void, Void, Bitmap>() {

        override fun onPreExecute() {
            super.onPreExecute()
            if (bitmapMap.containsKey(position)) {
                holder.iv?.setImageBitmap(bitmapMap[position])
                holder.iv?.visibility = View.VISIBLE
            }
        }

        override fun doInBackground(vararg params: Void?): Bitmap {
            return if (bitmapMap.containsKey(position)) bitmapMap[position]!! else loadThumbBitmap(position)
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            if (!bitmapMap.containsKey(position)) {
                holder.iv?.setImageBitmap(result!!)
                holder.iv?.visibility = View.VISIBLE
                bitmapMap.put(position, result!!)
            }
        }

        private fun loadThumbBitmap(position: Int): Bitmap {
            var orgBitmap = BitmapFactory.decodeFile(list!![position])

            val itemBitmap = if (orgBitmap.width > orgBitmap.height) {
                Bitmap.createScaledBitmap(orgBitmap, itemWidth, (orgBitmap.height * itemWidth.toFloat() / orgBitmap.width).toInt(), true)
            } else {
                Bitmap.createScaledBitmap(orgBitmap, (orgBitmap.width * itemWidth.toFloat() / orgBitmap.height).toInt(), itemWidth, true)
            }

            return itemBitmap!!
        }
    }

}