package com.nullwolf.mubutterknife;

import android.app.Activity;
import android.view.View;

/**
 * to be a better man.
 *
 * @author nullWolf
 * @date 2020/1/16
 */
public class Utils {

    /**
     * findViewById
     *
     * @param activity
     * @param resourceId
     * @param <T>
     * @return
     */
    public static <T extends View> T findViewById(Activity activity, int resourceId) {
        return (T) activity.findViewById(resourceId);
    }
}
