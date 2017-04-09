package com.walfud.cache;

/**
 * Created by walfud on 09/04/2017.
 */

public interface OnEventListener<T> {
    void onAdd(String key, T value);

    void onRemove(String key, T value);
}
