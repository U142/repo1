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
public final class TileCacheFleximap extends AbstractTileCacheUri {

    private static final int TILE_SIZE = 256;
    private static final int MAX_ZOOM = 18;
    private static final String FLEXIMAP_QUERY = "OID=UMS_TEST&UID=UMS&UPA=MSG&OP=drawarea&&RBO=%f&LBO=%f&BBO=%f&"
            + "TBO=%f&IW=%d&IH=%d&IT=0&IF=3&IP=1&PL=By";

    public TileCacheFleximap() {
        super(MAX_ZOOM, TILE_SIZE);
    }

    @Override
    protected URI createUri(final int zoom, final int row, final int column) {
        try {
            final ZoomLookup zoomLookup = getZoomLookup(zoom);
            final LonLat ll1 = zoomLookup.getLonLat(column * TILE_SIZE, row * TILE_SIZE);
            final LonLat ll2 = zoomLookup.getLonLat(column * TILE_SIZE + TILE_SIZE, row * TILE_SIZE + TILE_SIZE);
            // https://api.fleximap.com/servlet/FlexiMap?
            String query = String.format(Locale.ENGLISH, "OID=UMS_TEST&UID=UMS&UPA=MSG&OP=drawarea&&RBO=%f&LBO=%f&BBO=%f&TBO=%f&IW=%d&IH=%d&IT=0&IF=3&IP=1&PL=By", ll1.getLon(), ll2.getLon(), ll2.getLat(), ll1.getLat(), TILE_SIZE, TILE_SIZE);
            return new URI("http", "api.fleximap.com", "/servlet/FlexiMap", query, null);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to create URI", e);
        }
    }
}
