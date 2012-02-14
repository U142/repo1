package no.ums.map.tiled.component;

import no.ums.map.tiled.LonLat;
import no.ums.map.tiled.TileLookup;
import no.ums.map.tiled.ZoomLookup;

import java.awt.*;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public final class MapController implements MapComponent.Controller {

    public int zoomLevel = 1;
    
    public void setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
    }
    
    @Override
    public void onZoomIn(final MapModel model, final TileLookup tileLookup, final Dimension size,
                         final Point point) {
        onZoom(model, tileLookup, point, 1);
    }

    @Override
    public void onZoomOut(final MapModel model, final TileLookup tileLookup, final Dimension size,
                          final Point point) {
        onZoom(model, tileLookup, new Point(size.width-point.x/2, size.height-point.y/2), -1);
    }

    private void onZoom(final MapModel model, final TileLookup tileLookup, final Point point, final int delta) {
        final ZoomLookup zoomLookup = tileLookup.getZoomLookup(model.getZoom());
        final LonLat lonLat = zoomLookup.getLonLat(model.getTopLeft(), point.x, point.y);
        final ZoomLookup level = tileLookup.getZoomLookup(model.getZoom() + delta);
        if (level.getZoomLevel() <= zoomLevel) {
            return;
        }
        final Point centerAbs = level.getPoint(lonLat);
        final LonLat ll1 = level.getLonLat(centerAbs.x - point.x, centerAbs.y - point.y);
        model.setTopLeft(ll1);
        model.setZoom(level.getZoomLevel());
    }

    @Override
    public void mapDragged(final MapModel model, final TileLookup tileLookup, final Dimension size,
                           final int xOffset, final int yOffset) {
        final ZoomLookup zoomLookup = tileLookup.getZoomLookup(model.getZoom());
        final LonLat lonLat = zoomLookup.getLonLat(model.getTopLeft(), xOffset * -1, yOffset * -1);
        model.setTopLeft(lonLat);
    }

}
