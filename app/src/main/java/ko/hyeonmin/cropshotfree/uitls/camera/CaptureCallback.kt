package ko.hyeonmin.cropshotfree.uitls.camera

import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CaptureResult
import android.hardware.camera2.TotalCaptureResult

/**
 * Created by junse on 2017-09-16.
 */
open class CaptureCallback(val cameraApi: CameraAPI): CameraCaptureSession.CaptureCallback() {

    override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
        super.onCaptureCompleted(session, request, result)
        process(request, result)

    }

    fun process(request: CaptureRequest, result: CaptureResult) {
        when (request.tag) {
            "FOCUS" -> {
                cameraApi.activity?.mTouchFocus?.sendingFocusRequest = false
                cameraApi.mPreviewCaptureRequestBuilder?.set(CaptureRequest.CONTROL_AF_TRIGGER, null)
                cameraApi.mCameraCaptureSession?.setRepeatingRequest(cameraApi.mPreviewCaptureRequestBuilder!!.build(), null, null)
            }
            "TAKE" -> {
                when (cameraApi.mState) {
                    cameraApi.STATE_WAIT_LOCK -> {
                        cameraApi.captureStillImage()
//                        val afState = result.get(CaptureResult.CONTROL_AF_STATE)
//                        if (afState == CaptureRequest.CONTROL_AF_STATE_FOCUSED_LOCKED) {
//                            cameraApi.captureStillImage()
//                        }
                    }
                }
            }
        }
    }

}