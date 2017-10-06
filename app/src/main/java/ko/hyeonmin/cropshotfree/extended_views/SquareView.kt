package ko.hyeonmin.cropshotfree.extended_views

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

/**
 * Created by junse on 2017-10-06.
 */
class SquareView: RelativeLayout {
    constructor(context: Context): super(context)
    constructor(context: Context, attr: AttributeSet): super(context, attr)
    constructor(context: Context, attr: AttributeSet, defStyle: Int): super(context, attr, defStyle)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}