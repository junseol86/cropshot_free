package ko.hyeonmin.cropshotfree.recycler_parts

import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ko.hyeonmin.cropshotfree.R
import ko.hyeonmin.cropshotfree.activities.GalleryActivity
import java.io.File

/**
 * Created by junse on 2017-09-26.
 */
class GalleryAdapter: RecyclerView.Adapter<GalleryViewHolder> {

    var list: ArrayList<String>? = null
    var activity: GalleryActivity? = null

    constructor(activity: GalleryActivity, list: ArrayList<String>) {
        this.activity = activity
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): GalleryViewHolder =
            GalleryViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.viewholder_gallery, null))

    override fun onBindViewHolder(holder: GalleryViewHolder?, position: Int) {
        holder?.iv?.setImageBitmap(BitmapFactory.decodeFile(list!![position]))
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

}