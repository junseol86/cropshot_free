package ko.hyeonmin.cropshotfree.uitls

import android.app.Activity
import android.support.constraint.ConstraintLayout
import ko.hyeonmin.cropshotfree.R

/**
 * Created by Hyeonmin on 2017-09-10.
 */
class Console(activity: Activity) {
    private var consoleCl: ConstraintLayout? = null
    var active = true

    init {
        this.consoleCl = activity.findViewById(R.id.console) as ConstraintLayout
    }

}