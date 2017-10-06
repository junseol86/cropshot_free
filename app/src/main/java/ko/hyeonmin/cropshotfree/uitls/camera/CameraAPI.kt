package ko.hyeonmin.cropshotfree.uitls.camera

import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraAccessException
import android.util.Size
import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.view.Surface
import java.util.*
import android.view.View
import android.view.animation.AnimationUtils
import ko.hyeonmin.cropshotfree.R
import ko.hyeonmin.cropshotfree.activities.CropShotActivity


/**
 * Created by Hyeonmin on 2017-09-05.
 */

//http://metalkin.tistory.com/92 참조

class CameraAPI(activity: CropShotActivity) {

    var activity: CropShotActivity? = activity

    var mPreviewSize: Size? = null

    var mCharacteristics: CameraCharacteristics? = null
    var mCaptureSession: CameraCaptureSession? = null
    var mCameraDevice: CameraDevice? = null
    var mPreviewRequestBuilder: CaptureRequest.Builder? = null
    var mCaptureCallback: CaptureCallback = CaptureCallback(this)

    init {
        activity.canvasView?.visibility = View.VISIBLE
    }

    fun CameraManager(): CameraManager {
        return activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    fun CameraIdFromCharacteristics(cameraManager: CameraManager, width: Int, height: Int): String? {
        try {
            for (cameraId in cameraManager.cameraIdList) {
                mCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
                if (mCharacteristics?.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                    val map = mCharacteristics?.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    val sizes = map!!.getOutputSizes(SurfaceTexture::class.java)

                    mPreviewSize = sizes[0]

                    var collectorSizes = ArrayList<Size>()
                    sizes.map {
                        if (width > height) {
                            if (it.width > width && it.height > height)
                                collectorSizes.add(it)
                        } else {
                            if (it.width > height && it.height > width)
                                collectorSizes.add(it)
                        }
                    }

                    mPreviewSize = if (collectorSizes.size > 0) {
                        Collections.min(collectorSizes, object: Comparator<Size> {
                            override fun compare(lhs: Size, rhs: Size): Int {
                                return when {
                                    lhs.width * lhs.height - rhs.width * rhs.height < 0 -> -1
                                    lhs.width * lhs.height - rhs.width * rhs.height == 0 -> 0
                                    else -> 1
                                }
                            }
                        })
                    } else sizes[0]

//                    sizes
//                            .asSequence()
//                            .filter { it.width > mPreviewSize!!.width }
//                            .forEach { mPreviewSize = it }
//
//                    val ratioW = activity!!.canvasView!!.height
//                    val ratioH = activity!!.canvasView!!.width
//                    mPreviewSize = if (ratioW / ratioH.toFloat() < mPreviewSize!!.width / mPreviewSize!!.height.toFloat()) {
//                        Size(mPreviewSize!!.width, (mPreviewSize!!.width * ratioH.toFloat() / ratioW.toFloat()).toInt())
//                    } else {
//                        Size((mPreviewSize!!.height * ratioW.toFloat() / ratioH.toFloat()).toInt(), mPreviewSize!!.height)
//                    }

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

    private fun setCaptureSession(cameraDevice: CameraDevice, surface: Surface) {
        try {
            cameraDevice.createCaptureSession(Collections.singletonList(surface), mCaptureSessionCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun setCaptureRequest(cameraDevice: CameraDevice, surface: Surface) {
        try {
            mPreviewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            mPreviewRequestBuilder?.addTarget(surface)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun onCameraDeviceOpened() {
        val texture = activity?.textureView?.surfaceTexture

        texture?.setDefaultBufferSize(mPreviewSize!!.width, mPreviewSize!!.height)
//        texture?.setDefaultBufferSize(activity!!.canvasView!!.height, activity!!.canvasView!!.width)

        val surface = Surface(texture)

        setCaptureSession(mCameraDevice!!, surface)
        setCaptureRequest(mCameraDevice!!, surface)

        transformImage(activity!!.textureView!!.width, activity!!.textureView!!.height)

        if (activity!!.firstTime) {
            activity?.moveFinger?.visibility = View.VISIBLE
            activity?.moveFinger?.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.blink))
        }
    }

    fun transformImage(width: Int, height: Int) {
        if (mPreviewSize == null || activity!!.textureView == null) {
            return
        }
        var matrix = Matrix()
        var rotation = activity!!.windowManager.defaultDisplay.rotation
        var textureRectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
        var previewRectF = RectF(0f, 0f, mPreviewSize!!.height.toFloat(), mPreviewSize!!.width.toFloat())
        var centerX = textureRectF.centerX()
        var centerY = textureRectF.centerY()

        previewRectF.offset(centerX - previewRectF.centerX(),
                centerY - previewRectF.centerY())
        matrix.setRectToRect(textureRectF, previewRectF, Matrix.ScaleToFit.FILL)
        var scale = Math.max(width.toFloat() / mPreviewSize!!.width, height.toFloat() / mPreviewSize!!.height)
        matrix.postScale(scale, scale, centerX, centerY)
        matrix.postRotate(90f * (rotation), centerX, centerY)

        activity?.textureView?.setTransform(matrix)
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
