package ko.hyeonmin.cropshotfree.uitls

import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.view.MotionEvent
import android.widget.TextView
import ko.hyeonmin.cropshotfree.CropShotActivity
import ko.hyeonmin.cropshotfree.R
import java.io.File

/**
 * Created by Hyeonmin on 2017-09-10.
 */
class Panel(activity: CropShotActivity) {
    var panelCl: ConstraintLayout? = null
    var active = true

    var folderNameTV: TextView? = null

    init {
        this.panelCl = activity.findViewById(R.id.panel) as ConstraintLayout

        folderNameTV = activity.findViewById(R.id.folder_name) as TextView
        folderNameTV?.text = activity.caches!!.folderName

        panelCl?.setOnTouchListener { _, event ->
            when (event!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    panelCl?.setBackgroundColor(Color.parseColor("#333333"))
                }
                MotionEvent.ACTION_UP -> {
                    panelCl?.setBackgroundColor(Color.parseColor("#111111"))
                }
            }
            false
        }
    }
}