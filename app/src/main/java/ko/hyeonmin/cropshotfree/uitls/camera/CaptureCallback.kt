package ko.hyeonmin.cropshotfree.uitls.camera

import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CaptureResult
import android.hardware.camera2.TotalCaptureResult

/**
 * Created by junse on 2017-09-16.
 */
class CaptureCallback(cameraApi: CameraAPI): CameraCaptureSession.CaptureCallback() {

    val cameraApi = cameraApi

    override fun onCaptureProgressed(session: CameraCaptureSession, request: CaptureRequest, partialResult: CaptureResult) {
        super.onCaptureProgressed(session, request, partialResult)
    }
    override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
        super.onCaptureCompleted(session, request, result)
        cameraApi.mTouchFocus.sendingFocusRequest = false

        if (request.tag == "FOCUS") {
            cameraApi.mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AF_TRIGGER, null)
            cameraApi.mCaptureSession?.setRepeatingRequest(cameraApi.mPreviewRequestBuilder!!.build(), null, null)
        }

    }

}