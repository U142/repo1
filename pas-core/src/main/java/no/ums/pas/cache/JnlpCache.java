package no.ums.pas.cache;

import javax.annotation.Nullable;

/**
 * @author Ståle Undheim <su@ums.no>
 */
class JnlpCache<K, V> implements Cache<K, V> {

    private final CacheHandler<K, V> handler;

    public JnlpCache(final CacheHandler<K, V> handler) {
        this.handler = handler;
    }

    @Override
    public V apply(@Nullable final K input) {
        return null;
    }
}
