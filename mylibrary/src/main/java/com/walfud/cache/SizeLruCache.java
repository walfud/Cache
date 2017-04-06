package com.walfud.cache;

import android.util.Log;

/**
 * Created by walfud on 2015/11/15.
 */
public abstract class SizeLruCache<T> extends Cache<T> {

    public static final String TAG = "SizeLruCache";

    protected Lru<T> mLru;
    private Lru.OnEventListener<T> mOnEventListener;
    private long mCacheCapability;

    public SizeLruCache(long cacheCapability) {
        mLru = new Lru<>();
        mLru.setOnEventListener(new Lru.OnEventListener<T>() {
            private long mCacheSize;

            @Override
            public void onAdd(String key, T value) {
                Log.e(TAG, "onAdd: " + key);

                mCacheSize += getSize(value);

                if (mOnEventListener != null) {
                    mOnEventListener.onAdd(key, value);
                }

                while (mCacheSize > mCacheCapability && mLru.evict() != null);
            }

            @Override
            public void onRemove(String key, T value) {
                Log.e(TAG, "onRemove: " + key);

                mCacheSize -= getSize(value);

                if (mOnEventListener != null) {
                    mOnEventListener.onRemove(key, value);
                }
            }
        });
        mCacheCapability = cacheCapability;
    }

    @Override
    public void set(String key, T value) {
        mLru.set(key, value);
    }

    @Override
    public T get(String key) {
        return mLru.get(key);
    }

    @Override
    public void remove(String regKey) {
        mLru.remove(regKey);
    }

    public void setOnEventListener(Lru.OnEventListener<T> onEventListener) {
        mOnEventListener = onEventListener;
    }

    //
    public abstract long getSize(T value);
}
