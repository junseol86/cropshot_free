package ko.hyeonmin.cropshotfree.recycler_parts

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import ko.hyeonmin.cropshotfree.R

/**
 * Created by junse on 2017-09-26.
 */
class GalleryViewHolder: RecyclerView.ViewHolder {

    var iv: ImageView? = null

    constructor(item: View) : super(item) {
        iv = item.findViewById(R.id.gallery_item_image) as ImageView
    }
}