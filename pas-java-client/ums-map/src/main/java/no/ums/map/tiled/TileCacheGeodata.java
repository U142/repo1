package no.ums.map.tiled;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created with IntelliJ IDEA.
 * User: jone-l
 * Date: 24.06.13
 * Time: 13:55
 * To change this template use File | Settings | File Templates.
 */
public final class TileCacheGeodata extends AbstractTileCacheUri {

    // info about geodata map (for a generic arcgis map, this could be selected from the JSON)
    private static final int MAX_ZOOM = 17;
    private static final int TILE_SIZE = 256;

    public static enum Layer {
        GeocacheBasis("customservices.geodataonline.no", "/arcgis/rest/services/UMS/UMSBaseMap/MapServer/tile/%d/%d/%d", "token=_rcLdtkkHFdW3CEZL8qr5AJfDTQ5zirI84AOMXN-Wl9qn-f-c8KhilaUDUsWR3D409bvcT1pK_Sv5RuLyEfyVw..");

        private final String hostPostfix;
        private final String pathFormat;
        private final String query;

        Layer(final String hostPostfix, final String pathFormat, final String query) {
            this.hostPostfix = hostPostfix;
            this.pathFormat = pathFormat;
            this.query = query;
        }
    }

    private final Layer layer;

    public TileCacheGeodata(final Layer layer) {
        super(MAX_ZOOM, TILE_SIZE, ZoomLookup.ZoomLookupType.Geodata);
        this.layer = layer;
    }

    @Override
    protected URI createUri(final int zoom, final int row, final int column) {
        try {
            return new URI("http", null, layer.hostPostfix, -1, String.format(layer.pathFormat, zoom, row, column), layer.query, null);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to create URI", e);
        }
    }
}
