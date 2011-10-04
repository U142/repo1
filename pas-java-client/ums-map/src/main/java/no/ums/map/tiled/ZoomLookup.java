package no.ums.map.tiled;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class ZoomLookup {

    private static final double DEG_TO_RAD = Math.PI/180;
    private static final double RAD_TO_DEG = 180/Math.PI;

    private final double bc;
    private final double cc;

    private final int zoomLevel;
    private final int maxTile;
    private final int maxPos;
    private final int tileSize;

    public ZoomLookup(int zoomLevel, int tileSize) {
        this.zoomLevel = zoomLevel;
        this.tileSize = tileSize;
        maxTile = (int) Math.pow(2, zoomLevel);
        maxPos = tileSize * maxTile;
        bc = maxPos / 360d;
        cc = maxPos / (2*Math.PI);
    }

    public Point getTile(LonLat lonLat) {
        final Point p1 = getPoint(lonLat);
        return new Point((p1.x / tileSize) % maxTile, p1.y / tileSize);
    }

    public List<Point> getTiles(LonLat topLeft, LonLat bottomRight) {
        final List<Point> tiles = new ArrayList<Point>();
        final Point p1 = getPoint(topLeft);
        final Point p2 = getPoint(bottomRight);

        final Point sp1 = new Point(p1.x - p1.x % tileSize, p1.y - p1.y % tileSize);
        final Point sp2 = new Point(p2.x - p2.x % tileSize + tileSize, p2.y - p2.y % tileSize + tileSize);

        for (int y = Math.max(0, sp1.y); y < Math.min(maxPos, sp2.y); y += tileSize) {
            for (int x = Math.max(0, sp1.x); x < sp2.x; x += tileSize) {
                tiles.add(new Point((x / tileSize) % maxTile, y / tileSize));
            }
        }
        return tiles;
    }

    public LonLat getTopLeft(int row, int column) {
        return getLonLat(column*tileSize, row*tileSize);
    }

    public LonLat getBottomRight(int row, int column) {
    	return getLonLat(column*tileSize+tileSize, row*tileSize+tileSize);
    }


    public Point getPoint(LonLat ll) {
        int e = (int) Math.round(maxPos/2 + ll.getLon() * bc);
        double f = Math.min(Math.max(Math.sin(DEG_TO_RAD * ll.getLat()), -0.9999), 0.9999);
        int g = (int) Math.round(maxPos/2 + 0.5*Math.log((1+f)/(1-f))*-cc);
        return new Point(e,g);
    }

    public int getZoomLevel() {
        return zoomLevel;
    }

    public LonLat getLonLat(LonLat topLeft, int x, int y) {
        final Point point = getPoint(topLeft);
        return getLonLat(point.x + x, point.y + y);
    }

    public LonLat getLonLat(int x, int y) {
        int e = maxPos/2;
        double f = (x - e)/bc;
        double g = (y - e)/-cc;
        double h = RAD_TO_DEG * ( 2 * Math.atan(Math.exp(g)) - 0.5 * Math.PI);
        return new LonLat(f,h);
    }

}
