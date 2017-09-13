package ko.hyeonmin.cropshotfree.extended_views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import ko.hyeonmin.cropshotfree.CropShotActivity

/**
 * Created by Hyeonmin on 2017-09-06.
 */

class CanvasView: View, View.OnTouchListener {

    var activity: CropShotActivity? = null
    var sizeRect: Rect = Rect(0, 0, 0, 0)
    val takenRect: Rect = Rect(0, 0, 0, 0)
    val sizePaint = Paint()
    val takenPaint = Paint()

    var takenWidth = 0
    var takenHeight = 0

    // fromX, fromY, toX, toY
    var sizeShrink = 40
    var fromToXY = hashMapOf(0 to 0f, 1 to 0f, 2 to 0f, 3 to 0f)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.activity = context as CropShotActivity
        this.setOnTouchListener(this)

        sizePaint.style = Paint.Style.STROKE
        sizePaint.strokeWidth = 4f
        sizePaint.color = Color.WHITE
        takenPaint.style = Paint.Style.STROKE
        takenPaint.strokeWidth = 4f
        takenPaint.color = Color.YELLOW
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        sizeRect.set(sizeShrink, sizeShrink, width - sizeShrink, height - sizeShrink)
        canvas?.drawRect(sizeRect, sizePaint)
        if (Math.abs(fromToXY[0]!! - fromToXY[2]!!) > 10 && Math.abs(fromToXY[1]!! - fromToXY[3]!!) > 10) {

            if (Math.abs((fromToXY[0]!! - fromToXY[2]!!) / (fromToXY[1]!! - fromToXY[3]!!)) > (width - 2 * sizeShrink).toFloat() / (height - 2 * sizeShrink).toFloat()) {
                takenHeight = (Math.abs(fromToXY[1]!! - fromToXY[3]!!) * (width - 2f * sizeShrink) / Math.abs(fromToXY[0]!! - fromToXY[2]!!)).toInt()
                takenRect.set(sizeShrink, (height - takenHeight) / 2, width - sizeShrink, (height + takenHeight) / 2)
            } else {
                takenWidth = (Math.abs(fromToXY[0]!! - fromToXY[2]!!) * (height - 2f * sizeShrink) / Math.abs(fromToXY[1]!! - fromToXY[3]!!)).toInt()
                takenRect.set((width - takenWidth) / 2, sizeShrink, (width + takenWidth) / 2, height - sizeShrink)
            }
            println(fromToXY)
//            canvas?.drawColor(Color.TRANSPARENT)
            canvas?.drawRect(takenRect, takenPaint)
        }
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                activity?.console?.active = false
                activity?.panel?.active = false
                fromToXY[0] = event.x
                fromToXY[1] = event.y

//                activity?.openCamera()
            }
            MotionEvent.ACTION_MOVE -> {
                fromToXY[2] = event.x
                fromToXY[3] = event.y
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                activity?.console?.active = true
                activity?.panel?.active = true

//                activity?.closeCamera()
            }
        }
        return true
    }

}
