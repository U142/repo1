package no.ums.map.tiled;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public abstract class AbstractTileCacheWms extends AbstractTileCacheUri {

    private static final int TILE_SIZE = 256;
    private static final String WMS_QUERY = "SERVICE=WMS&LAYERS=%s&FORMAT=%s&TRANSPARENT=TRUE&REQUEST=GetMap&"
            + "BBOX=%f,%f,%f,%f&WIDTH=%d&HEIGHT=%d&STYLES=,&SRS=EPSG:4326&VERSION=%s";
    private static final int MAX_ZOOM = 18;

    public AbstractTileCacheWms(final int maxZoom, final int tileSize) {
        super(maxZoom, tileSize);
    }

    public AbstractTileCacheWms() {
        this(MAX_ZOOM, TILE_SIZE);
    }

    @Override
    protected final URI createUri(final int zoom, final int row, final int column) {
        try {
    // http://webatlas.no/wms-UMs?SERVICE=WMS&LAYERS=ortofoto,Navn&FORMAT=image/png&TRANSPARENT=TRUE&REQUEST=GetMap&
    // BBOX=%f,%f,%f,%f&WIDTH=%d&HEIGHT=%d&STYLES=,&SRS=EPSG:4326&VERSION=1.1.1
            final ZoomLookup zoomLookup = getZoomLookup(zoom);
            final LonLat ll1 = zoomLookup.getLonLat(column * TILE_SIZE, row * TILE_SIZE);
            final LonLat ll2 = zoomLookup.getLonLat(column * TILE_SIZE + TILE_SIZE, row * TILE_SIZE + TILE_SIZE);
            String query = String.format(WMS_QUERY, getLayers(), getFormat(), ll1.getLon(), ll2.getLat(),
                    ll2.getLon(), ll1.getLat(), TILE_SIZE, TILE_SIZE, getVersion());
            return new URI(getScheme(), getHost(), getPath(), query, null);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to create URI", e);
        }
    }

    public abstract String getScheme();

    public abstract String getHost();

    public abstract String getPath();

    public abstract String getVersion();

    public abstract String getFormat();

    public abstract String getLayers();
}
