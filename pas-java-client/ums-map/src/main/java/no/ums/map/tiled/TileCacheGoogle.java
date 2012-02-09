package no.ums.map.tiled;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public final class TileCacheGoogle extends AbstractTileCacheUri {


    private static final String SERVER_CHARS = "0123";
    private static final int MAX_ZOOM = 18;
    private static final int TILE_SIZE = 256;

    public TileCacheGoogle() {
        super(MAX_ZOOM, TILE_SIZE);
    }

    protected URI createUri(final int zoom, final int row, final int column) {
        try {
            java.util.Random r = new Random();
            final String host =
                    "mt" + (SERVER_CHARS.charAt((int) (SERVER_CHARS.length() * r.nextDouble()))) + ".google.com";
            final String path = String.format("/vt/lyrs=m@152000000&hl=no&x=%d&y=%d&z=%d&s=", column, row, zoom);
            return new URI("http", host, path, null);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to create URI", e);
        }
    }

}
