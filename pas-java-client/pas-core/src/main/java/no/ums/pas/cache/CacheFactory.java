package no.ums.pas.cache;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public interface CacheFactory {

    <K, V> Cache<K, V> getCache(CacheHandler<K, V> handler);
}
