package no.ums.map.tiled.component;

import no.ums.map.tiled.LonLat;
import no.ums.map.tiled.TileLookup;
import no.ums.map.tiled.ZoomLookup;

import java.awt.Dimension;
import java.awt.Point;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class MapController implements MapComponent.Controller {

    @Override
    public void onZoomIn(MapModel model, TileLookup tileLookup, Dimension size, Point point) {
        onZoom(model, tileLookup, point, 1);
    }

    @Override
    public void onZoomOut(MapModel model, TileLookup tileLookup, Dimension size, Point point) {
        onZoom(model, tileLookup, point, -1);
    }

    private void onZoom(MapModel model, TileLookup tileLookup, Point point, int delta) {
        final ZoomLookup zoomLookup = tileLookup.getZoomLookup(model.getZoom());
        final LonLat lonLat = zoomLookup.getLonLat(model.getTopLeft(), point.x, point.y);
        final ZoomLookup level = tileLookup.getZoomLookup(model.getZoom()+delta);
        if(level.getZoomLevel()<=1)
        	return;
        final Point centerAbs = level.getPoint(lonLat);
        final LonLat ll1 = level.getLonLat(centerAbs.x - point.x, centerAbs.y - point.y);
        model.setTopLeft(ll1);
        model.setZoom(level.getZoomLevel());
    }

    @Override
    public void mapDragged(MapModel model, TileLookup tileLookup, Dimension size, int xOffset, int yOffset) {
        final ZoomLookup zoomLookup = tileLookup.getZoomLookup(model.getZoom());
        final LonLat lonLat = zoomLookup.getLonLat(model.getTopLeft(), xOffset * -1, yOffset * -1);
        model.setTopLeft(lonLat);
    }

}
