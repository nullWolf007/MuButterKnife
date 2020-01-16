package com.nullwolf.mubutterknife;

import android.app.Activity;

import java.lang.reflect.Constructor;

/**
 * to be a better man.
 *
 * @author nullWolf
 * @date 2020/1/16
 */
public class MuButterKnife {
    public static Unbinder bind(Activity activity) {
        try {
            Class<? extends Unbinder> bindClass = (Class<? extends Unbinder>) Class.forName(activity.getClass().getName() + "_ViewBinding");
            //构造函数
            Constructor<? extends Unbinder> bindConstructor = bindClass.getDeclaredConstructor(activity.getClass());
            //Unbinder
            Unbinder unbinder = bindConstructor.newInstance(activity);
            return unbinder;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Unbinder.EMPTY;
    }
}
