package com.walfud.cache;

/**
 * Created by walfud on 2015/11/18.
 */
public class DiskCache {
//
//    public static final String TAG = "DiskCache";
//
//    private static final long CACHE_SIZE = 100L * 1024 * 1024;
//
//    private Context mContext;
//    private File mCacheDir;
//    final private File mCacheIndexFile;
//    private Lru<Bitmap> mLru;
//    private long mCacheSize;
//
//    public DiskCache(Context context) {
//        mContext = context;
//        mCacheDir = new File(ObjectUtils.getOpt(context.getExternalCacheDir(), context.getCacheDir()), "FlowImageLoader");
//        mCacheDir.mkdirs();
//        mCacheIndexFile = new File(mCacheDir, "index.json");
//        mLru = new Lru<>();
//        mLru.setOnEventListener(new Lru.OnEventListener<Bitmap>() {
//            @Override
//            public void onAdd(String key, Bitmap value) {
//                mCacheSize += value.getAllocationByteCount();
//
//                while (mCacheSize > CACHE_SIZE && mLru.evict() != null);
//            }
//
//            @Override
//            public void onRemove(String key, Bitmap value) {
//                mCacheSize -= value.getAllocationByteCount();
//            }
//        });
//    }
//
//    @Override
//    public synchronized void set(String id, Bitmap bitmap) {
//        // Bitmap -> File
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//    }
//
//    @Override
//    public synchronized Bitmap get(String id) {
//        return null;
//    }
//
//    @Override
//    public synchronized void invalid(String id) {
//    }
//
//    @Override
//    public synchronized void invalid(Uri uri) {
//    }
//
//    @Override
//    public void clear() {
//        mLru.clear();
//    }
}
