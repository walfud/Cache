package com.walfud.cache;

import android.content.Context;
import android.util.Log;

/**
 * Created by walfud on 2015/11/15.
 */
public abstract class SizeCacheManager<T> implements Cache<T>, Sizable<T>, SerializableAndDeserializable<T> {

    public static final String TAG = "SizeCacheManager";

    private Context mContext;
    private MemorySizeLruCache<T> mMemoryCache;
    private DiskSizeLruCache<T> mDiskCache;

    public SizeCacheManager(Context context, long memoryCacheCapability, long diskCacheCapability) {
        mContext = context;
        mMemoryCache = new MemorySizeLruCache<T>(memoryCacheCapability) {
            @Override
            public long getSize(T value) {
                return SizeCacheManager.this.getSize(value);
            }
        };
        mDiskCache = new DiskSizeLruCache<T>(mContext, diskCacheCapability) {
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
            if (value != null) {
                Log.v(TAG, String.format("hit(memory): %s", key));
            }
        }
        if (value == null && disk) {
            value = mDiskCache.get(key);
            if (value != null) {
                Log.v(TAG, String.format("hit(disk): %s", key));
            }
        }

        if (value == null) {
            Log.v(TAG, String.format("miss: %s", key));
        }

        return value;
    }

    public void add(String key, T value) {
        set(key, value, true, true);
    }
    public void set(String key, T value, boolean memory, boolean disk) {
        if (memory) {
            mMemoryCache.add(key, value);
        }
        if (disk) {
            mDiskCache.add(key, value);
        }
    }
    public void invalidate(String key) {
        invalidate(key, true, true);
    }
    public void invalidate(String key, boolean memory, boolean disk) {
        if (memory) {
            mMemoryCache.invalidate(key);
        }
        if (disk) {
            mDiskCache.invalidate(key);
        }
    }
}
