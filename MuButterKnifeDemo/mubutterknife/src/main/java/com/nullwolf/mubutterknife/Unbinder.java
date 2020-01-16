package com.nullwolf.mubutterknife;

import androidx.annotation.UiThread;

/**
 * to be a better man.
 *
 * @author nullWolf
 * @date 2020/1/16
 */
public interface Unbinder {
    @UiThread
    void unbind();

    Unbinder EMPTY = new Unbinder() {
        @Override
        public void unbind() {

        }
    };
}
