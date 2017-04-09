package com.walfud.cache;

import android.content.Context;

import com.google.gson.Gson;
import com.walfud.walle.algorithm.hash.HashUtils;
import com.walfud.walle.io.IoUtils;
import com.walfud.walle.lang.ObjectUtils;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by walfud on 2017/4/6.
 */

public abstract class DiskSizeLruCache<T> implements Cache<T>, SerializableAndDeserializable<T> {

    public static final String TAG = "DiskSizeLruCache";

    private Context mContext;
    private File mCacheDir;
    private File mIndexFile;
    private SizeLruCache<File> mCacheImpl;  // key: origin key, value: webp file
    private OnEventListener<File> mOnEventListener;

    public DiskSizeLruCache(Context context, long cacheCapability) {
        mContext = context;
        mCacheDir = new File(context.getExternalCacheDir(), com.walfud.cache.BuildConfig.APPLICATION_ID);
        mIndexFile = new File(mCacheDir, "index.json");
        mCacheImpl = new SizeLruCache<File>(cacheCapability) {
            @Override
            public long getSize(File value) {
                return value.length() + toTmpFile(value).length();
            }
        };
        mCacheImpl.setOnEventListener(new OnEventListener<File>() {
            @Override
            public void onAdd(String key, File value) {
                File tmpFile = toTmpFile(value);
                tmpFile.renameTo(new File(mCacheDir, HashUtils.md5(key)));
                syncIndex();

                if (mOnEventListener != null) {
                    mOnEventListener.onAdd(key, value);
                }
            }

            @Override
            public void onRemove(String key, File value) {
                value.delete();
                syncIndex();

                if (mOnEventListener != null) {
                    mOnEventListener.onRemove(key, value);
                }
            }
        });

        // Init
        mCacheDir.mkdirs();
        String jsonIndex = IoUtils.read(mIndexFile);
        List<Lru.Entry<File>> kv = new Gson().fromJson(ObjectUtils.getOpt(jsonIndex, "[]"), new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{new ParameterizedType() {
                    @Override
                    public Type[] getActualTypeArguments() {
                        return new Type[]{File.class};
                    }

                    @Override
                    public Type getRawType() {
                        return Lru.Entry.class;
                    }

                    @Override
                    public Type getOwnerType() {
                        return null;
                    }
                }};
            }

            @Override
            public Type getRawType() {
                return List.class;
            }

            @Override
            public Type getOwnerType() {
                return new ParameterizedType() {
                    @Override
                    public Type[] getActualTypeArguments() {
                        return new Type[]{File.class};
                    }

                    @Override
                    public Type getRawType() {
                        return Lru.class;
                    }

                    @Override
                    public Type getOwnerType() {
                        return null;
                    }
                };
            }
        });
        kv.forEach(entry -> mCacheImpl.add(entry.key, entry.value));
    }

    @Override
    public void add(String key, T value) {
        File old = mCacheImpl.get(key);
        if (old != null) {
            return;
        }

        String filename = HashUtils.md5(key);
        byte[] data = serialize(value);
        File file = new File(mCacheDir, filename);
        File cached = IoUtils.output(toTmpFile(file), data);
        if (cached == null) {
            return;
        }

        mCacheImpl.add(key, file);
    }

    @Override
    public T get(String key) {
        File file = mCacheImpl.get(key);
        if (file == null || !file.exists() || !file.canRead()) {
            return null;
        }

        byte[] data = IoUtils.input(file);
        return deserialize(data);
    }

    @Override
    public void invalidate(String key) {
        mCacheImpl.invalidate(key);
    }

    public void setOnEventListener(OnEventListener onEventListener) {
        mOnEventListener = onEventListener;
    }

    // internal
    private File toTmpFile(File file) {
        return new File(file.getAbsolutePath() + ".tmp");
    }
    private void syncIndex() {
        IoUtils.write(mIndexFile, mCacheImpl.toString());
    }
}
