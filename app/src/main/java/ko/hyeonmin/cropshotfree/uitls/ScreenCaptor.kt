package ko.hyeonmin.cropshotfree.uitls

import android.graphics.Bitmap
import android.os.Environment
import android.text.format.DateFormat
import ko.hyeonmin.cropshotfree.CropShotActivity
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by junse on 2017-09-18.
 */
class ScreenCaptor(activity: CropShotActivity) {
    val activity = activity
    var now: Date? = null
    var imageFile: File? = null
    var fileOutputStream: FileOutputStream? = null

    fun takeScreenShot(cropXY: HashMap<Int, Float>) {
        now = Date()
        DateFormat.format("yyyy-MM-dd_hh:mm:ss", now)

        try {
            activity.canvasView?.isDrawingCacheEnabled = true

            val bitmap = Bitmap.createBitmap(activity.canvasView!!.drawingCache, cropXY[0]!!.toInt(), cropXY[1]!!.toInt(), Math.abs(cropXY[0]!! - cropXY[2]!!).toInt(), Math.abs(cropXY[1]!! - cropXY[3]!!).toInt())
            activity.canvasView!!.isDrawingCacheEnabled = false

            imageFile = File(Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg")
            fileOutputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream?.flush()
            fileOutputStream?.close()
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }
}