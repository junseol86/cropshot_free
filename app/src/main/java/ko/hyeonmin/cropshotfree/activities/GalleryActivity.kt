package ko.hyeonmin.cropshotfree.activities

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.os.Environment
import android.support.v4.view.ViewPager
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        caches = Caches(this)

        galleryTopbar = GalleryTopbar(this)

        galleryRv = findViewById(R.id.galleryRv) as RecyclerView
        galleryRv?.setHasFixedSize(true)

        galleryLm = StaggeredGridLayoutManager(3, 1)
        galleryRv?.layoutManager = galleryLm

        galleryVp = findViewById(R.id.galleryVp) as ViewPager

        list = getListFiles(File(Environment.getExternalStorageDirectory().toString() + "/cropshot/${caches!!.folderName}/"))

        galleryRecyclerAdapter = GalleryRecyclerAdapter(this, list)
        galleryRv?.adapter = galleryRecyclerAdapter

        galleryPagerAdapter = GalleryPagerAdapter(this)
        galleryVp?.adapter = galleryPagerAdapter
    }

    fun pickAnImage(position: Int) {
        galleryVp?.visibility = View.VISIBLE
        galleryVp?.currentItem = position
    }

    private fun getListFiles(parentDir: File): ArrayList<String> {
        val inFiles = ArrayList<String>()
        val files = parentDir.listFiles()
        for (i in files.size - 1 downTo 0) {
            if (files[i].isDirectory) {
                continue
            } else {
                if (files[i].name.endsWith(".png")) {
                    inFiles.add(files[i].absolutePath)
                }
            }
        }
        return inFiles
    }

    override fun onBackPressed() {
        if (galleryVp!!.visibility == View.VISIBLE) {
            galleryVp!!.visibility = View.GONE
            return
        }
        super.onBackPressed()
    }

    fun emptyFinish() {
        AlertDialog.Builder(this)
                .setTitle("")
    }
}
