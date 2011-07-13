package no.ums.pas.cache;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataOutputStream;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public interface CacheHandler<K, V> {
    
    interface Sink<T> {
        void put(T value);
    }

    void fetchValue(K key, Sink<V> valueSink);

    void markExpiredKeys(Iterable<K> keys, Sink<K> expiredKeysSink);

    void saveKey(K key, DataOutput output);

    void saveValue(V value, DataOutput output);

    K readKey(DataInput input);

    V readValue(DataInput input);
}
