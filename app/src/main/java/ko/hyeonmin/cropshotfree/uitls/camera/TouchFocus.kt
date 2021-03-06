package ko.hyeonmin.cropshotfree.uitls.camera

import android.graphics.Rect
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.params.MeteringRectangle
import ko.hyeonmin.cropshotfree.activities.CropShotActivity

/**
 * Created by junse on 2017-09-17.
 */
class TouchFocus(activity: CropShotActivity) {

    var activity = activity

    var sizeRatio = 0f
    var offsetX = 0
    var offsetY = 0

    private var focusXY: MeteringRectangle? = null
    private var lastCropXY = hashMapOf(0 to 0, 1 to 0, 2 to 0, 3 to 0)

    var sendingFocusRequest = false

    private val SENSIBILITY = 15

    fun focusRequest(cropXY: HashMap<Int, Int>) {
        if (
        !(Math.pow(Math.pow((cropXY[0]!! - lastCropXY[0]!!).toDouble(), 2.0) + Math.pow((cropXY[1]!! - lastCropXY[1]!!).toDouble(), 2.0), 0.5) > SENSIBILITY ||
            Math.pow(Math.pow((cropXY[2]!! - lastCropXY[2]!!).toDouble(), 2.0) + Math.pow((cropXY[3]!! - lastCropXY[3]!!).toDouble(), 2.0), 0.5) > SENSIBILITY)
                ) return

        lastCropXY = cropXY

        if (sendingFocusRequest) return

        if (activity.cameraApi!!.mPreviewSize!!.width / activity.cameraApi!!.mPreviewSize!!.height.toFloat() > activity.canvasView!!.width / activity.canvasView!!.height.toFloat()) {
            sizeRatio = activity.cameraApi!!.mPreviewSize!!.height / activity.canvasView!!.height.toFloat()
            offsetX = ((activity.cameraApi!!.mPreviewSize!!.width - activity.canvasView!!.width * sizeRatio) / 2).toInt()
        } else {
            sizeRatio = activity.cameraApi!!.mPreviewSize!!.width / activity.canvasView!!.width.toFloat()
            offsetY = ((activity.cameraApi!!.mPreviewSize!!.height - activity.canvasView!!.height * sizeRatio) / 2).toInt()
        }

        focusXY = MeteringRectangle((offsetX + cropXY[0]!! * sizeRatio).toInt(), (offsetY + cropXY[1]!! * sizeRatio).toInt(),
                (Math.abs(cropXY[0]!! - cropXY[2]!!) * sizeRatio).toInt(), (Math.abs(cropXY[1]!! - cropXY[3]!!) * sizeRatio).toInt(), MeteringRectangle.METERING_WEIGHT_MAX - 1)

        activity.cameraApi!!.mCameraCaptureSession?.stopRepeating()
        activity.cameraApi!!.mPreviewCaptureRequestBuilder?.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL)
        activity.cameraApi!!.mPreviewCaptureRequestBuilder?.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF)
        activity.cameraApi!!.mCameraCaptureSession?.capture(activity.cameraApi!!.mPreviewCaptureRequestBuilder!!.build(), activity.cameraApi!!.mCameraCaptureSessionCallback, null)

        if (activity.cameraApi!!.mCharacteristics!!.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF) >= 1) {
            activity.cameraApi!!.mPreviewCaptureRequestBuilder?.set(CaptureRequest.CONTROL_AF_REGIONS, arrayOf(focusXY))
        }

        activity.cameraApi!!.mPreviewCaptureRequestBuilder?.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        activity.cameraApi!!.mPreviewCaptureRequestBuilder?.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO)
        activity.cameraApi!!.mPreviewCaptureRequestBuilder?.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START)
        activity.cameraApi!!.mPreviewCaptureRequestBuilder?.setTag("FOCUS")
        activity.cameraApi!!.mCameraCaptureSession?.capture(activity.cameraApi!!.mPreviewCaptureRequestBuilder!!.build(), activity.cameraApi!!.mCameraCaptureSessionCallback, null)
        sendingFocusRequest = true
    }

}