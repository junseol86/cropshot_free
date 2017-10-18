package ko.hyeonmin.cropshotfree.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.MotionEvent
import android.view.TextureView
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import ko.hyeonmin.cropshotfree.uitls.camera.CameraAPI
import ko.hyeonmin.cropshotfree.R
import ko.hyeonmin.cropshotfree.extended_views.CanvasView
import ko.hyeonmin.cropshotfree.listeners.*
import ko.hyeonmin.cropshotfree.uitls.*
import ko.hyeonmin.cropshotfree.uitls.camera.TouchFocus

import com.google.android.gms.ads.MobileAds;

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

    var finishPopup: ConstraintLayout? = null
    var adView: AdView? = null

    var finishButton: RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_crop_shot)

        MobileAds.initialize(this, "ca-app-pub-6948199413384164~7117346112")
        finishPopup = findViewById(R.id.finishPopup) as ConstraintLayout
        adView = findViewById(R.id.adView) as AdView
        adView?.loadAd(AdRequest.Builder().build())

        finishButton = findViewById(R.id.finishButton) as RelativeLayout
        finishButton?.setOnTouchListener({ _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> finishButton?.setBackgroundResource(R.drawable.dark_round_bordered)
                MotionEvent.ACTION_UP -> {
                    finishButton?.setBackgroundResource(R.drawable.black_round_bordered)
                    finishAndRemoveTask()
                }
            }
            true
        })

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
        cameraApi?.openBackgroundThread()
        if (textureView!!.isAvailable) {
            cameraApi?.setupCamera(textureView!!.width, textureView!!.height)
            cameraApi?.openCamera()
        } else {
            textureView?.surfaceTextureListener = MySurfaceTextureListener(this)
        }
    }

    override fun onPause() {
        cameraApi?.closeCamera()
        cameraApi?.closeBackgroundThread()
        super.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        firstTime = false
    }

    override fun onBackPressed() {
        if (moveFinger!!.visibility == View.VISIBLE) {
            moveFinger?.clearAnimation()
            moveFinger?.visibility = View.GONE
        }

        finishPopup?.visibility = if (finishPopup!!.visibility == View.GONE) View.VISIBLE else View.GONE
//        super.onBackPressed()
    }
}
