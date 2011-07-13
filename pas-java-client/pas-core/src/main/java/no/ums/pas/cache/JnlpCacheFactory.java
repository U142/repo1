package no.ums.pas.cache;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class JnlpCacheFactory implements CacheFactory {

    private static class CacheHandlerKey<K, V> {
        private final CacheHandler<K, V> cacheHandler;

        private CacheHandlerKey(@Nonnull final CacheHandler<K, V> cacheHandler) {
            this.cacheHandler = Preconditions.checkNotNull(cacheHandler);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final CacheHandlerKey that = (CacheHandlerKey) o;

            return cacheHandler.getClass().equals(that.cacheHandler.getClass());

        }

        @Override
        public int hashCode() {
            return cacheHandler.getClass().hashCode();
        }
    }

    private final Map<CacheHandlerKey<?, ?>, Cache<?, ?>> caches = new MapMaker().makeComputingMap(new Function<CacheHandlerKey<?, ?>, Cache<?, ?>>() {
        @Override
        @SuppressWarnings("unchecked")
        public Cache<?, ?> apply(@Nullable final CacheHandlerKey<?, ?> input) {
            return new JnlpCache(input.cacheHandler);
        }
    });




    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Cache<K, V> getCache(final CacheHandler<K, V> handler) {
        return (Cache<K, V>) caches.get(new CacheHandlerKey<K, V>(handler));
    }
}
