package ko.hyeonmin.cropshotfree.uitls.camera

import android.graphics.Rect
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.params.MeteringRectangle
import android.view.View
import ko.hyeonmin.cropshotfree.CropShotActivity

/**
 * Created by junse on 2017-09-17.
 */
class TouchFocus(activity: CropShotActivity) {

    var activity = activity
    var sensorArraySize: Rect? = null
    var sensorW = 0
    var sensorH = 0
    var availableXY = hashMapOf(0 to 0, 1 to 0, 2 to 0, 3 to 0)
    private var focusXY: MeteringRectangle? = null
    private var lastCropXY = hashMapOf(0 to 0, 1 to 0, 2 to 0, 3 to 0)
    var sendingFocusRequest = false

    private val SENSIBILITY = 15

    fun focusRequest(cropXY: HashMap<Int, Int>, view: View) {
        if (
        !(Math.pow(Math.pow((cropXY[0]!! - lastCropXY[0]!!).toDouble(), 2.0) + Math.pow((cropXY[1]!! - lastCropXY[1]!!).toDouble(), 2.0), 0.5) > SENSIBILITY ||
            Math.pow(Math.pow((cropXY[2]!! - lastCropXY[2]!!).toDouble(), 2.0) + Math.pow((cropXY[3]!! - lastCropXY[3]!!).toDouble(), 2.0), 0.5) > SENSIBILITY)
                ) return

        lastCropXY = cropXY

        if (sendingFocusRequest) return

        if (sensorArraySize == null) {
            sensorArraySize = activity.cameraApi!!.mCharacteristics?.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)
//            반대로 되는 것이 맞다
            sensorW = Math.min(sensorArraySize!!.width(), sensorArraySize!!.height())
            sensorH = Math.max(sensorArraySize!!.width(), sensorArraySize!!.height())

            if (view.width.toFloat() / view.height.toFloat() > sensorW.toFloat() / sensorH.toFloat()) {
                availableXY[0] = 0
                availableXY[2] = sensorW
                val availableHeight = view.height * sensorW / view.width
                availableXY[1] = (sensorH - availableHeight) / 2
                availableXY[3] = availableXY[1]!! + availableHeight
            } else {
                availableXY[1] = 0
                availableXY[3] = sensorH
                val availableWidth = view.width * sensorH / view.height
                availableXY[0] = (sensorW - availableWidth) / 2
                availableXY[2] = availableXY[0]!! + availableWidth
            }
        }

        focusXY = MeteringRectangle(availableXY[0]!! + cropXY[0]!!, availableXY[1]!! + cropXY[1]!!, availableXY[2]!! + cropXY[2]!!, availableXY[3]!! + cropXY[3]!!, MeteringRectangle.METERING_WEIGHT_MAX - 1)

        activity.cameraApi!!.mCaptureSession?.stopRepeating()
        activity.cameraApi!!.mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL)
        activity.cameraApi!!.mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF)
        activity.cameraApi!!.mCaptureSession?.capture(activity.cameraApi!!.mPreviewRequestBuilder!!.build(), activity.cameraApi!!.mCaptureCallback, null)

        if (activity.cameraApi!!.mCharacteristics!!.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF) >= 1) {
            activity.cameraApi!!.mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AF_REGIONS, arrayOf(focusXY))
        }

        activity.cameraApi!!.mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        activity.cameraApi!!.mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO)
        activity.cameraApi!!.mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START)
        activity.cameraApi!!.mPreviewRequestBuilder?.setTag("FOCUS")
        activity.cameraApi!!.mCaptureSession?.capture(activity.cameraApi!!.mPreviewRequestBuilder!!.build(), activity.cameraApi!!.mCaptureCallback, null)
        sendingFocusRequest = true
    }

}