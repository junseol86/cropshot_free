package ko.hyeonmin.cropshotfree.uitls.camera

import android.hardware.camera2.CameraDevice

/**
 * Created by junse on 2017-10-08.
 */
class CameraDeviceStateCallback(val cameraAPI: CameraAPI): CameraDevice.StateCallback() {
    override fun onOpened(camera: CameraDevice?) {
        cameraAPI.mCameraDevice = camera
        cameraAPI.createCameraPreviewSession()
    }

    override fun onDisconnected(camera: CameraDevice?) {
        camera?.close()
        cameraAPI.mCameraDevice = null
    }

    override fun onError(camera: CameraDevice?, error: Int) {
        camera?.close()
        cameraAPI.mCameraDevice = null
    }
}