package ko.hyeonmin.cropshotfree.uitls

import android.content.Context
import android.content.SharedPreferences
import ko.hyeonmin.cropshotfree.CropShotActivity

/**
 * Created by junse on 2017-09-22.
 */
class Caches(activity: CropShotActivity) {
    var sharedPrefs : SharedPreferences = activity.getSharedPreferences("account", Context.MODE_PRIVATE)

    var folderName: String
        get() = sharedPrefs.getString("folder_name", "default")
        set(string) {
            sharedPrefs.edit().putString("folder_name", string).commit()
        }
}