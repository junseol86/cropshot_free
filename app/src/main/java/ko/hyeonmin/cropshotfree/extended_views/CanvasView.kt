package ko.hyeonmin.cropshotfree.extended_views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import ko.hyeonmin.cropshotfree.R
import ko.hyeonmin.cropshotfree.activities.CropShotActivity

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
    private var outerRect: Rect = Rect(0, 0, 0, 0)
    private val outerPaint = Paint()


    // fromX, fromY, toX, toY
    var sizeShrink = 0
    var fromToXY = hashMapOf(0 to 0f, 1 to 0f, 2 to 0f, 3 to 0f)
    var cropXY = hashMapOf(0 to 0f, 1 to 0f, 2 to 0f, 3 to 0f)

    var squeezeModeOuterXY = hashMapOf(0 to 0, 1 to 0, 2 to 0, 3 to 0)
    var squeezeModeHorizontal = true

    var ratioModeOuterXY = hashMapOf(0 to 0, 1 to 0, 2 to 0, 3 to 0)
    var ratioW = 0.0
    var ratioH = 0.0
    var moveDistance = 0.0
    var cropRatioW = 0
    var cropRatioH = 0

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

        outerPaint.style = Paint.Style.STROKE
        outerPaint.strokeWidth = 1f
        outerPaint.color = Color.WHITE

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
            activity?.console?.CROP_SQUEEZE_MODE -> {

                leftDarkerShadeRect.set(0, squeezeModeOuterXY[1]!!, squeezeModeOuterXY[0]!!, squeezeModeOuterXY[3]!!)
                topDarkerShadeRect.set(0, 0, width, squeezeModeOuterXY[1]!!)
                rightDarkerShadeRect.set(squeezeModeOuterXY[2]!!, squeezeModeOuterXY[1]!!, width, squeezeModeOuterXY[3]!!)
                bottomDarkerShadeRect.set(0, squeezeModeOuterXY[3]!!, width, height)
                canvas?.drawRect(leftDarkerShadeRect, leftDarkerShadePaint)
                canvas?.drawRect(topDarkerShadeRect, topDarkerShadePaint)
                canvas?.drawRect(rightDarkerShadeRect, rightDarkerShadePaint)
                canvas?.drawRect(bottomDarkerShadeRect, bottomDarkerShadePaint)

                outerRect.set(squeezeModeOuterXY[0]!!, squeezeModeOuterXY[1]!!, squeezeModeOuterXY[2]!!, squeezeModeOuterXY[3]!!)
                canvas?.drawRect(outerRect, outerPaint)

                if (touching) {
                    if (squeezeModeHorizontal) {
                        leftShadeRect.set(squeezeModeOuterXY[0]!!, squeezeModeOuterXY[1]!!, cropXY[0]!!.toInt(), squeezeModeOuterXY[3]!!)
                        rightShadeRect.set(cropXY[2]!!.toInt(), squeezeModeOuterXY[1]!!, squeezeModeOuterXY[2]!!, squeezeModeOuterXY[3]!!)
                        canvas?.drawRect(leftShadeRect, leftShadePaint)
                        canvas?.drawRect(rightShadeRect, rightShadePaint)
                    } else {
                        topShadeRect.set(squeezeModeOuterXY[0]!!, squeezeModeOuterXY[1]!!, squeezeModeOuterXY[2]!!, cropXY[1]!!.toInt())
                        bottomShadeRect.set(squeezeModeOuterXY[0]!!, cropXY[3]!!.toInt(), squeezeModeOuterXY[2]!!, squeezeModeOuterXY[3]!!)
                        canvas?.drawRect(topShadeRect, topShadePaint)
                        canvas?.drawRect(bottomShadeRect, bottomShadePaint)
                    }
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

                outerRect.set(ratioModeOuterXY[0]!!, ratioModeOuterXY[1]!!, ratioModeOuterXY[2]!!, ratioModeOuterXY[3]!!)
                canvas?.drawRect(outerRect, outerPaint)

                if (touching) {
                    topShadeRect.set(ratioModeOuterXY[0]!!, ratioModeOuterXY[1]!!, ratioModeOuterXY[2]!!, cropXY[1]!!.toInt())
                    bottomShadeRect.set(ratioModeOuterXY[0]!!, cropXY[3]!!.toInt(), ratioModeOuterXY[2]!!, ratioModeOuterXY[3]!!)
                    leftShadeRect.set(ratioModeOuterXY[0]!!, cropXY[1]!!.toInt(), cropXY[0]!!.toInt(), cropXY[3]!!.toInt())
                    rightShadeRect.set(cropXY[2]!!.toInt(), cropXY[1]!!.toInt(), ratioModeOuterXY[2]!!, cropXY[3]!!.toInt())
                    canvas?.drawRect(topShadeRect, topShadePaint)
                    canvas?.drawRect(bottomShadeRect, bottomShadePaint)
                    canvas?.drawRect(leftShadeRect, leftShadePaint)
                    canvas?.drawRect(rightShadeRect, rightShadePaint)
                }
            }
        }

        if (touching) {
            cropRect.set(cropXY[0]!!.toInt(), cropXY[1]!!.toInt(), cropXY[2]!!.toInt(), cropXY[3]!!.toInt())
            canvas?.drawRect(cropRect, cropPaint)
        }

    }

    fun setSqueezeModeShade() {
        sizeShrink = ((width / 2 - 10) * (1 - activity!!.console!!.sizerView!!.size)).toInt()
        squeezeModeOuterXY[0] = sizeShrink
        squeezeModeOuterXY[1] = sizeShrink
        squeezeModeOuterXY[2] = width - sizeShrink
        squeezeModeOuterXY[3] = height - sizeShrink
        invalidate()
    }
    
    fun setRatioModeShade() {
        ratioW = if (activity!!.console!!.ratioWidthEt!!.text.toString().trim() == "") 1.0 else activity!!.console!!.ratioWidthEt!!.text!!.toString().toDouble()
        if (ratioW == 0.0) ratioW = 1.0
        ratioH = if (activity!!.console!!.ratioHeightEt!!.text.toString().trim() == "") 1.0 else activity!!.console!!.ratioHeightEt!!.text!!.toString().toDouble()
        if (ratioH == 0.0) ratioH = 1.0

        if (ratioW / ratioH > width.toDouble() / height.toDouble()) {
            ratioModeOuterXY[0] = 0
            ratioModeOuterXY[1] = ((height - (width * ratioH / ratioW)) / 2).toInt()
            ratioModeOuterXY[2] = width
            ratioModeOuterXY[3] = (ratioModeOuterXY[1]!! + width * ratioH / ratioW).toInt()
        } else {
            ratioModeOuterXY[0] = ((width - (height * ratioW / ratioH)) / 2).toInt()
            ratioModeOuterXY[1] = 0
            ratioModeOuterXY[2] = (ratioModeOuterXY[0]!! + height * ratioW / ratioH).toInt()
            ratioModeOuterXY[3] = height
        }
        invalidate()
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {

                if (activity!!.moveFinger!!.visibility == View.VISIBLE) {
                    activity?.moveFinger?.clearAnimation()
                    activity?.moveFinger?.visibility = View.GONE
                }

                activity?.console?.active = false
                activity?.panel?.active = false

                fromToXY[0] = event.x
                fromToXY[1] = event.y
                fromToXY[2] = event.x
                fromToXY[3] = event.y

                when (activity?.console?.crop_mode) {
                    activity?.console?.CROP_DIRECT_MODE -> {
                        if (!activity!!.console!!.centered) {
                            cropXY[0] = event.x
                            cropXY[1] = event.y
                            cropXY[2] = event.x
                            cropXY[3] = event.y
                        }
                    }
                    activity?.console?.CROP_RATIO_MODE -> {
                        cropXY[0] = ratioModeOuterXY[0]!!.toFloat()
                        cropXY[1] = ratioModeOuterXY[1]!!.toFloat()
                        cropXY[2] = ratioModeOuterXY[2]!!.toFloat()
                        cropXY[3] = ratioModeOuterXY[3]!!.toFloat()
                    }
                }
                touching = true
            }
            MotionEvent.ACTION_MOVE -> {

                when (activity?.console?.crop_mode) {
                    activity?.console?.CROP_DIRECT_MODE -> {
                        if (activity!!.console!!.centered) {
                            fromToXY[2] = event.x
                            fromToXY[3] = event.y

                            cropXY[0] = Math.max(0f, (width - Math.abs(fromToXY[0]!! - fromToXY[2]!!) * 2f) / 2)
                            cropXY[1] = Math.max(0f, (height - Math.abs(fromToXY[1]!! - fromToXY[3]!!) * 2f) / 2)
                            cropXY[2] = Math.min(width.toFloat(), width - (width - Math.abs(fromToXY[0]!! - fromToXY[2]!!) * 2f) / 2)
                            cropXY[3] = Math.min(height.toFloat(), height - (height - Math.abs(fromToXY[1]!! - fromToXY[3]!!) * 2f) / 2)
                        } else {
                            cropXY[0] = Math.max(0f, Math.min(cropXY[0]!!, event.x))
                            cropXY[1] = Math.max(0f, Math.min(cropXY[1]!!, event.y))
                            cropXY[2] = Math.min(width.toFloat(), Math.max(cropXY[2]!!, event.x))
                            cropXY[3] = Math.min(height.toFloat(), Math.max(cropXY[3]!!, event.y))
                        }
                    }
                    activity?.console?.CROP_SQUEEZE_MODE -> {

                        fromToXY[2] = if (event.x < 0) 0f else if (event.x > width) width.toFloat() else event.x
                        fromToXY[3] = if (event.y < 0) 0f else if (event.y > height) height.toFloat() else event.y

                        if (Math.abs(fromToXY[0]!! - fromToXY[2]!!) > Math.abs(fromToXY[1]!! - fromToXY[3]!!)) {
                            squeezeModeHorizontal = true
                            cropXY[0] = Math.min(squeezeModeOuterXY[0]!! + Math.abs((fromToXY[0]!! - fromToXY[2]!!)), width / 2f - 10)
                            cropXY[1] = squeezeModeOuterXY[1]!!.toFloat()
                            cropXY[2] = Math.max(squeezeModeOuterXY[2]!! - Math.abs((fromToXY[0]!! - fromToXY[2]!!)), width / 2f + 10)
                            cropXY[3] = squeezeModeOuterXY[3]!!.toFloat()
                        } else {
                            squeezeModeHorizontal = false
                            cropXY[0] = squeezeModeOuterXY[0]!!.toFloat()
                            cropXY[1] = Math.min(squeezeModeOuterXY[1]!! + Math.abs((fromToXY[1]!! - fromToXY[3]!!)), height / 2f - 10)
                            cropXY[2] = squeezeModeOuterXY[2]!!.toFloat()
                            cropXY[3] = Math.max(squeezeModeOuterXY[3]!! - Math.abs((fromToXY[1]!! - fromToXY[3]!!)), height / 2f + 10)
                        }
                    }
                    activity?.console?.CROP_RATIO_MODE -> {
                        moveDistance = Math.pow((Math.pow((fromToXY[0]!! - event.x).toDouble(), 2.0) + Math.pow((fromToXY[1]!! - event.y).toDouble(), 2.0)), 0.5)
                        if (Math.abs(ratioModeOuterXY[0]!! - ratioModeOuterXY[2]!!) > Math.abs(ratioModeOuterXY[1]!! - ratioModeOuterXY[3]!!)) {
                            ratioW = Math.abs(ratioModeOuterXY[0]!! - ratioModeOuterXY[2]!!) - moveDistance
                            ratioH = ratioW * Math.abs(ratioModeOuterXY[1]!! - ratioModeOuterXY[3]!!) / Math.abs(ratioModeOuterXY[0]!! - ratioModeOuterXY[2]!!)
                        } else {
                            ratioH = Math.abs(ratioModeOuterXY[1]!! - ratioModeOuterXY[3]!!) - moveDistance
                            ratioW = ratioH * Math.abs(ratioModeOuterXY[0]!! - ratioModeOuterXY[2]!!) / Math.abs(ratioModeOuterXY[1]!! - ratioModeOuterXY[3]!!)
                        }
                        if (ratioW > 20 && ratioH > 20) {
                            cropXY[0] = (ratioModeOuterXY[0]!! + (Math.abs(ratioModeOuterXY[0]!! - ratioModeOuterXY[2]!!) - ratioW) / 2f).toFloat()
                            cropXY[1] = (ratioModeOuterXY[1]!! + (Math.abs(ratioModeOuterXY[1]!! - ratioModeOuterXY[3]!!) - ratioH) / 2f).toFloat()
                            cropXY[2] = (ratioModeOuterXY[2]!! - (Math.abs(ratioModeOuterXY[0]!! - ratioModeOuterXY[2]!!) - ratioW) / 2f).toFloat()
                            cropXY[3] = (ratioModeOuterXY[3]!! - (Math.abs(ratioModeOuterXY[1]!! - ratioModeOuterXY[3]!!) - ratioH) / 2f).toFloat()
                        }
                    }
                }
                invalidate()

                activity?.mTouchFocus?.focusRequest(hashMapOf(0 to cropXY[0]!!.toInt(), 1 to cropXY[1]!!.toInt(), 2 to cropXY[2]!!.toInt(), 3 to cropXY[3]!!.toInt()))
            }
            MotionEvent.ACTION_UP -> {
                activity?.console?.active = true
                activity?.panel?.active = true
                touching = false
                invalidate()

                if (Math.abs(cropXY[0]!! - cropXY[2]!!) < 20 && Math.abs(cropXY[1]!! - cropXY[3]!!) < 20) {
                    activity?.screenCaptor?.takeScreenShot(cropXY, false)
                } else {
                    if (Math.abs(cropXY[0]!! - cropXY[2]!!) < 2 || Math.abs(cropXY[1]!! - cropXY[3]!!) < 2) {
                        Toast.makeText(activity, activity!!.resources.getString(R.string.ratio_not_available), Toast.LENGTH_SHORT).show()
                    } else {
                        activity?.screenCaptor?.takeScreenShot(cropXY, true)
                    }
                }
            }
        }
        return true
    }

}
