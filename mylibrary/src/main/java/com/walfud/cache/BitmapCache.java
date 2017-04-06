package com.walfud.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by walfud on 2017/4/6.
 */

public class BitmapCache extends SizeCacheManager<Bitmap> {
    public BitmapCache(Context context, long memoryCacheCapability, long diskCacheCapability) {
        super(context, memoryCacheCapability, diskCacheCapability);
    }

    @Override
    public long getSize(Bitmap bitmap) {
        return bitmap.getAllocationByteCount();
    }

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
}
