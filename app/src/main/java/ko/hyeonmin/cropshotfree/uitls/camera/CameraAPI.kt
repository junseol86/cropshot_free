package ko.hyeonmin.cropshotfree.uitls.camera

import android.app.ProgressDialog
import android.util.Size
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.view.Surface
import java.util.*
import android.view.View
import android.view.animation.AnimationUtils
import ko.hyeonmin.cropshotfree.R
import ko.hyeonmin.cropshotfree.activities.CropShotActivity
import android.os.Handler
import android.os.HandlerThread
import android.util.SparseIntArray


/**
 * Created by Hyeonmin on 2017-09-05.
 */

//http://metalkin.tistory.com/92 참조

class CameraAPI(activity: CropShotActivity) {

    var activity: CropShotActivity? = activity

    val START_CAMERA_APP = 0
    val STATE_PREVIEW = 0
    val STATE_WAIT_LOCK = 1
    var mState = START_CAMERA_APP

    var mPreviewSize: Size? = null

    var mCameraDevice: CameraDevice? = null
    var mCharacteristics: CameraCharacteristics? = null
    var mCameraId: String? = null

    var mCameraCaptureSession: CameraCaptureSession? = null
    var mCameraCaptureSessionCallback: CameraCaptureSession.CaptureCallback = CaptureCallback(this)

    var mPreviewCaptureRequest: CaptureRequest? = null
    var mPreviewCaptureRequestBuilder: CaptureRequest.Builder? = null

    var mBackgroundThread: HandlerThread? = null
    var mBackgroundHandler: Handler? = null

    val photoTaker = PhotoTaker(this)
    var progressDialog: ProgressDialog? = null

    val ORIENTATIONS = SparseIntArray()

    init {
        activity.canvasView?.visibility = View.VISIBLE
        progressDialog = ProgressDialog(activity)
        progressDialog?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog?.setMessage(activity.resources.getString(R.string.saving_image))
        ORIENTATIONS.append(Surface.ROTATION_0, 90)
        ORIENTATIONS.append(Surface.ROTATION_90, 0)
        ORIENTATIONS.append(Surface.ROTATION_180, 270)
        ORIENTATIONS.append(Surface.ROTATION_270, 180)
    }

