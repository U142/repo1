package no.ums.map.tiled;

import com.google.common.io.Resources;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public abstract class AbstractTileCacheWms extends AbstractTileCacheUri {

    private static final int TILE_SIZE = 256;

    public AbstractTileCacheWms(int maxZoom, int tileSize) {
        super(maxZoom, tileSize);
    }

    public AbstractTileCacheWms() {
        this(18, TILE_SIZE);
    }

    @Override
    protected URI createUri(int zoom, int row, int column) {
        try {
    // http://webatlas.no/wms-UMs?SERVICE=WMS&LAYERS=ortofoto,Navn&FORMAT=image/png&TRANSPARENT=TRUE&REQUEST=GetMap&BBOX=%f,%f,%f,%f&WIDTH=%d&HEIGHT=%d&STYLES=,&SRS=EPSG:4326&VERSION=1.1.1
            final ZoomLookup zoomLookup = getZoomLookup(zoom);
            final LonLat ll1 = zoomLookup.getLonLat(column * TILE_SIZE, row * TILE_SIZE);
            final LonLat ll2 = zoomLookup.getLonLat(column * TILE_SIZE + TILE_SIZE, row * TILE_SIZE + TILE_SIZE);
            String query = String.format(Locale.ENGLISH, "SERVICE=WMS&LAYERS=%s&FORMAT=%s&TRANSPARENT=TRUE&REQUEST=GetMap&BBOX=%f,%f,%f,%f&WIDTH=%d&HEIGHT=%d&STYLES=,&SRS=EPSG:4326&VERSION=%s", getLayers(), getFormat(), ll1.getLon(), ll2.getLat(), ll2.getLon(), ll1.getLat(), TILE_SIZE, TILE_SIZE, getVersion());
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
