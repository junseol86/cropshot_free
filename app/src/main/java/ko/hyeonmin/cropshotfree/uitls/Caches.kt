package ko.hyeonmin.cropshotfree.uitls

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

/**
 * Created by junse on 2017-09-22.
 */
class Caches(activity: Activity) {
    var sharedPrefs : SharedPreferences = activity.getSharedPreferences("account", Context.MODE_PRIVATE)

    var folderName: String
        get() = sharedPrefs.getString("folder_name", "default")
        set(string) {
            sharedPrefs.edit().putString("folder_name", string).commit()
        }

    var proVersion: Int
        get() = sharedPrefs.getInt("pro_version", -1)
        set(int) {
            sharedPrefs.edit().putInt("pro_version", int).commit()
        }
}