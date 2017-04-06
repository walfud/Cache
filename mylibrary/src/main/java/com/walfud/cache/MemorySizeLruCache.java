package com.walfud.cache;

/**
 * Created by walfud on 2017/4/5.
 */

public abstract class MemorySizeLruCache<T> extends SizeLruCache<T> {
    public MemorySizeLruCache(long cacheCapability) {
        super(cacheCapability);
    }
}
