package ko.hyeonmin.cropshotfree.uitls

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.view.MotionEvent
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
                    if (activity.isPagerOn()) {
                        AlertDialog.Builder(activity)
                                .setTitle(activity.resources.getString(R.string.remove_a_photo))
                                .setPositiveButton(activity.resources.getString(R.string.yes), {_, _ ->
                                    val idxToRemove = activity.galleryVp!!.currentItem
                                    deletePhoto(idxToRemove)
                                })
                                .setNegativeButton(activity.resources.getString(R.string.no), {_, _ ->})
                                .show()
                    } else {
                        AlertDialog.Builder(activity)
                                .setTitle(activity.resources.getString(R.string.remove_photos))
                                .setPositiveButton(activity.resources.getString(R.string.yes), {_, _ ->
                                    deletePhotos(activity.galleryRecyclerAdapter!!.selectedList)
                                })
                                .setNegativeButton(activity.resources.getString(R.string.no), {_, _ ->})
                                .show()
                    }
                }
            }
            false
        }
    }

    fun deletePhoto(idxToRemove: Int) {
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

                when {
                    activity.list.isEmpty() -> activity.finish()
                    activity.isPagerOn() -> {
                        activity.galleryPagerAdapter?.notifyDataSetChanged()
                        activity.galleryVp?.currentItem = if (idxToRemove >= activity.list.size) idxToRemove - 1 else idxToRemove
                    }
                }
            } else {
                AlertDialog.Builder(activity)
                        .setTitle(activity.resources.getString(R.string.remove_failed))
                        .setPositiveButton(activity.resources.getString(R.string.ok), {_, _ ->})
                        .show()
            }
        }
    }

    fun deletePhotos(selectedList: ArrayList<Int>) {
        val deletedList = ArrayList<String>()
        selectedList.map {
            val fileToDelete = File(activity.list[it])
            if (fileToDelete.exists()) {
                if (fileToDelete.delete()) {
                    deletedList.add(activity.list[it])
                }
            }
        }
        deletedList.map {
            activity.list.remove(it)
        }
        activity.galleryRecyclerAdapter!!.bitmapMap = hashMapOf()
        activity.galleryRecyclerAdapter!!.notifyDataSetChanged()
        activity.galleryRecyclerAdapter!!.selectedList.removeAll(activity.galleryRecyclerAdapter!!.selectedList)
        if (activity.list.isEmpty())
            activity.finish()
    }

}