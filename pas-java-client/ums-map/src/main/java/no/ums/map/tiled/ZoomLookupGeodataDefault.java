package no.ums.map.tiled;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public final class ZoomLookupGeodataDefault implements  ZoomLookup {

    private final int zoomLevel;
    private final int tileSize;

    // Zoom resolutions for GeoData maps
    private final double[] zoomResolution;
    private final double xOrigin = -2500000;
    private final double yOrigin = 9045984;

    private final double minX = -3708422.0276739914;
    private final double minY = 3479849.9391751494;
    private final double maxX = 4766389.58857987;
    private final double maxY = 9997963.94301857;

    private final CoorConverter conv = new CoorConverter();

    public ZoomLookupGeodataDefault(final int zoomLevel, final int tileSize) {
        this.zoomLevel = zoomLevel;
        this.tileSize = tileSize;

        // Zoom reolutions for GeoData mas (should be collected from JSON)
        zoomResolution = new double[] {
                21674.7100160867,
                10837.35500804335,
                5418.677504021675,
                2709.3387520108377,
                1354.6693760054188,
                677.3346880027094,
                338.6673440013547,
                169.33367200067735,
                84.66683600033868,
                42.33341800016934,
                21.16670900008467,
                10.583354500042335,
                5.291677250021167,
                2.6458386250105836,
                1.3229193125052918,
                0.6614596562526459,
                0.33072982812632296,
                0.16536491406316148
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

        // Convert from lon lat to map point
        CoorConverter.UTMCoor xy = conv.LL2UTM(23, ll.getLat(), ll.getLon(), "33N");

        int xPos = (int) Math.round((xy.getEasting() - xOrigin) / (tileResolution));
        int yPos = (int) Math.round((yOrigin - xy.getNorthing()) / (tileResolution));

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

        // Convert to UTM
        double easting = x * tileResolution + xOrigin;
        double northing = - (y * tileResolution - yOrigin);

        // convert from UTM to lon lat
        CoorConverter.LLCoor ll = conv.UTM2LL(23, northing, easting, "33N");

        return new LonLat(ll.get_lon(), ll.get_lat());
    }
}
