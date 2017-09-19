package ko.hyeonmin.cropshotfree.uitls

import android.app.Activity
import android.support.constraint.ConstraintLayout
import ko.hyeonmin.cropshotfree.R

/**
 * Created by Hyeonmin on 2017-09-10.
 */
class Panel(activity: Activity) {
    var panelCl: ConstraintLayout? = null
    var active = true

    init {
        this.panelCl = activity.findViewById(R.id.panel) as ConstraintLayout
    }
}