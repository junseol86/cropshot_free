package ko.hyeonmin.cropshotfree.uitls

import android.content.Intent
import android.net.Uri
import com.android.volley.Request
import ko.hyeonmin.cropshotfree.activities.CropShotActivity

import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

/**
 * Created by junse on 2017-09-28.
 */
class VersionCheck(val activity: CropShotActivity) {
    private var rq = Volley.newRequestQueue(activity)!!

    fun sendVersionCheckRequest() {
        val versionCheckRequest = object : StringRequest(Request.Method.GET,
                Secrets.versionCheckUrl,
                Response.Listener {
                    val jo = JSONObject(it)
                    val currentVersion = activity.packageManager.getPackageInfo(activity.packageName, 0).versionCode
                    if (Integer.parseInt(jo["free_version"].toString()) > currentVersion) {
                        activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(jo["free_url"].toString())))
                    }
                },
                Response.ErrorListener {
                    println("ERROR")
                }
        ) {
            override fun getHeaders(): Map<String, String> {
                return HashMap()
            }
        }
        rq.add(versionCheckRequest)
    }

}