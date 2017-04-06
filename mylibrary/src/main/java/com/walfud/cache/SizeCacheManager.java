package com.walfud.cache;

import android.content.Context;

/**
 * Created by walfud on 2015/11/15.
 */
public abstract class SizeCacheManager<T> implements Cache<T>, Sizable<T>, SerializableAndDeserializable<T> {

    public static final String TAG = "SizeCacheManager";

    private Context mContext;
    private MemorySizeLruCache<T> mMemoryCache;
    private DiskSizeLruCache<T> mDiskCache;

    public SizeCacheManager(Context context) {
        mContext = context;
        mMemoryCache = new MemorySizeLruCache<T>(100 * 1024 * 1024) {
            @Override
            public long getSize(T value) {
                return SizeCacheManager.this.getSize(value);
            }
        };
        mDiskCache = new DiskSizeLruCache<T>(mContext, 1 * 1024 * 1024) {
            @Override
            public byte[] serialize(T value) {
                return SizeCacheManager.this.serialize(value);
            }

            @Override
            public T deserialize(byte[] bytes) {
                return SizeCacheManager.this.deserialize(bytes);
            }
        };
    }

    // Function
    public T get(String key) {
        return get(key, true, true);
    }
    public T get(String key, boolean memory, boolean disk) {
        T value = null;
        if (memory) {
            value = mMemoryCache.get(key);
        }
        if (value == null && disk) {
            value = mDiskCache.get(key);
        }

        return value;
    }

    public void set(String key, T value) {
        set(key, value, true, true);
    }
    public void set(String key, T value, boolean memory, boolean disk) {
        if (memory) {
            mMemoryCache.set(key, value);
        }
        if (disk) {
            mDiskCache.set(key, value);
        }
    }
    public void invalidate(String regKey) {
        invalidate(regKey, true, true);
    }
    public void invalidate(String regKey, boolean memory, boolean disk) {
        if (memory) {
            mMemoryCache.invalidate(regKey);
        }
        if (disk) {
            mDiskCache.invalidate(regKey);
        }
    }
}
