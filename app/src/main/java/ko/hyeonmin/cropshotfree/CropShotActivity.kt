package ko.hyeonmin.cropshotfree

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.TextureView
import android.view.Window
import ko.hyeonmin.cropshotfree.uitls.camera.CameraAPI
import android.view.View
import ko.hyeonmin.cropshotfree.extended_views.CanvasView
import ko.hyeonmin.cropshotfree.extended_views.GetPermissionButton
import ko.hyeonmin.cropshotfree.listeners.*
import ko.hyeonmin.cropshotfree.uitls.*

class CropShotActivity : Activity() {

    var cameraApi: CameraAPI? = null
    var textureView: TextureView? = null
    var console: Console? = null
    var panel: Panel? = null
    var canvasView: CanvasView? = null
    var screenCaptor = ScreenCaptor(this)

    var getPermissionButton: GetPermissionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_crop_shot)

        textureView = findViewById(R.id.textureView) as TextureView
        textureView?.surfaceTextureListener = MySurfaceTextureListener(this)
        console = Console(this)
        panel = Panel(this)
        canvasView = findViewById(R.id.canvasView) as CanvasView

        getPermissionButton = findViewById(R.id.getPermissionButton) as GetPermissionButton
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
            }
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                getPermissionButton?.visibility= View.VISIBLE
                getPermissionButton?.setOnClickListener {
                    requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 0)
                }
            } else {
                cameraApi = CameraAPI(this)
            }
        } else {
            cameraApi = CameraAPI(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            cameraApi = CameraAPI(this)
            getPermissionButton?.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        if (textureView!!.isAvailable) {
            openCamera()
        } else {
            textureView?.surfaceTextureListener = MySurfaceTextureListener(this)
        }
    }

    override fun onPause() {
        if (cameraApi != null) {
            closeCamera()
        }
        super.onPause()
    }

    fun openCamera() {
        val cameraManager = cameraApi?.CameraManager()
        val cameraId = cameraApi?.CameraIdFromCharacteristics(cameraManager!!)
        cameraApi?.CameraDevice(cameraManager!!, cameraId!!)
    }

    fun closeCamera() {
        cameraApi?.closeCamera()
    }
}
