package com.walfud.cache;

/**
 * Created by walfud on 2017/4/6.
 */

public interface SerializableAndDeserializable<T> {
    public abstract byte[] serialize(T value);
    public abstract T deserialize(byte[] bytes);
}