    fun setupCamera(width: Int, height: Int) {
        val cameraManager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        try {
            for (cameraId in cameraManager.cameraIdList) {
                mCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
                if (mCharacteristics?.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue
                }

                val map = mCharacteristics?.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

                val imageSizes = ArrayList<Size>()
                map!!.getOutputSizes(ImageFormat.JPEG).map { imageSizes.add(it) }

                val largestImageSize = Collections.max(
                        imageSizes,
                        { lhs, rhs ->
                            when {
                                lhs.width * lhs.height - rhs.width * rhs.height < 0 -> -1
                                lhs.width * lhs.height - rhs.width * rhs.height == 0 -> 0
                                else -> 1
                            }
                        }) as Size

                photoTaker.mImageReader = ImageReader.newInstance(largestImageSize.width, largestImageSize.height,
                        ImageFormat.JPEG, 1)
                photoTaker.mImageReader?.setOnImageAvailableListener(photoTaker.mOnImageAvailableListener, mBackgroundHandler)

                mPreviewSize = getPreferredPreviewSize(map.getOutputSizes(SurfaceTexture::class.java), width, height)
                mCameraId = cameraId
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    fun getPreferredPreviewSize(mapSizes: Array<Size>, width: Int, height: Int): Size {
        var collectorSizes = ArrayList<Size>()
        mapSizes.map {
            if (width > height) {
                if (it.width > width && it.height > height)
                    collectorSizes.add(it)
            } else {
                if (it.width > height && it.height > width)
                    collectorSizes.add(it)
            }
        }
        return if (collectorSizes.size > 0) {
            Collections.max(
                    collectorSizes
            ) { lhs, rhs ->
                when {
                    lhs.width * lhs.height - rhs.width * rhs.height < 0 -> -1
                    lhs.width * lhs.height - rhs.width * rhs.height == 0 -> 0
                    else -> 1
                }
            }
        } else mapSizes[0]
    }

    fun openCamera() {
        var cameraManager = activity!!.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraManager.openCamera(mCameraId, CameraDeviceStateCallback(this), null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun createCameraPreviewSession() {
        try {
            val texture = activity?.textureView?.surfaceTexture
            texture?.setDefaultBufferSize(mPreviewSize!!.width, mPreviewSize!!.height)
            val previewSurface = Surface(texture)

            mPreviewCaptureRequestBuilder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            mPreviewCaptureRequestBuilder!!.addTarget(previewSurface)
            mCameraDevice!!.createCaptureSession(Arrays.asList(previewSurface, photoTaker.mImageReader!!.surface), object: CameraCaptureSession.StateCallback() {
                override fun onConfigureFailed(session: CameraCaptureSession?) {
                }

                override fun onConfigured(session: CameraCaptureSession?) {
                    if (mCameraDevice == null) {
                        return
                    }
                    try {
                        mPreviewCaptureRequest = mPreviewCaptureRequestBuilder!!.build()
                        mCameraCaptureSession = session
                        mCameraCaptureSession?.setRepeatingRequest(
                                mPreviewCaptureRequest,
                                mCameraCaptureSessionCallback,
                                mBackgroundHandler
                        )
                    } catch (e: CameraAccessException) {
                        e.printStackTrace()
                    }
                }
            }, null)

            transformImage(activity!!.textureView!!.width, activity!!.textureView!!.height)

            if (activity!!.firstTime) {
                activity?.moveFinger?.visibility = View.VISIBLE
                activity?.moveFinger?.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.blink))
            }

        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun openBackgroundThread() {
        mBackgroundThread = HandlerThread("Camera2 background thread")
        mBackgroundThread?.start()
        mBackgroundHandler = Handler(mBackgroundThread!!.looper)
    }

    fun closeBackgroundThread() {
        mBackgroundThread?.quitSafely()
        try {
            mBackgroundThread?.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun setFocus(onOff: Boolean) {
        try {
            if (onOff) photoTaker.setPhotoTakingAndSpinner(true)
            mState = if (onOff) STATE_WAIT_LOCK else STATE_PREVIEW
            mPreviewCaptureRequestBuilder?.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    if (onOff) CaptureRequest.CONTROL_AF_TRIGGER_START else CaptureRequest.CONTROL_AF_TRIGGER_CANCEL)
            mPreviewCaptureRequestBuilder?.setTag("TAKE")
            mCameraCaptureSession?.capture(mPreviewCaptureRequestBuilder!!.build(),
                    mCameraCaptureSessionCallback, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            if (onOff) photoTaker.setPhotoTakingAndSpinner(false)
            e.printStackTrace()
        }
    }

    // 카메라 화면의 이미지 비율을 여기서 맞춰준다
    fun transformImage(textureWidth: Int, textureHeight: Int) {
        if (mPreviewSize == null || activity!!.textureView == null) {
            return
        }
        val matrix = Matrix()
        val textureRectF = RectF(0f, 0f, textureWidth.toFloat(), textureHeight.toFloat())
        val previewRectF = RectF(0f, 0f, mPreviewSize!!.height.toFloat(), mPreviewSize!!.width.toFloat())

        previewRectF.offset(textureRectF.centerX() - previewRectF.centerX(),
                textureRectF.centerY() - previewRectF.centerY())
        matrix.setRectToRect(textureRectF, previewRectF, Matrix.ScaleToFit.FILL)
        val scale = Math.max(textureWidth.toFloat() / mPreviewSize!!.width, textureHeight.toFloat() / mPreviewSize!!.height)
        matrix.postScale(scale, scale, textureRectF.centerX(), textureRectF.centerY())

        activity?.textureView?.setTransform(matrix)
    }

    // 다시 살펴볼 것
    fun captureStillImage() {
        try {
            val captureStillBuilder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureStillBuilder.addTarget(photoTaker.mImageReader!!.surface)
            captureStillBuilder.set(CaptureRequest.NOISE_REDUCTION_MODE, CaptureRequest.NOISE_REDUCTION_MODE_FAST)
            captureStillBuilder.set(CaptureRequest.EDGE_MODE, CaptureRequest.EDGE_MODE_FAST)

            val rotation = activity!!.windowManager.defaultDisplay.rotation
            captureStillBuilder.set(CaptureRequest.JPEG_ORIENTATION,
                    ORIENTATIONS.get(rotation))

            val captureCallback = object: CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(session: CameraCaptureSession?, request: CaptureRequest?, result: TotalCaptureResult?) {
                    setFocus(false)
                }
            }
            mCameraCaptureSession?.capture(
                    captureStillBuilder.build(), captureCallback, null
            )

        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun closeCamera() {
        if (null != mCameraCaptureSession) {
            mCameraCaptureSession?.close()
            mCameraCaptureSession = null
        }
        if (null != mCameraDevice) {
            mCameraDevice?.close()
            mCameraDevice = null
        }
        if (photoTaker.mImageReader != null) {
            photoTaker.mImageReader?.close()
            photoTaker.mImageReader = null
        }
    }

}
