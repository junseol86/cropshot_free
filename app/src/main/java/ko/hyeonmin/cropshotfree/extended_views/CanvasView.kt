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

    var leftShadeRect: Rect = Rect(0, 0, 0, 0)
    val leftShadePaint = Paint()
    var topShadeRect: Rect = Rect(0, 0, 0, 0)
    val topShadePaint = Paint()
    var rightShadeRect: Rect = Rect(0, 0, 0, 0)
    val rightShadePaint = Paint()
    var bottomShadeRect: Rect = Rect(0, 0, 0, 0)
    val bottomShadePaint = Paint()

    var leftDarkerShadeRect: Rect = Rect(0, 0, 0, 0)
    val leftDarkerShadePaint = Paint()
    var topDarkerShadeRect: Rect = Rect(0, 0, 0, 0)
    val topDarkerShadePaint = Paint()
    var rightDarkerShadeRect: Rect = Rect(0, 0, 0, 0)
    val rightDarkerShadePaint = Paint()
    var bottomDarkerShadeRect: Rect = Rect(0, 0, 0, 0)
    val bottomDarkerShadePaint = Paint()

    var takenWidth = 0
    var takenHeight = 0

    // fromX, fromY, toX, toY
    var sizeShrinkInit = 0
    var sizeShrink = 0
    var fromToXY = hashMapOf(0 to 0f, 1 to 0f, 2 to 0f, 3 to 0f)
    var maxMinXY = hashMapOf(0 to 0f, 1 to 0f, 2 to 0f, 3 to 0f)
    var touching = false

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.activity = context as CropShotActivity
        this.setOnTouchListener(this)

        sizePaint.style = Paint.Style.STROKE
        sizePaint.strokeWidth = 4f
        sizePaint.color = Color.WHITE
        takenPaint.style = Paint.Style.STROKE
        takenPaint.strokeWidth = 4f
        takenPaint.color = Color.WHITE

        leftShadePaint.color = Color.argb(128, 0, 0, 0)
        leftShadePaint.style = Paint.Style.FILL
        topShadePaint.color = Color.argb(128, 0, 0, 0)
        topShadePaint.style = Paint.Style.FILL
        rightShadePaint.color = Color.argb(128, 0, 0, 0)
        rightShadePaint.style = Paint.Style.FILL
        bottomShadePaint.color = Color.argb(128, 0, 0, 0)
        bottomShadePaint.style = Paint.Style.FILL

        leftDarkerShadePaint.color = Color.argb(192, 0, 0, 0)
        leftDarkerShadePaint.style = Paint.Style.FILL
        topDarkerShadePaint.color = Color.argb(192, 0, 0, 0)
        topDarkerShadePaint.style = Paint.Style.FILL
        rightDarkerShadePaint.color = Color.argb(192, 0, 0, 0)
        rightDarkerShadePaint.style = Paint.Style.FILL
        bottomDarkerShadePaint.color = Color.argb(192, 0, 0, 0)
        bottomDarkerShadePaint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (sizeShrinkInit == 0) {
            sizeShrinkInit = width/10
            sizeShrink = width/10
        }

        when (activity?.console?.crop_mode) {
            activity?.console?.CROP_DIRECT_MODE -> {

                if (touching) {
                    leftShadeRect.set(0, maxMinXY[1]!!.toInt(), maxMinXY[0]!!.toInt(), maxMinXY[3]!!.toInt())
                    topShadeRect.set(0, 0, width, maxMinXY[1]!!.toInt())
                    rightShadeRect.set(maxMinXY[2]!!.toInt(), maxMinXY[1]!!.toInt(), width, maxMinXY[3]!!.toInt())
                    bottomShadeRect.set(0, maxMinXY[3]!!.toInt(), width, height)
                    canvas?.drawRect(leftShadeRect, leftShadePaint)
                    canvas?.drawRect(topShadeRect, topShadePaint)
                    canvas?.drawRect(rightShadeRect, rightShadePaint)
                    canvas?.drawRect(bottomShadeRect, bottomShadePaint)

                    takenRect.set(maxMinXY[0]!!.toInt(), maxMinXY[1]!!.toInt(), maxMinXY[2]!!.toInt(), maxMinXY[3]!!.toInt())
                    canvas?.drawRect(takenRect, takenPaint)
                }
            }
            activity?.console?.CROP_RATIO_MODE -> {

                leftDarkerShadeRect.set(0, sizeShrink, sizeShrink, height - sizeShrink)
                topDarkerShadeRect.set(0, 0, width, sizeShrink)
                rightDarkerShadeRect.set(width - sizeShrink, sizeShrink, width, height - sizeShrink)
                bottomDarkerShadeRect.set(0, height - sizeShrink, width, height)
                canvas?.drawRect(leftDarkerShadeRect, leftDarkerShadePaint)
                canvas?.drawRect(topDarkerShadeRect, topDarkerShadePaint)
                canvas?.drawRect(rightDarkerShadeRect, rightDarkerShadePaint)
                canvas?.drawRect(bottomDarkerShadeRect, bottomDarkerShadePaint)

                if (touching && (Math.abs(fromToXY[0]!! - fromToXY[2]!!) > 10 && Math.abs(fromToXY[1]!! - fromToXY[3]!!) > 10)) {


                    if (Math.abs((fromToXY[0]!! - fromToXY[2]!!) / (fromToXY[1]!! - fromToXY[3]!!)) > (width - 2 * sizeShrink).toFloat() / (height - 2 * sizeShrink).toFloat()) {
                        topShadeRect.set(sizeShrink, sizeShrink, width - sizeShrink, (height - takenHeight) / 2)
                        bottomShadeRect.set(sizeShrink, (height + takenHeight) / 2, width - sizeShrink, height - sizeShrink)
                        canvas?.drawRect(topShadeRect, topShadePaint)
                        canvas?.drawRect(bottomShadeRect, bottomShadePaint)

                        takenHeight = (Math.abs(fromToXY[1]!! - fromToXY[3]!!) * (width - 2f * sizeShrink) / Math.abs(fromToXY[0]!! - fromToXY[2]!!)).toInt()
                        takenRect.set(sizeShrink, (height - takenHeight) / 2, width - sizeShrink, (height + takenHeight) / 2)
                    } else {
                        leftShadeRect.set(sizeShrink, sizeShrink, (width - takenWidth) / 2, height - sizeShrink)
                        rightShadeRect.set((width + takenWidth) / 2, sizeShrink, width - sizeShrink, height - sizeShrink)
                        canvas?.drawRect(leftShadeRect, leftShadePaint)
                        canvas?.drawRect(rightShadeRect, rightShadePaint)

                        takenWidth = (Math.abs(fromToXY[0]!! - fromToXY[2]!!) * (height - 2f * sizeShrink) / Math.abs(fromToXY[1]!! - fromToXY[3]!!)).toInt()
                        takenRect.set((width - takenWidth) / 2, sizeShrink, (width + takenWidth) / 2, height - sizeShrink)
                    }
                    canvas?.drawRect(takenRect, takenPaint)
                }
            }
        }
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                activity?.console?.active = false
                activity?.panel?.active = false
                fromToXY[0] = event.x
                fromToXY[1] = event.y

                maxMinXY[0] = event.x
                maxMinXY[1] = event.y
                maxMinXY[2] = event.x
                maxMinXY[3] = event.y

                touching = true
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                fromToXY[2] = event.x
                fromToXY[3] = event.y

                maxMinXY[0] = Math.min(maxMinXY[0]!!, event.x)
                maxMinXY[1] = Math.min(maxMinXY[1]!!, event.y)
                maxMinXY[2] = Math.max(maxMinXY[2]!!, event.x)
                maxMinXY[3] = Math.max(maxMinXY[3]!!, event.y)

                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                activity?.console?.active = true
                activity?.panel?.active = true
                touching = false
                invalidate()
            }
        }
        return true
    }

}
