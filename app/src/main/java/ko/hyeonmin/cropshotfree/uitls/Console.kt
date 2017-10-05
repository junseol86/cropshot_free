package ko.hyeonmin.cropshotfree.uitls

import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import ko.hyeonmin.cropshotfree.activities.CropShotActivity
import ko.hyeonmin.cropshotfree.R
import ko.hyeonmin.cropshotfree.extended_views.SizerView

/**
 * Created by Hyeonmin on 2017-09-10.
 */
class Console(val activity: CropShotActivity) {

    val CROP_DIRECT_MODE = 0
    val CROP_SQUEEZE_MODE = 1
    val CROP_RATIO_MODE = 2

    var consoleCl: ConstraintLayout? = null
    private  var cropDirectButton: ImageView? = null
    private  var cropSqueezeButton: ImageView? = null
    private  var cropRatioButton: ImageView? = null

    var directCenterBtn: ConstraintLayout? = null
    var directCenterImg: ImageView? = null

    var sizerView: SizerView? = null

    var ratioConsole: ConstraintLayout? = null
    var ratioWidthEt: EditText? = null
    var ratioHeightEt: EditText? = null
    var ratioExchangeBtn: ImageView? = null

    var active = true
    var crop_mode = 0
    var centered = true

    init {
        consoleCl = activity.findViewById(R.id.console) as ConstraintLayout
        cropDirectButton = activity.findViewById(R.id.crop_direct_button) as ImageView
        cropDirectButton?.setOnClickListener{setCropMode(CROP_DIRECT_MODE)}
        cropSqueezeButton = activity.findViewById(R.id.crop_squeeze_button) as ImageView
        cropSqueezeButton?.setOnClickListener{
            setCropMode(CROP_SQUEEZE_MODE)
            activity.canvasView?.setSqueezeModeShade()
        }
        cropRatioButton = activity.findViewById(R.id.crop_ratio_button) as ImageView
        cropRatioButton?.setOnClickListener{
            setCropMode(CROP_RATIO_MODE)
            activity.canvasView?.setRatioModeShade()
        }

        directCenterBtn = activity.findViewById(R.id.cropDirectCenterBtn) as ConstraintLayout
        directCenterImg = activity.findViewById(R.id.cropDirectCenterImg) as ImageView
        directCenterBtn?.setOnTouchListener({_, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> directCenterBtn?.setBackgroundColor(Color.parseColor("#222222"))
                MotionEvent.ACTION_UP -> {
                    directCenterBtn?.setBackgroundColor(Color.parseColor("#111111"))
                    centered = !centered
                    directCenterImg?.setImageResource(if (centered) R.drawable.direct_centered_on else R.drawable.direct_centered_off)
                }
            }
            false
        })

        sizerView = activity.findViewById(R.id.sizerView) as SizerView

        ratioConsole = activity.findViewById(R.id.ratioConsole) as ConstraintLayout
        ratioWidthEt = activity.findViewById(R.id.ratioWidth) as EditText
        ratioWidthEt?.addTextChangedListener(object:TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                activity.canvasView?.setRatioModeShade()
            }
        })
        ratioHeightEt = activity.findViewById(R.id.ratioHeight) as EditText
        ratioHeightEt?.addTextChangedListener(object:TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                activity.canvasView?.setRatioModeShade()
            }
        })
        ratioExchangeBtn = activity.findViewById(R.id.ratioExchange) as ImageView
        ratioExchangeBtn?.setOnClickListener {
            val tmp = ratioWidthEt!!.text
            ratioWidthEt?.text = ratioHeightEt!!.text
            ratioHeightEt?.text = tmp
            activity.canvasView?.setRatioModeShade()
        }
    }

    private fun setCropMode(mode: Int):Boolean {
        this.crop_mode = mode
        cropDirectButton?.alpha = if (crop_mode == CROP_DIRECT_MODE) 1f else 0.2f
        cropSqueezeButton?.alpha = if (crop_mode == CROP_SQUEEZE_MODE) 1f else 0.2f
        cropRatioButton?.alpha = if (crop_mode == CROP_RATIO_MODE) 1f else 0.2f

        directCenterBtn?.visibility = if (crop_mode == CROP_DIRECT_MODE) View.VISIBLE else View.GONE
        sizerView?.visibility = if (crop_mode == CROP_SQUEEZE_MODE) View.VISIBLE else View.GONE
        ratioConsole?.visibility = if (crop_mode == CROP_RATIO_MODE) View.VISIBLE else View.GONE

        activity.canvasView?.invalidate()
        return true
    }

}