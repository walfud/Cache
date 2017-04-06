package com.walfud.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by walfud on 2015/11/15.
 */
public class BitmapCacheManager {

    public static final String TAG = "BitmapCacheManager";

    private Context mContext;
    private MemorySizeLruCache<Bitmap> mMemoryCache;
    private DiskSizeLruCache<Bitmap> mDiskCache;

    public BitmapCacheManager(Context context) {
        mContext = context;
        mMemoryCache = new MemorySizeLruCache<Bitmap>(100 * 1024 * 1024) {
            @Override
            public long getSize(Bitmap value) {
                return value.getAllocationByteCount();
            }
        };
        mDiskCache = new DiskSizeLruCache<Bitmap>(mContext, 1 * 1024 * 1024) {
            @Override
            public byte[] serialize(Bitmap value) {
                byte[] bytes = null;
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(10 * 1024);
                    value.compress(Bitmap.CompressFormat.WEBP, 100, baos);
                    bytes = baos.toByteArray();
                    baos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return bytes;
            }

            @Override
            public Bitmap deserialize(byte[] bytes) {
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
        };
    }

    // Function
    public Bitmap get(String key) {
        return get(key, true, true);
    }
    public Bitmap get(String key, boolean memory, boolean disk) {
        Bitmap value = null;
        if (memory) {
            value = mMemoryCache.get(key);
        }
        if (value == null && disk) {
            value = mDiskCache.get(key);
        }

        return value;
    }

    public BitmapCacheManager set(String key, Bitmap bitmap) {
        return set(key, bitmap, true, true);
    }
    public BitmapCacheManager set(String key, Bitmap bitmap, boolean memory, boolean disk) {
        if (memory) {
            mMemoryCache.set(key, bitmap);
        }
        if (disk) {
            mDiskCache.set(key, bitmap);
        }

        return this;
    }
    public BitmapCacheManager invalidate(String regKey) {
        return invalidate(regKey, true, true);
    }
    public BitmapCacheManager invalidate(String regKey, boolean memory, boolean disk) {
        if (memory) {
            mMemoryCache.remove(regKey);
        }
        if (disk) {
            mDiskCache.remove(regKey);
        }

        return this;
    }
}
