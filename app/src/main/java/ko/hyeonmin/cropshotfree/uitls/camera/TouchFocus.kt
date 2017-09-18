package ko.hyeonmin.cropshotfree.uitls.camera

import android.graphics.Rect
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.params.MeteringRectangle
import android.view.View

/**
 * Created by junse on 2017-09-17.
 */
class TouchFocus(cameraAPI: CameraAPI) {

    private val cameraApi = cameraAPI
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
            sensorArraySize = cameraApi.mCharacteristics?.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)
//            반대로 되는 것이 맞다
            sensorW = sensorArraySize!!.height()
            sensorH = sensorArraySize!!.width()

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

        cameraApi.mCaptureSession?.stopRepeating()
        cameraApi.mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL)
        cameraApi.mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF)
        cameraApi.mCaptureSession?.capture(cameraApi.mPreviewRequestBuilder!!.build(), cameraApi.mCaptureCallback, null)

        if (cameraApi.mCharacteristics!!.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF) >= 1) {
            cameraApi.mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AF_REGIONS, arrayOf(focusXY))
        }

        cameraApi.mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        cameraApi.mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO)
        cameraApi.mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START)
        cameraApi.mPreviewRequestBuilder?.setTag("FOCUS")
        cameraApi.mCaptureSession?.capture(cameraApi.mPreviewRequestBuilder!!.build(), cameraApi.mCaptureCallback, null)
        sendingFocusRequest = true
    }

}