package com.walfud.cache;

/**
 * Created by walfud on 2015/11/15.
 */
public abstract class SizeLruCache<T> implements Cache<T>, Sizable<T> {

    public static final String TAG = "SizeLruCache";

    protected Lru<T> mLru;
    private OnEventListener<T> mOnEventListener;
    private long mCacheCapability;

    public SizeLruCache(long cacheCapability) {
        mLru = new Lru<>();
        mLru.setOnEventListener(new OnEventListener<T>() {
            private long mCacheSize;

            @Override
            public void onAdd(String key, T value) {
                mCacheSize += getSize(value);

                if (mOnEventListener != null) {
                    mOnEventListener.onAdd(key, value);
                }

                while (mCacheSize > mCacheCapability && mLru.evict() != null);
            }

            @Override
            public void onRemove(String key, T value) {
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
    public void invalidate(String key) {
        mLru.remove(key);
    }

    public void setOnEventListener(OnEventListener<T> onEventListener) {
        mOnEventListener = onEventListener;
    }

    @Override
    public String toString() {
        return mLru.toString();
    }
}
