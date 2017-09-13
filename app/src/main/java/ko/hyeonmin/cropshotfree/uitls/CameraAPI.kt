package ko.hyeonmin.cropshotfree.uitls

import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraCaptureSession
import android.util.Size
import android.hardware.camera2.CameraManager
import android.app.Activity
import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.view.Surface
import java.util.*
import android.hardware.camera2.TotalCaptureResult
import android.hardware.camera2.CaptureResult
import android.view.View
import ko.hyeonmin.cropshotfree.CropShotActivity


/**
 * Created by Hyeonmin on 2017-09-05.
 */

//http://metalkin.tistory.com/92 참조

class CameraAPI(activity: CropShotActivity) {

    private var activity: CropShotActivity? = activity

    private var mCameraSize: Size? = null

    private var mCaptureSession: CameraCaptureSession? = null
    private var mCameraDevice: CameraDevice? = null
    private var mPreviewRequestBuilder: CaptureRequest.Builder? = null

    init {
        activity.canvasView?.visibility = View.VISIBLE
    }

    fun CameraManager(activity: Activity): CameraManager {
        return activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    fun CameraCharacteristics(cameraManager: CameraManager): String? {
        try {
            for (cameraId in cameraManager.cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                if (characteristics.get(CameraCharacteristics.LENS_FACING) === CameraCharacteristics.LENS_FACING_BACK) {
                    val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    val sizes = map!!.getOutputSizes(SurfaceTexture::class.java)

                    mCameraSize = sizes[0]
                    sizes
                        .asSequence()
                        .filter { it.width > mCameraSize!!.width }
                        .forEach { mCameraSize = it }
                    return cameraId
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        return null
    }

    private val mCameraDeviceStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            mCameraDevice = camera
            onCameraDeviceOpened()
        }

        override fun onDisconnected(camera: CameraDevice) {
            camera.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            camera.close()
        }
    }

    fun CameraDevice(cameraManager: CameraManager, cameraId: String) {
        try {
            cameraManager.openCamera(cameraId, mCameraDeviceStateCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private val mCaptureSessionCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
            try {
                mCaptureSession = cameraCaptureSession
                mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                cameraCaptureSession.setRepeatingRequest(mPreviewRequestBuilder!!.build(), mCaptureCallback, null)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
        override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
        }
    }

    private fun CaptureSession(cameraDevice: CameraDevice, surface: Surface) {
        try {
            cameraDevice.createCaptureSession(Collections.singletonList(surface), mCaptureSessionCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun CaptureRequest(cameraDevice: CameraDevice, surface: Surface) {
        try {
            mPreviewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            mPreviewRequestBuilder?.addTarget(surface)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private val mCaptureCallback = object : CameraCaptureSession.CaptureCallback() {
        override fun onCaptureProgressed(session: CameraCaptureSession, request: CaptureRequest, partialResult: CaptureResult) {
            super.onCaptureProgressed(session, request, partialResult)
        }
        override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
            super.onCaptureCompleted(session, request, result)
        }
    }

    fun onCameraDeviceOpened() {
        val texture = activity?.textureView?.surfaceTexture
        texture?.setDefaultBufferSize(mCameraSize!!.height, mCameraSize!!.width)
        val surface = Surface(texture)

        CaptureSession(mCameraDevice!!, surface)
        CaptureRequest(mCameraDevice!!, surface)
    }

    fun closeCamera() {
        if (null != mCaptureSession) {
            mCaptureSession?.close()
            mCaptureSession = null
        }
        if (null != mCameraDevice) {
            mCameraDevice?.close()
            mCameraDevice = null
        }
    }

}
