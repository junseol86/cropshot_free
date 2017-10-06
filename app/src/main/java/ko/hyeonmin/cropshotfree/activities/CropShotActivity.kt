package ko.hyeonmin.cropshotfree.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.TextureView
import android.view.View
import android.view.Window
import android.widget.ImageView
import ko.hyeonmin.cropshotfree.uitls.camera.CameraAPI
import ko.hyeonmin.cropshotfree.R
import ko.hyeonmin.cropshotfree.extended_views.CanvasView
import ko.hyeonmin.cropshotfree.listeners.*
import ko.hyeonmin.cropshotfree.uitls.*
import ko.hyeonmin.cropshotfree.uitls.camera.TouchFocus

class CropShotActivity : Activity() {

    var caches: Caches? = null

    var cameraApi: CameraAPI? = null
    var textureView: TextureView? = null
    var console: Console? = null
    var panel: Panel? = null
    var canvasView: CanvasView? = null
    var screenCaptor = ScreenCaptor(this)

    var mTouchFocus: TouchFocus? = null

    var cropshotCl: ConstraintLayout? = null

    var moveFinger: ImageView? = null

    var firstTime = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_crop_shot)

        cropshotCl = findViewById(R.id.cropshotCl) as ConstraintLayout
        moveFinger = findViewById(R.id.moveFinger) as ImageView

        VersionCheck(this).sendVersionCheckRequest()

        caches = Caches(this)

        textureView = findViewById(R.id.textureView) as TextureView
        textureView?.surfaceTextureListener = MySurfaceTextureListener(this)
        console = Console(this)
        panel = Panel(this)
        canvasView = findViewById(R.id.canvasView) as CanvasView
        mTouchFocus = TouchFocus(this)


        getPermissionsAndCameraOn()
    }

    private fun getPermissionsAndCameraOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    ) {
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.INTERNET, android.Manifest.permission.CAMERA), 0)
            } else {
                cameraApi = CameraAPI(this)
            }
        } else {
            cameraApi = CameraAPI(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults!![0] != PackageManager.PERMISSION_GRANTED) {
            finishAndRemoveTask()
            return
        }
        if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            finishAndRemoveTask()
            return
        }
        if (grantResults[2] != PackageManager.PERMISSION_GRANTED) {
            finishAndRemoveTask()
            return
        } else {
            cameraApi = CameraAPI(this)
        }
        getPermissionsAndCameraOn()
    }

    override fun onResume() {
        super.onResume()
        if (textureView!!.isAvailable) {
            openCamera(textureView!!.width, textureView!!.height)
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

    fun openCamera(width: Int, height: Int) {
        val cameraManager = cameraApi?.CameraManager()
        val cameraId = cameraApi?.CameraIdFromCharacteristics(cameraManager!!, width, height)
        cameraApi?.CameraDevice(cameraManager!!, cameraId!!)
    }

    fun closeCamera() {
        cameraApi?.closeCamera()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        firstTime = false
    }

    override fun onBackPressed() {
        if (moveFinger!!.visibility == View.VISIBLE) {
            moveFinger?.clearAnimation()
            moveFinger?.visibility = View.GONE
            return
        }

        super.onBackPressed()
    }
}
