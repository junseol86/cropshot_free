package ko.hyeonmin.cropshotfree.activities

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.os.Environment
import android.support.v4.view.ViewPager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View

import ko.hyeonmin.cropshotfree.R
import ko.hyeonmin.cropshotfree.gallery_utils.GalleryPagerAdapter
import ko.hyeonmin.cropshotfree.gallery_utils.GalleryRecyclerAdapter
import ko.hyeonmin.cropshotfree.gallery_utils.GalleryViewHolder
import ko.hyeonmin.cropshotfree.uitls.Caches
import ko.hyeonmin.cropshotfree.uitls.GalleryTopbar
import java.io.File

class GalleryActivity : Activity() {

    var caches: Caches? = null

    var galleryTopbar: GalleryTopbar? = null

    var galleryRv: RecyclerView? = null
    var galleryLm: RecyclerView.LayoutManager? = null
    var galleryRecyclerAdapter: GalleryRecyclerAdapter? = null

    var galleryVp: ViewPager? = null
    var galleryPagerAdapter: GalleryPagerAdapter? = null

    var list = ArrayList<String>()

    var selectOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        caches = Caches(this)

        galleryTopbar = GalleryTopbar(this)

        galleryRv = findViewById(R.id.galleryRv) as RecyclerView
        galleryRv?.setHasFixedSize(true)

        galleryLm = GridLayoutManager(this, 3)
        galleryRv?.layoutManager = galleryLm

        galleryVp = findViewById(R.id.galleryVp) as ViewPager

        list = getListFiles(File(Environment.getExternalStorageDirectory().toString() + "/cropshot/${caches!!.folderName}/"))

        galleryRecyclerAdapter = GalleryRecyclerAdapter(this, list)
        galleryRv?.adapter = galleryRecyclerAdapter

        galleryPagerAdapter = GalleryPagerAdapter(this)
        galleryVp?.adapter = galleryPagerAdapter
    }

    fun pickAnImage(position: Int) {
        setPagerOnOff(true)
        galleryVp?.currentItem = position
    }

    fun setPagerOnOff(onOff: Boolean) {
        galleryVp?.visibility = if (onOff) View.VISIBLE else View.GONE
        galleryTopbar?.removeBtnRl?.visibility = if (onOff) View.VISIBLE else View.GONE
    }

    fun setSelectOnOff(onOff: Boolean) {
        selectOn = onOff
        galleryRecyclerAdapter?.selectedList?.removeAll(galleryRecyclerAdapter!!.selectedList)
        galleryTopbar?.removeBtnRl?.visibility = if (onOff) View.VISIBLE else View.GONE
        galleryRecyclerAdapter?.notifyDataSetChanged()
    }

    fun isPagerOn(): Boolean = galleryVp!!.visibility == View.VISIBLE

    private fun getListFiles(parentDir: File): ArrayList<String> {
        val inFiles = ArrayList<String>()
        val files = parentDir.listFiles()
        files.sortByDescending { it.absolutePath }
        files
                .filter { !it.isDirectory && it.name.endsWith(".png") }
                .mapTo(inFiles) { it.absolutePath }
        return inFiles
    }

    override fun onBackPressed() {
        if (isPagerOn()) {
            setPagerOnOff(false)
            return
        }
        if (selectOn) {
            setSelectOnOff(false)
            return
        }
        super.onBackPressed()
    }

    fun emptyFinish() {
        AlertDialog.Builder(this)
                .setTitle("")
    }
}
