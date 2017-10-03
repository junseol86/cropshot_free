package ko.hyeonmin.cropshotfree.uitls

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Environment
import android.support.constraint.ConstraintLayout
import android.support.v4.content.FileProvider
import android.view.MotionEvent
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

    init {
        panelCl = activity.findViewById(R.id.panel) as ConstraintLayout
        folderBtnCl = activity.findViewById(R.id.folderButtonCl) as ConstraintLayout
        folderNameTV = activity.findViewById(R.id.folderName) as TextView
        folderNameTV?.text = activity.caches!!.folderName

        folderBtnCl?.setOnTouchListener { _, event ->
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
                        activity.startActivity(Intent(activity, GalleryActivity::class.java))
                    }
                }
            }
            false
        }
    }
}