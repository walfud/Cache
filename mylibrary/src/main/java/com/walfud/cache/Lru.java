package com.walfud.cache;

import com.walfud.walle.collection.CollectionUtils;
import com.walfud.walle.lang.ObjectUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Created by walfud on 2015/11/16.
 */
public class Lru<T> {

    public static final String TAG = "Lru";

    private Deque<Entry<T>> mContainer = new ArrayDeque<>();
    private OnEventListener mOnEventListener;

    // Function

    public void setOnEventListener(OnEventListener onEventListener) {
        mOnEventListener = onEventListener;
    }

    /**
     * @param value
     */
    public void add(String key, T value) {
        remove(key);
        doAdd(new Entry<>(key, value));
    }

    /**
     * @param key
     * @return
     */
    public T get(String key) {
        Entry<T> value = CollectionUtils.find(mContainer, key, (s, t) -> ObjectUtils.isEqual(s, t.key) ? 0 : -1);
        if (value == null) {
            return null;
        }

        mContainer.remove(value);
        mContainer.addFirst(value);
        return value.value;
    }

    /**
     *
     * @param keyRgx    
     */
    public void remove(String keyRgx) {
        List<Entry<T>> toRemoveList = new ArrayList<>();
        mContainer.forEach(entry -> {
            String key = entry.key;
            if (keyRgx.matches(key)) {
                toRemoveList.add(entry);
            }
        });

        toRemoveList.forEach(this::doRemove);
    }

    /**
     * Remove oldest one to trim size
     *
     * @return
     */
    public Entry<T> evict() {
        return !mContainer.isEmpty() ? doRemove(mContainer.getLast()) : null;
    }

    public long size() {
        return mContainer.size();
    }

    // internal
    private void doAdd(Entry<T> toAdd) {
        mContainer.addFirst(toAdd);
        if (mOnEventListener != null) {
            mOnEventListener.onAdd(toAdd.key, toAdd.value);
        }
    }

    private Entry<T> doRemove(Entry<T> toRemove) {
        mContainer.remove(toRemove);
        if (mOnEventListener != null) {
            mOnEventListener.onRemove(toRemove.key, toRemove.value);
        }
        return toRemove;
    }

    //
    private static class Entry<T> {
        public String key;
        public T value;

        public Entry(String key, T value) {
            this.key = key;
            this.value = value;
        }
    }

    public interface OnEventListener<T> {
        void onAdd(String key, T value);

        void onRemove(String key, T value);
    }
}
