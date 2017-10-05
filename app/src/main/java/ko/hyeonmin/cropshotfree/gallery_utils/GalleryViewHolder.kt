package ko.hyeonmin.cropshotfree.gallery_utils

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import ko.hyeonmin.cropshotfree.R

/**
 * Created by junse on 2017-09-26.
 */
class GalleryViewHolder: RecyclerView.ViewHolder {

    var viewHolderRl: RelativeLayout? = null
    var iv: ImageView? = null
    var selectOnOff: ImageView? = null

    constructor(item: View) : super(item) {
        viewHolderRl = item.findViewById(R.id.viewHolder) as RelativeLayout
        iv = item.findViewById(R.id.galleryItemImage) as ImageView
        selectOnOff = item.findViewById(R.id.selectOnOff) as ImageView
    }
}