package ko.hyeonmin.cropshotfree.uitls

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import ko.hyeonmin.cropshotfree.R
import ko.hyeonmin.cropshotfree.activities.GalleryActivity
import java.io.File

/**
 * Created by junse on 2017-10-03.
 */

class GalleryTopbar(val activity: GalleryActivity) {

    var folderBtnCl: ConstraintLayout? = null
    var folderNameTV: TextView? = null
    var removeBtnRl: RelativeLayout? = null

    init {
        folderBtnCl = activity.findViewById(R.id.galFolderButtonCl) as ConstraintLayout
        folderNameTV = activity.findViewById(R.id.galFolderName) as TextView
        folderNameTV?.text = activity.caches!!.folderName

        removeBtnRl = activity.findViewById(R.id.removeRl) as RelativeLayout

        removeBtnRl?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> removeBtnRl?.setBackgroundColor(Color.parseColor("#222222"))
                MotionEvent.ACTION_UP ->  {
                    removeBtnRl?.setBackgroundColor(Color.parseColor("#111111"))
                    if (activity!!.galleryVp!!.visibility == View.VISIBLE) {
                        AlertDialog.Builder(activity)
                                .setTitle(activity.resources.getString(R.string.remove_a_photo))
                                .setPositiveButton(activity.resources.getString(R.string.yes), {_, _ ->
                                    val idxToRemove = activity.galleryVp!!.currentItem

                                    val fileToDelete = File(activity.list[idxToRemove])
                                    if (fileToDelete.exists()) {
                                        if (fileToDelete.delete()) {
                                            activity.list.removeAt(idxToRemove)

                                            // 이미지 로드 저장된 해시맵에서 지운 파일 이후의 인덱스를 하나씩 당김
                                            val newBitmapMap = HashMap<Int, Bitmap>()
                                            activity.galleryRecyclerAdapter?.bitmapMap!!.filter { it.key < idxToRemove }.map { newBitmapMap.put(it.key, it.value) }
                                            activity.galleryRecyclerAdapter?.bitmapMap!!.filter { it.key > idxToRemove }.map { newBitmapMap.put(it.key - 1, it.value) }
                                            activity.galleryRecyclerAdapter?.bitmapMap = newBitmapMap

                                            activity.galleryRecyclerAdapter?.notifyItemRemoved(idxToRemove)
                                            activity.galleryRecyclerAdapter?.notifyDataSetChanged()
                                            activity.galleryPagerAdapter?.notifyDataSetChanged()
                                            when {
                                                activity.list.isEmpty() -> activity.finish()
                                                idxToRemove >= activity.list.size -> activity.galleryVp?.currentItem = idxToRemove - 1
                                                else -> activity.galleryVp?.currentItem = idxToRemove
                                            }
                                        } else {
                                            AlertDialog.Builder(activity)
                                                    .setTitle(activity.resources.getString(R.string.remove_failed))
                                                    .setPositiveButton(activity.resources.getString(R.string.ok), {_, _ ->})
                                                    .show()
                                        }
                                    }

                                })
                                .setNegativeButton(activity.resources.getString(R.string.no), {_, _ ->})
                                .show()
                    } else {
                        AlertDialog.Builder(activity)
                                .setTitle(activity.resources.getString(R.string.remove_photos))
                                .setPositiveButton(activity.resources.getString(R.string.yes), {_, _ ->

                                })
                                .setNegativeButton(activity.resources.getString(R.string.no), {_, _ ->})
                                .show()
                    }
                }
            }
            false
        }
    }

}