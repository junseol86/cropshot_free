package ko.hyeonmin.cropshotfree.uitls

import android.app.Activity
import android.support.constraint.ConstraintLayout
import android.widget.ImageView
import ko.hyeonmin.cropshotfree.CropShotActivity
import ko.hyeonmin.cropshotfree.R
import ko.hyeonmin.cropshotfree.extended_views.SizerView

/**
 * Created by Hyeonmin on 2017-09-10.
 */
class Console(activity: Activity) {

    val CROP_DIRECT_MODE = 0
    val CROP_RATIO_MODE = 1

    var activity = activity as CropShotActivity
    private var consoleCl: ConstraintLayout? = null
    private  var cropDirectButton: ImageView? = null
    private  var cropRatioButton: ImageView? = null
    var sizerView: SizerView? = null

    var active = true
    var crop_mode = 0

    init {
        consoleCl = activity.findViewById(R.id.console) as ConstraintLayout
        cropDirectButton = activity.findViewById(R.id.crop_direct_button) as ImageView
        cropDirectButton?.setOnClickListener{setCropMode(CROP_DIRECT_MODE)}
        cropRatioButton = activity.findViewById(R.id.crop_ratio_button) as ImageView
        cropRatioButton?.setOnClickListener{
            setCropMode(CROP_RATIO_MODE)
            this.activity.canvasView?.setRatioModeShade()
        }
        sizerView = activity?.findViewById(R.id.sizerView) as SizerView
    }

    private fun setCropMode(mode: Int):Boolean {
        this.crop_mode = mode
        cropDirectButton?.alpha = if (crop_mode == CROP_DIRECT_MODE) 1f else 0.2f
        cropRatioButton?.alpha = if (crop_mode == CROP_RATIO_MODE) 1f else 0.2f
        activity.canvasView?.invalidate()
        return true
    }

}