package com.walfud.cache;

import android.content.Context;
import android.util.Log;

import java.io.File;

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
        mMemoryCache.setOnEventListener(new OnEventListener<T>() {
            @Override
            public void onAdd(String key, T value) {
                Log.v(TAG, "onAdd(memory): " + key);
            }

            @Override
            public void onRemove(String key, T value) {
                Log.v(TAG, "onRemove(memory): " + key);
            }
        });
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
        mDiskCache.setOnEventListener(new OnEventListener<File>() {
            @Override
            public void onAdd(String key, File value) {
                Log.v(TAG, "onAdd(disk): " + key);
            }

            @Override
            public void onRemove(String key, File value) {
                Log.v(TAG, "onRemove(disk): " + key);
            }
        });
    }

    // Function
    public T get(String key) {
        return get(key, true, true);
    }
    public T get(String key, boolean memory, boolean disk) {
        T value;
        if (memory) {
            value = mMemoryCache.get(key);
            if (value != null) {
                Log.v(TAG, String.format("hit(memory): %s", key));
                return value;
            }
        }
        if (disk) {
            value = mDiskCache.get(key);
            if (value != null) {
                put(key, value, true, false);                       // Touch memory cache
                Log.v(TAG, String.format("hit(disk): %s", key));
                return value;
            }
        }

        Log.v(TAG, String.format("miss: %s", key));
        return null;
    }

    @Override
    public void put(String key, T value) {
        put(key, value, true, true);
    }
    public void put(String key, T value, boolean memory, boolean disk) {
        if (memory) {
            mMemoryCache.put(key, value);
        }
        if (disk) {
            mDiskCache.put(key, value);
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
