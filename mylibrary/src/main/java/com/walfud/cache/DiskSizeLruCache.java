package com.walfud.cache;

import android.content.Context;

import com.google.gson.Gson;
import com.walfud.walle.algorithm.hash.HashUtils;
import com.walfud.walle.io.IoUtils;
import com.walfud.walle.lang.ObjectUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by walfud on 2017/4/6.
 */

public abstract class DiskSizeLruCache<T> extends Cache<T> {

    public static final String TAG = "DiskSizeLruCache";

    private Context mContext;
    private File mCacheDir;
    private Map<String, String> mIndex;     // `.first` -> origin key, `.second` -> filename
    private File mIndexFile;
    private SizeLruCache<File> mCacheImpl;  // key: origin key, value: webp file

    public DiskSizeLruCache(Context context, long cacheCapability) {
        mContext = context;
        mCacheDir = new File(context.getExternalCacheDir(), com.walfud.cache.BuildConfig.APPLICATION_ID);
        mIndexFile = new File(mCacheDir, "index.json");
        mCacheImpl = new SizeLruCache<File>(cacheCapability) {
            @Override
            public long getSize(File value) {
                return value.length();
            }
        };
        mCacheImpl.setOnEventListener(new Lru.OnEventListener<File>() {
            @Override
            public void onAdd(String key, File value) {
            }

            @Override
            public void onRemove(String key, File value) {
                value.delete();

                List<String> toRemoveList = new ArrayList<>();
                mIndex.forEach((k, f) -> {
                    if (ObjectUtils.isEqual(f, key)) {
                        toRemoveList.add(k);
                    }
                });
                toRemoveList.forEach(mIndex::remove);
                syncIndex();
            }
        });

        // Init
        mCacheDir.mkdirs();
        String jsonIndex = IoUtils.read(mIndexFile);
        mIndex = new Gson().fromJson(ObjectUtils.getOpt(jsonIndex, "{}"), Map.class);
        mIndex.forEach((key, filename) -> mCacheImpl.set(filename, new File(mCacheDir, filename)));
    }

    @Override
    public void set(String key, T value) {
        String filename = HashUtils.md5(key);
        mIndex.put(key, filename);
        syncIndex();

        byte[] data = serialize(value);
        File file = IoUtils.output(new File(mCacheDir, filename), data);
        mCacheImpl.set(filename, file);
    }

    @Override
    public T get(String key) {
        String filename = mIndex.get(key);
        File file = mCacheImpl.get(filename);
        byte[] data = IoUtils.input(file);
        return deserialize(data);
    }

    @Override
    public void remove(String regKey) {
        Pattern pattern = Pattern.compile(regKey);
        List<String> toRemoveList = new ArrayList<>();
        mIndex.forEach((key, filename) -> {
            if (pattern.matcher(key).matches()) {
                toRemoveList.add(filename);
            }
        });
        toRemoveList.forEach(mCacheImpl::remove);
    }

    public abstract byte[] serialize(T value);
    public abstract T deserialize(byte[] bytes);

    // internal
    private void syncIndex() {
        IoUtils.write(mIndexFile, new Gson().toJson(mIndex));
    }
}
