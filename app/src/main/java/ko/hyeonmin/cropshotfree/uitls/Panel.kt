package ko.hyeonmin.cropshotfree.uitls

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Environment
import android.support.constraint.ConstraintLayout
import android.support.v4.content.FileProvider
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import ko.hyeonmin.cropshotfree.activities.CropShotActivity
import ko.hyeonmin.cropshotfree.R
import ko.hyeonmin.cropshotfree.activities.GalleryActivity
import java.io.File

/**
 * Created by Hyeonmin on 2017-09-10.
 */

class Panel(activity: CropShotActivity) {
    var panelCl: ConstraintLayout? = null
    var active = true

    var folderBtnCl: ConstraintLayout? = null
    var folderNameTV: TextView? = null
    var directory = ""
    var folder: File? = null
    var createFolderSucess = false

    var deleteRecentPanel: LinearLayout? = null
    var deleteRecentBtn: ImageView? = null
    var recentPreview: ImageView? = null
    var recentFileName = ""

    init {
        panelCl = activity.findViewById(R.id.panel) as ConstraintLayout
        folderBtnCl = activity.findViewById(R.id.folderButtonCl) as ConstraintLayout
        folderNameTV = activity.findViewById(R.id.folderName) as TextView
        folderNameTV?.text = activity.caches!!.folderName

        folderBtnCl?.setOnTouchListener { _, event ->

            if (activity.moveFinger!!.visibility == View.VISIBLE) {
                activity.moveFinger!!.clearAnimation()
                activity.moveFinger!!.visibility = View.GONE
            }

            when (event!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    folderBtnCl?.setBackgroundColor(Color.parseColor("#222222"))
                }
                MotionEvent.ACTION_UP -> {
                    folderBtnCl?.setBackgroundColor(Color.parseColor("#111111"))

                    if (!File(Environment.getExternalStorageDirectory().toString() + "/cropshot").exists() ||
                            !File(Environment.getExternalStorageDirectory().toString() + "/cropshot/${activity.caches!!.folderName}").exists() ||
                            File(Environment.getExternalStorageDirectory().toString() + "/cropshot/${activity.caches!!.folderName}").listFiles().isEmpty()
                            ) {
                        AlertDialog.Builder(activity).setTitle(activity.resources.getString(R.string.empty_folder))
                                .setCancelable(true)
                                .setPositiveButton(activity.resources.getText(R.string.ok)) { _, _ ->  }
                                .show()
                    } else {
                        activity.startActivityForResult(Intent(activity, GalleryActivity::class.java), 0)
                    }
                }
            }
            false
        }

        deleteRecentPanel = activity.findViewById(R.id.deleteRecentPanel) as LinearLayout
        deleteRecentBtn = activity.findViewById(R.id.deleteRecentButton) as ImageView
        deleteRecentBtn?.setOnClickListener {
            AlertDialog.Builder(activity)
                    .setTitle(R.string.remove_a_photo)
                    .setPositiveButton(activity.resources.getString(R.string.yes), {_, _ ->
                        val fileToDelete = File(recentFileName)
                        if (fileToDelete.exists()) {
                            if (fileToDelete.delete()) {
                                deleteRecentPanel?.visibility = View.GONE
                            }
                        }
                    })
                    .setNegativeButton(activity.resources.getString(R.string.no), {_, _ ->
                    })
                    .show()
        }
        recentPreview = activity.findViewById(R.id.recentPreview) as ImageView
        recentPreview?.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.setDataAndType(FileProvider.getUriForFile(
                    activity,
                    "ko.hyeonmin.cropshotfree.provider",
                    File(recentFileName)),
                    "image/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            activity.startActivity(intent)
        }
    }

    fun showPreview(fileName: String) {
        deleteRecentPanel?.visibility = View.VISIBLE
        recentPreview?.setImageBitmap(BitmapFactory.decodeFile(fileName))
        recentFileName = fileName
    }

}