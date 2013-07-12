package no.ums.map.tiled;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public final class ZoomLookupGeodata implements  ZoomLookup {

    private final int zoomLevel;
    private final int tileSize;

    // Zoom resolutions for GeoData maps
    private final double[] zoomResolution;
    private final double xOrigin = -180;
    private final double yOrigin = 90;

    private final double minX = -29.999990555234554;
    private final double minY = 25.028721811898468;
    private final double maxX = 59.9999955900742;
    private final double maxY = 89.99999104336159;

    public ZoomLookupGeodata(final int zoomLevel, final int tileSize) {
        this.zoomLevel = zoomLevel;
        this.tileSize = tileSize;

        // Zoom reolutions for GeoData mas (should be collected from JSON)
        zoomResolution = new double[] {
                0, // To compensate for array starting on zoom level 0 which doesn't really exist in the zoom resolution table
                0.29743262572878504,
                0.15228550437313793,
                0.07614275218656896,
                0.03807137609328448,
                0.01903568804664224,
                0.00951784402332112,
                0.00475892201166056,
                0.00237946100583028,
                0.00118973050291514,
                5.9486525145757E-4,
                2.97432625728785E-4,
                1.5228550437313792E-4,
                7.614275218656896E-5,
                3.807137609328448E-5,
                1.903568804664224E-5,
                9.51784402332112E-6,
                4.75892201166056E-6
        };
    }

    public Point getTile(final LonLat lonLat) {
        final Point p1 = getPoint(lonLat);
        return new Point((p1.x / tileSize), p1.y / tileSize);
    }

    public List<Point> getTiles(final LonLat topLeft, final LonLat bottomRight) {
        final List<Point> tiles = new ArrayList<Point>();
        final Point p1 = getPoint(topLeft);
        final Point p2 = getPoint(bottomRight);

        final Point sp1 = new Point(p1.x - p1.x % tileSize, p1.y - p1.y % tileSize);
        final Point sp2 = new Point(p2.x - p2.x % tileSize + tileSize, p2.y - p2.y % tileSize + tileSize);

        double resolution = zoomResolution[zoomLevel];

        int minXPos = (int)Math.round((minX - xOrigin) / resolution);
        int minYPos = (int)Math.round((yOrigin - maxY) / resolution);
        int maxXPos = (int)Math.round((maxX - xOrigin) / resolution);
        int maxYPos = (int)Math.round((yOrigin - minY) / resolution);

        // make sure max x and y matches tiles
        maxXPos = maxXPos - maxXPos % tileSize + tileSize;
        maxYPos = maxYPos - maxYPos % tileSize + tileSize;

        for (int y = Math.max(minYPos, sp1.y); y < Math.min(maxYPos, sp2.y); y += tileSize) {
            for (int x = Math.max(minXPos, sp1.x); x < Math.min(maxXPos, sp2.x); x += tileSize) {
                tiles.add(new Point((x / tileSize), y / tileSize));
            }
        }
        return tiles;
    }

    public LonLat getTopLeft(final int row, final int column) {
        return getLonLat(column * tileSize, row * tileSize);
    }

    public LonLat getBottomRight(final int row, final int column) {
        return getLonLat(column * tileSize + tileSize, row * tileSize + tileSize);
    }


    public Point getPoint(final LonLat ll) {
        double tileResolution = zoomResolution[zoomLevel];

        int xPos = (int) Math.round((ll.getLon() - xOrigin) / (tileResolution));
        int yPos = (int) Math.round((yOrigin - ll.getLat()) / (tileResolution));

        return new Point(xPos, yPos);
    }

    public Point getScreenPoint(LonLat topLeftLL, LonLat ll) {
        Point topLeft = getPoint(topLeftLL);
        Point point = getPoint(ll);
        return new Point(point.x - topLeft.x, point.y - topLeft.y);
    }

    public int getZoomLevel() {
        return zoomLevel;
    }

    public LonLat getLonLat(final LonLat topLeft, final int x, final int y) {
        final Point point = getPoint(topLeft);
        return getLonLat(point.x + x, point.y + y);
    }

    public LonLat getLonLat(final int x, final int y) {
        double tileResolution = zoomResolution[zoomLevel];

        double lon = x * tileResolution + xOrigin;
        double lat = - (y * tileResolution - yOrigin);

        return new LonLat(lon, lat);

    }

}
