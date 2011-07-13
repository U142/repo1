package no.ums.map.tiled;

import no.ums.pas.PasApplication;
import no.ums.ws.pas.UMapInfo;
import no.ums.ws.pas.UPASMap;

import java.awt.Image;
import java.awt.Toolkit;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class TileCachePas extends AbstractTileCache {

    private static final int TILE_SIZE = 256;

    public TileCachePas() {
        super(18, TILE_SIZE);
    }

    @Override
    protected Image getImage(TileCell input) {
        // https://api.fleximap.com/servlet/FlexiMap?
                // OID=UMS_TEST&
                // UID=UMS&
                // UPA=MSG&
                // OP=drawarea&&
                // RBO=59.8889368967658&
                // LBO=59.5343180010956&
                // BBO=5.625&
                // TBO=6.328125&
                // IW=256&
                // IH=256&
                // IT=0&
                // IF=3&
                // IP=1&
                // PL=By

                UMapInfo info = new UMapInfo();
        final ZoomLookup zoomLookup = getZoomLookup(input.getZoom());
                final LonLat ll1 = zoomLookup.getLonLat(input.getColumn() * TILE_SIZE, input.getRow() * TILE_SIZE);
                final LonLat ll2 = zoomLookup.getLonLat(input.getColumn() * TILE_SIZE + TILE_SIZE, input.getRow() * TILE_SIZE + TILE_SIZE);
                info.setLBo(ll1.getLon());
                info.setRBo(ll2.getLon());
                info.setUBo(ll2.getLat());
                info.setBBo(ll1.getLat());
                info.setHeight(TILE_SIZE);
                info.setWidth(TILE_SIZE);
                info.setPortrayal("By");

                final UPASMap map = PasApplication.getInstance().getPaswsSoap().getMap(info);

                return Toolkit.getDefaultToolkit().createImage(map.getImage());
    }
}
