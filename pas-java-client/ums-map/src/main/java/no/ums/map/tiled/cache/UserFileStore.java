package no.ums.map.tiled.cache;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.MapMaker;
import com.google.common.io.Resources;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public final class UserFileStore implements ImageStore {

    private static final int ONE_K = 1024;
    private static final int HIGH_BIT_MASK = 0xF0;
    private static final int LOW_BIT_MASK = 0x0F;
    private static final int HIGH_BIT_COUNT = 4;

    private static final class Key {
        private final String hostname;
        private final URI uri;

        private Key(final String hostname, final URI uri) {
            this.hostname = hostname;
            this.uri = uri;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            Key key = (Key) o;

            return Objects.equal(hostname, key.hostname)
                    && Objects.equal(uri.getPath(), key.uri.getPath())
                    && Objects.equal(uri.getQuery(), key.uri.getQuery());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(hostname, uri.getPath(), uri.getQuery());
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("Key");
            sb.append("{hostname='").append(hostname).append('\'');
            sb.append(", uri=").append(uri);
            sb.append('}');
            return sb.toString();
        }
    }

    private final File root;

    private final Map<Key, Image> cache = new MapMaker().softValues().makeComputingMap(new Function<Key, Image>() {
        @Override
        public Image apply(final Key input) {
            try {
                final byte[] img = Resources.toByteArray(input.uri.toURL());
                return ImageIO.read(new ByteArrayInputStream(img));
            } catch (IOException e) {
                throw new IllegalStateException("Failed to download from " + input.uri, e);
            }
        }
    });

    public UserFileStore(final File root) {
        this.root = root;
    }

    public UserFileStore() {
        this(new File(System.getProperty("user.home"), ".urlCache"));
    }

    @Override
    public Image getImage(final String hostBase, final URI uri) {
        final Image image = cache.get(new Key(hostBase, uri));
        System.out.printf("Cache: %d, Free: %dk Total: %dk\n", cache.size(),
                Runtime.getRuntime().freeMemory() / ONE_K,
                Runtime.getRuntime().totalMemory() / ONE_K);
        return image;
    }

    @Override
    public boolean exists(final String hostBase, final URI uri) {
        return cache.containsKey(new Key(hostBase, uri));
    }

    private File getTargetFile(final String hostBase, final URI uri) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(hostBase.getBytes(Charset.forName("utf-8")), 0, hostBase.length());
            md.update(uri.getPath().getBytes(Charset.forName("utf-8")), 0, uri.getPath().length());
            if (uri.getQuery() != null) {
                md.update(uri.getQuery().getBytes(Charset.forName("utf-8")), 0, uri.getQuery().length());
            }
            byte[] sha1hash = md.digest();
            File targetFile = root;
            for (byte b : sha1hash) {
                final char highByteChar = HEX_CHARS.charAt((b & HIGH_BIT_MASK) >> HIGH_BIT_COUNT);
                final char lowByteChar = HEX_CHARS.charAt(b & LOW_BIT_MASK);
                targetFile = new File(targetFile, new String(new char[]{highByteChar, lowByteChar}));
            }
            final String extension = uri.getPath().substring(uri.getPath().lastIndexOf("."));
            return new File(targetFile.getParent(), targetFile.getName() + extension);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 not supported", e);
        }
    }

    static final String HEX_CHARS = "0123456789abcdef";

    public static String getHex(final byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEX_CHARS.charAt((b & HIGH_BIT_MASK) >> HIGH_BIT_COUNT))
                    .append(HEX_CHARS.charAt((b & LOW_BIT_MASK)));
        }
        return hex.toString();
    }
}
