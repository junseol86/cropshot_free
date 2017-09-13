package ko.hyeonmin.cropshotfree.extended_views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import ko.hyeonmin.cropshotfree.R
import android.graphics.DashPathEffect



/**
 * Created by junse on 2017-09-13.
 */
class GetPermissionButton: View, View.OnTouchListener {

    private var button: RectF? = null
    private var buttonPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var buttonPressed = false

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.setOnTouchListener(this)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (button == null) button = RectF(4f, 4f, width.toFloat() - 4, height.toFloat() - 4)
        buttonPaint.style = Paint.Style.STROKE
        buttonPaint.color = Color.WHITE
        buttonPaint.strokeWidth = if (buttonPressed) 4f else 2f
        buttonPaint.pathEffect = DashPathEffect(floatArrayOf(24f, 16f), 1f)
        canvas?.drawRoundRect(button!!, (Math.min(width, height) - 8)/2f, (Math.min(width, height) - 8)/2f, buttonPaint)

        textPaint.color = Color.WHITE
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = 52f
        canvas?.drawText(context.resources.getText(R.string.activate_camera).toString(), width/2f, height/2f + 18, textPaint)

    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> buttonPressed = true
            MotionEvent.ACTION_UP -> buttonPressed = false
        }
        invalidate()
        return false
    }

}
