package ko.hyeonmin.cropshotfree.activities

import android.app.Activity
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager

import ko.hyeonmin.cropshotfree.R
import ko.hyeonmin.cropshotfree.recycler_parts.GalleryAdapter
import ko.hyeonmin.cropshotfree.recycler_parts.GalleryViewHolder
import ko.hyeonmin.cropshotfree.uitls.Caches
import java.io.File

class GalleryActivity : Activity() {

    var caches: Caches? = null

    var galleryRv: RecyclerView? = null
    var galleryLm: RecyclerView.LayoutManager? = null
    var galleryAdapter: RecyclerView.Adapter<GalleryViewHolder>? = null
    var list = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        caches = Caches(this)

        galleryRv = findViewById(R.id.galleryRv) as RecyclerView
        galleryRv?.setHasFixedSize(true)

        galleryLm = StaggeredGridLayoutManager(3, 1)
        galleryRv?.layoutManager = galleryLm

        list = getListFiles(File(Environment.getExternalStorageDirectory().toString() + "/cropshot/${caches!!.folderName}/"))

        galleryAdapter = GalleryAdapter(this, list)
        galleryRv?.adapter = galleryAdapter
    }

    private fun getListFiles(parentDir: File): ArrayList<String> {
        val inFiles = ArrayList<String>()
        val files = parentDir.listFiles()
        for (file in files) {
            if (file.isDirectory) {
                continue
            } else {
                if (file.name.endsWith(".png")) {
                    inFiles.add(file.absolutePath)
                }
            }
        }
        return inFiles
    }
}
