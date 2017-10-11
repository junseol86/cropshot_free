package ko.hyeonmin.cropshotfree.uitls.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.Image
import android.media.ImageReader
import android.media.MediaScannerConnection
import android.os.Environment
import android.text.format.DateFormat
import android.widget.Toast
import ko.hyeonmin.cropshotfree.activities.CropShotActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


/**
 * Created by junse on 2017-10-08.
 */
class PhotoTaker(val cameraAPI: CameraAPI) {

    var photoTaking = false
    var mImageReader: ImageReader? = null
    val mOnImageAvailableListener = ImageReader.OnImageAvailableListener {
        reader -> cameraAPI.mBackgroundHandler?.post(ImageSaver(reader!!.acquireNextImage(), cameraAPI!!.activity!!))
    }

    fun setPhotoTakingAndSpinner(onOff: Boolean) {
        photoTaking = onOff
    }

    class ImageSaver(val image: Image, val activity: CropShotActivity): Runnable {
        companion object {
            var mImageFile: File? = null
            var orgBitmap: Bitmap? = null
            var rotBitmap: Bitmap? = null
            var cropBitmap: Bitmap? = null
        }


        override fun run() {
            var directory = ""
            var fileName = ""
            var folder: File? = null
            var createFolderSucess = false

            val now = Date()

            directory = Environment.getExternalStorageDirectory().toString() + "/cropshot"
            folder = File(directory)
            if (!folder!!.exists()) {
                createFolderSucess = folder?.mkdir()!!
                if (!createFolderSucess) {
                    return
                }
            }

            directory += "/${activity.caches!!.folderName}"
            folder = File(directory)
            if (!folder.exists()) {
                createFolderSucess = folder.mkdir()
                if (!createFolderSucess) {
                    return
                }
            }

            fileName = "$directory/${DateFormat.format("yyyy-MM-dd-HH-mm-ss", now)}.jpg"

            mImageFile = File(fileName)

            println("BeforeBuffer")

            val byteBuffer = image.planes[0].buffer
            val bytes = ByteArray(byteBuffer.remaining())
            byteBuffer.get(bytes)
            orgBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

            println("Bitmap made")

            val matrix = Matrix()
            matrix.postRotate(90f)
            rotBitmap = Bitmap.createBitmap(orgBitmap!!, 0, 0, orgBitmap!!.width, orgBitmap!!.height, matrix, true)
            orgBitmap = null

            var fileOutputStream: FileOutputStream? = null

            val height = rotBitmap!!.width
            val width = height * activity.canvasView!!.width / activity.canvasView!!.height
            val offsetX = (rotBitmap!!.width - width) / 2
            val offsetY = (rotBitmap!!.height - height) / 2
            var cropXY = hashMapOf(0 to 0, 1 to 0, 2 to 0, 3 to 0)
            (0..3).map {
                cropXY[it] = (activity.canvasView!!.cropXY[it]!! * width / activity.canvasView!!.width).toInt()
            }

            println("$offsetX $offsetY $width $height")

            cropBitmap = Bitmap.createBitmap(rotBitmap!!, offsetX + cropXY[0]!!.toInt(), offsetY + cropXY[1]!!.toInt(),
                    Math.abs(cropXY[2]!! - cropXY[0]!!), Math.abs(cropXY[3]!! - cropXY[1]!!))
            rotBitmap = null

            try {
                fileOutputStream = FileOutputStream(mImageFile)
                cropBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                fileOutputStream.flush()
            } catch(e: IOException) {
                e.printStackTrace()
            } finally {
                image.close()
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        println("Image Saved")
                        cropBitmap = null
                        Toast.makeText(activity, "IMAGE SAVED", Toast.LENGTH_SHORT).show()
                        MediaScannerConnection.scanFile(activity, arrayOf(fileName), arrayOf("image/jpg"), null)
                        activity.cameraApi!!.photoTaker.setPhotoTakingAndSpinner(false)
                    }
                }
            }
        }

    }
}