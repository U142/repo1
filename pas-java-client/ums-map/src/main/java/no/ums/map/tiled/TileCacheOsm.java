package no.ums.map.tiled;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public final class TileCacheOsm extends AbstractTileCacheUri {

    private static final int MAX_ZOOM = 18;
    private static final int TILE_SIZE = 256;
    private static final String HOST_NAMES = "abc";

    // OSM http://a.tah.openstreetmap.org/Tiles/tile/6/20/39.png
    // MapNik: http://a.tah.openstreetmap.org/Tiles/tile/6/20/39.png

    public static enum Layer {
        OSM(".tah.openstreetmap.org", "/Tiles/tile/%d/%d/%d.png"),
        MAPNIK(".tile.openstreetmap.org", "/%d/%d/%d.png");

        private final String hostPostfix;
        private final String pathFormat;

        Layer(final String hostPostfix, final String pathFormat) {
            this.hostPostfix = hostPostfix;
            this.pathFormat = pathFormat;
        }
    }

    private final Layer layer;

    public TileCacheOsm(final Layer layer) {
        super(MAX_ZOOM, TILE_SIZE);
        this.layer = layer;
    }

    @Override
    protected URI createUri(final int zoom, final int row, final int column) {
        try {
            java.util.Random r = new Random();

            final String host = HOST_NAMES.charAt((int) (HOST_NAMES.length() * r.nextDouble())) + layer.hostPostfix;
            return new URI("http", host, String.format(layer.pathFormat, zoom, column, row), null);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to create URI", e);
        }
    }
}
