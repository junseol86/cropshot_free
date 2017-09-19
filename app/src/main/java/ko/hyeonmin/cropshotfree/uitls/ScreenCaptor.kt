package ko.hyeonmin.cropshotfree.uitls

import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
import android.text.format.DateFormat
import ko.hyeonmin.cropshotfree.CropShotActivity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by junse on 2017-09-18.
 */
class ScreenCaptor(activity: CropShotActivity) {
    val activity = activity
    var now: Date? = null
    var directory = ""
    var fileName = ""
    var folder: File? = null
    var createFolderSucess = false
    var imageFile: File? = null
    var fileOutputStream: FileOutputStream? = null

    fun takeScreenShot(cropXY: HashMap<Int, Float>) {
        now = Date()
        DateFormat.format("yyyy-MM-dd_hh:mm:ss", now)

        val bitmap = Bitmap.createBitmap(activity.textureView!!.bitmap, cropXY[0]!!.toInt(), cropXY[1]!!.toInt(), Math.abs(cropXY[0]!! - cropXY[2]!!).toInt(), Math.abs(cropXY[1]!! - cropXY[3]!!).toInt())

        directory = Environment.getExternalStorageDirectory().toString() + "/cropshot"
        folder = File(directory)
        if (!folder!!.exists()) {
            createFolderSucess = folder?.mkdir()!!
            if (!createFolderSucess) {
                return
            }
        }

        directory += "/default"
        folder = File(directory)
        if (!folder!!.exists()) {
            createFolderSucess = folder?.mkdir()!!
            if (!createFolderSucess) {
                return
            }
        }

        fileName = "$directory/$now.png"

        try {
            imageFile = File(fileName)
            fileOutputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream)
            fileOutputStream?.flush()
            fileOutputStream?.close()
            MediaScannerConnection.scanFile(activity, arrayOf(fileName), arrayOf("image/png"), null)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }
}