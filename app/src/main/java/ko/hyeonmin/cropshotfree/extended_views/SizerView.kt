package ko.hyeonmin.cropshotfree.extended_views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import ko.hyeonmin.cropshotfree.activities.CropShotActivity

/**
 * Created by Hyeonmin on 2017-09-10.
 */
class SizerView : View, View.OnTouchListener {

    var size = 0.8f
    var activity: CropShotActivity? = null

    private var wholeLine: RectF? = null
    private var wholeLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var sizeLine: RectF? = null
    private var sizeLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private  var handlePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var barHeight = 0
    private var moveRange = 0
    private var sizeLineLength = 0f

    private var dragging = false

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.activity = context as CropShotActivity
        this.setOnTouchListener(this)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (wholeLine == null) {
            barHeight = height
            moveRange = width - height
            sizeLineLength = moveRange * size
            wholeLine = RectF(height / 2 - 4f, height / 2f - 4, moveRange + height / 2f + 4, height / 2f + 4)
            wholeLinePaint.style = Paint.Style.FILL
            wholeLinePaint.color = Color.rgb(64, 64, 64)
        }
        canvas?.drawRoundRect(wholeLine, 4f, 4f, wholeLinePaint)

        sizeLine = RectF(height / 2 - 4f, height / 2f - 4, sizeLineLength + height / 2f + 4, height / 2f + 4)
        sizeLinePaint.style = Paint.Style.FILL
        sizeLinePaint.color = Color.rgb(192, 192, 192)
        canvas?.drawRoundRect(sizeLine, 4f, 4f, sizeLinePaint)

        handlePaint.style = Paint.Style.FILL
        handlePaint.color = Color.WHITE
        canvas?.drawCircle(height / 2 + moveRange * size, height / 2f, 32f, handlePaint)
    }


    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        if (activity!!.console!!.crop_mode != activity!!.console!!.CROP_SQUEEZE_MODE) return true

        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.x in sizeLineLength .. sizeLineLength + height) {
                    dragging = true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (dragging) {
                    sizeLineLength = event.x - height / 2
                    if (sizeLineLength < 0) sizeLineLength = 0f
                    if (sizeLineLength > moveRange ) sizeLineLength = moveRange.toFloat()
                    size = sizeLineLength / moveRange
                    invalidate()
                    activity?.canvasView?.setSqueezeModeShade()
                }
            }
            MotionEvent.ACTION_UP -> {
                dragging = false

            }
        }
        return true
    }

}