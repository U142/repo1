package no.ums.map.tiled;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created with IntelliJ IDEA.
 * User: jone-l
 * Date: 25.09.13
 * Time: 13:54
 * To change this template use File | Settings | File Templates.
 */
public class TileCacheArcgis extends AbstractTileCacheUri {

    // info about geodata map (for a generic arcgis map, this could be selected from the JSON)
    private static final int MAX_ZOOM = 18;
    private static final int TILE_SIZE = 256;

    public static enum Layer {
        ArcgisBasis("server.arcgisonline.com", "/ArcGIS/rest/services/World_Street_Map/MapServer/tile/%d/%d/%d", ""),
        ArcgisTopo("server.arcgisonline.com", "/ArcGIS/rest/services/World_Topo_Map/MapServer/tile/%d/%d/%d", ""),
        Geocache("services.geodataonline.no", "/arcgis/rest/services/Geocache_WMAS_WGS84/GeocacheBasis/MapServer/tile/%d/%d/%d", "token=_rcLdtkkHFdW3CEZL8qr5I5VDQPySW6LFebxHSjMhafS9BqCOALBcoTrsesazImsxB_W6qTx2f9Ui4Ih_GlMXA..");

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

    public TileCacheArcgis(final Layer layer) {
        super(MAX_ZOOM, TILE_SIZE, ZoomLookup.ZoomLookupType.Default);
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
