package ko.hyeonmin.cropshotfree.listeners

import android.view.TextureView
import ko.hyeonmin.cropshotfree.activities.CropShotActivity

/**
 * Created by Hyeonmin on 2017-09-10.
 */
class MySurfaceTextureListener(activity: CropShotActivity): TextureView.SurfaceTextureListener {
    private var activity: CropShotActivity = activity
    override fun onSurfaceTextureSizeChanged(p0: android.graphics.SurfaceTexture?, p1: Int, p2: Int) {
    }

    override fun onSurfaceTextureUpdated(p0: android.graphics.SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(p0: android.graphics.SurfaceTexture?): Boolean {
        return true
    }

    override fun onSurfaceTextureAvailable(p0: android.graphics.SurfaceTexture?, width: Int, height: Int) {
        activity.cameraApi?.setupCamera(width, height)
        activity.cameraApi?.openCamera()
    }
}