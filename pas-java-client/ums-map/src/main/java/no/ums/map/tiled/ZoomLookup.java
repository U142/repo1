package no.ums.map.tiled;

import java.awt.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jone-l
 * Date: 09.07.13
 * Time: 09:17
 * To change this template use File | Settings | File Templates.
 */
public interface ZoomLookup {

    enum ZoomLookupType {
        Default, Geodata, GeodataDefault
    }

    Point getTile(final LonLat lonLat);

    List<Point> getTiles(final LonLat topLeft, final LonLat bottomRight);

    LonLat getTopLeft(final int row, final int column);

    LonLat getBottomRight(final int row, final int column);


    Point getPoint(final LonLat ll);

    Point getScreenPoint(LonLat topLeftLL, LonLat ll);

    int getZoomLevel();

    LonLat getLonLat(final LonLat topLeft, final int x, final int y);

    LonLat getLonLat(final int x, final int y);
}
