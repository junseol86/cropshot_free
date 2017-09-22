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
    private val cropRect: Rect = Rect(0, 0, 0, 0)
    private val sizePaint = Paint()
    private val cropPaint = Paint()

    private var leftShadeRect: Rect = Rect(0, 0, 0, 0)
    private val leftShadePaint = Paint()
    private var topShadeRect: Rect = Rect(0, 0, 0, 0)
    private val topShadePaint = Paint()
    private var rightShadeRect: Rect = Rect(0, 0, 0, 0)
    private val rightShadePaint = Paint()
    private var bottomShadeRect: Rect = Rect(0, 0, 0, 0)
    private val bottomShadePaint = Paint()

    private var leftDarkerShadeRect: Rect = Rect(0, 0, 0, 0)
    private val leftDarkerShadePaint = Paint()
    private var topDarkerShadeRect: Rect = Rect(0, 0, 0, 0)
    private val topDarkerShadePaint = Paint()
    private var rightDarkerShadeRect: Rect = Rect(0, 0, 0, 0)
    private val rightDarkerShadePaint = Paint()
    private var bottomDarkerShadeRect: Rect = Rect(0, 0, 0, 0)
    private val bottomDarkerShadePaint = Paint()
    private var ratioOuterRect: Rect = Rect(0, 0, 0, 0)
    private val ratioOuterPaint = Paint()


    // fromX, fromY, toX, toY
    var sizeShrink = 0
    var fromToXY = hashMapOf(0 to 0f, 1 to 0f, 2 to 0f, 3 to 0f)
    var cropXY = hashMapOf(0 to 0f, 1 to 0f, 2 to 0f, 3 to 0f)
    var ratioModeOuterXY = hashMapOf(0 to 0, 1 to 0, 2 to 0, 3 to 0)
    var ratioModeHorizontal = true
    var touching = false

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.activity = context as CropShotActivity
        this.setOnTouchListener(this)

        sizePaint.style = Paint.Style.STROKE
        sizePaint.strokeWidth = 4f
        sizePaint.color = Color.WHITE
        cropPaint.style = Paint.Style.STROKE
        cropPaint.strokeWidth = 4f
        cropPaint.color = Color.WHITE

        ratioOuterPaint.style = Paint.Style.STROKE
        ratioOuterPaint.strokeWidth = 1f
        ratioOuterPaint.color = Color.WHITE

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

        when (activity?.console?.crop_mode) {
            activity?.console?.CROP_DIRECT_MODE -> {

                if (touching) {
                    leftShadeRect.set(0, cropXY[1]!!.toInt(), cropXY[0]!!.toInt(), cropXY[3]!!.toInt())
                    topShadeRect.set(0, 0, width, cropXY[1]!!.toInt())
                    rightShadeRect.set(cropXY[2]!!.toInt(), cropXY[1]!!.toInt(), width, cropXY[3]!!.toInt())
                    bottomShadeRect.set(0, cropXY[3]!!.toInt(), width, height)
                    canvas?.drawRect(leftShadeRect, leftShadePaint)
                    canvas?.drawRect(topShadeRect, topShadePaint)
                    canvas?.drawRect(rightShadeRect, rightShadePaint)
                    canvas?.drawRect(bottomShadeRect, bottomShadePaint)

                }
            }
            activity?.console?.CROP_RATIO_MODE -> {

                leftDarkerShadeRect.set(0, ratioModeOuterXY[1]!!, ratioModeOuterXY[0]!!, ratioModeOuterXY[3]!!)
                topDarkerShadeRect.set(0, 0, width, ratioModeOuterXY[1]!!)
                rightDarkerShadeRect.set(ratioModeOuterXY[2]!!, ratioModeOuterXY[1]!!, width, ratioModeOuterXY[3]!!)
                bottomDarkerShadeRect.set(0, ratioModeOuterXY[3]!!, width, height)
                canvas?.drawRect(leftDarkerShadeRect, leftDarkerShadePaint)
                canvas?.drawRect(topDarkerShadeRect, topDarkerShadePaint)
                canvas?.drawRect(rightDarkerShadeRect, rightDarkerShadePaint)
                canvas?.drawRect(bottomDarkerShadeRect, bottomDarkerShadePaint)

                ratioOuterRect.set(ratioModeOuterXY[0]!!, ratioModeOuterXY[1]!!, ratioModeOuterXY[2]!!, ratioModeOuterXY[3]!!)
                canvas?.drawRect(ratioOuterRect, ratioOuterPaint)

                if (touching) {
                    if (ratioModeHorizontal) {
                        leftShadeRect.set(ratioModeOuterXY[0]!!, ratioModeOuterXY[1]!!, cropXY[0]!!.toInt(), ratioModeOuterXY[3]!!)
                        rightShadeRect.set(cropXY[2]!!.toInt(), ratioModeOuterXY[1]!!, ratioModeOuterXY[2]!!, ratioModeOuterXY[3]!!)
                        canvas?.drawRect(leftShadeRect, leftShadePaint)
                        canvas?.drawRect(rightShadeRect, rightShadePaint)
                    } else {
                        topShadeRect.set(ratioModeOuterXY[0]!!, ratioModeOuterXY[1]!!, ratioModeOuterXY[2]!!, cropXY[1]!!.toInt())
                        bottomShadeRect.set(ratioModeOuterXY[0]!!, cropXY[3]!!.toInt(), ratioModeOuterXY[2]!!, ratioModeOuterXY[3]!!)
                        canvas?.drawRect(topShadeRect, topShadePaint)
                        canvas?.drawRect(bottomShadeRect, bottomShadePaint)
                    }
                }
            }
        }

        if (touching) {
            cropRect.set(cropXY[0]!!.toInt(), cropXY[1]!!.toInt(), cropXY[2]!!.toInt(), cropXY[3]!!.toInt())
            canvas?.drawRect(cropRect, cropPaint)
        }

    }

    fun setRatioModeShade() {
        sizeShrink = ((width / 2 - 10) * (1 - activity!!.console!!.sizerView!!.size)).toInt()
        ratioModeOuterXY[0] = sizeShrink
        ratioModeOuterXY[1] = sizeShrink
        ratioModeOuterXY[2] = width - sizeShrink
        ratioModeOuterXY[3] = height - sizeShrink
        invalidate()
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                activity?.console?.active = false
                activity?.panel?.active = false

                fromToXY[0] = event.x
                fromToXY[1] = event.y
                fromToXY[2] = event.x
                fromToXY[3] = event.y

                when (activity?.console?.crop_mode) {
                    activity?.console?.CROP_DIRECT_MODE -> {
                        cropXY[0] = event.x
                        cropXY[1] = event.y
                        cropXY[2] = event.x
                        cropXY[3] = event.y
                    }
                }
                touching = true
            }
            MotionEvent.ACTION_MOVE -> {

                when (activity?.console?.crop_mode) {
                    activity?.console?.CROP_DIRECT_MODE -> {
                        cropXY[0] = Math.max(0f, Math.min(cropXY[0]!!, event.x))
                        cropXY[1] = Math.max(0f, Math.min(cropXY[1]!!, event.y))
                        cropXY[2] = Math.min(width.toFloat(), Math.max(cropXY[2]!!, event.x))
                        cropXY[3] = Math.min(height.toFloat(), Math.max(cropXY[3]!!, event.y))
                    }
                    activity?.console?.CROP_RATIO_MODE -> {

                        fromToXY[2] = if (event.x < 0) 0f else if (event.x > width) width.toFloat() else event.x
                        fromToXY[3] = if (event.y < 0) 0f else if (event.y > height) height.toFloat() else event.y

                        if (Math.abs(fromToXY[0]!! - fromToXY[2]!!) > Math.abs(fromToXY[1]!! - fromToXY[3]!!)) {
                            ratioModeHorizontal = true
                            cropXY[0] = Math.min(ratioModeOuterXY[0]!! + Math.abs((fromToXY[0]!! - fromToXY[2]!!)), width / 2f - 10)
                            cropXY[1] = ratioModeOuterXY[1]!!.toFloat()
                            cropXY[2] = Math.max(ratioModeOuterXY[2]!! - Math.abs((fromToXY[0]!! - fromToXY[2]!!)), width / 2f + 10)
                            cropXY[3] = ratioModeOuterXY[3]!!.toFloat()
                        } else {
                            ratioModeHorizontal = false
                            cropXY[0] = ratioModeOuterXY[0]!!.toFloat()
                            cropXY[1] = Math.min(ratioModeOuterXY[1]!! + Math.abs((fromToXY[1]!! - fromToXY[3]!!)), height / 2f - 10)
                            cropXY[2] = ratioModeOuterXY[2]!!.toFloat()
                            cropXY[3] = Math.max(ratioModeOuterXY[3]!! - Math.abs((fromToXY[1]!! - fromToXY[3]!!)), height / 2f + 10)
                        }
                    }
                }
                invalidate()

                activity?.mTouchFocus?.focusRequest(hashMapOf(0 to cropXY[0]!!.toInt(), 1 to cropXY[1]!!.toInt(), 2 to cropXY[2]!!.toInt(), 3 to cropXY[3]!!.toInt()), this)
            }
            MotionEvent.ACTION_UP -> {
                activity?.console?.active = true
                activity?.panel?.active = true
                touching = false
                invalidate()

                if (Math.abs(fromToXY[0]!! - event.x) < 20 || Math.abs(fromToXY[1]!! - event.y) < 20) {
                    activity?.screenCaptor?.takeScreenShot(cropXY, false)
                } else {
                    activity?.screenCaptor?.takeScreenShot(cropXY, true)
                }
            }
        }
        return true
    }

}
