package com.walfud.cache;

/**
 * Created by walfud on 2015/11/15.
 */
public interface Cache<T> {

    String TAG = "Cache";

    void put(String key, T value);

    T get(String key);

    void invalidate(String key);
}
