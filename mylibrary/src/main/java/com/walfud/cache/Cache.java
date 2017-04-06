package com.walfud.cache;

/**
 * Created by walfud on 2015/11/15.
 */
public abstract class Cache<T> {

    public static final String TAG = "Cache";

    public abstract void set(String key, T value);

    public abstract T get(String key);

    public abstract void remove(String rgxKey);
}
