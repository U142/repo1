package no.ums.map.tiled;

import java.awt.Point;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class GoogleProjection {

    private static final double DEG_TO_RAD = Math.PI/180;
    private static final double RAD_TO_DEG = 180/Math.PI;

    private final double[] bc;
    private final double[] cc;
    private final Point[] zc;
    private final int maxZoom;
    private final int tileSize;

    public GoogleProjection(int maxZoom, int size) {
        this.maxZoom = maxZoom;
        this.tileSize = size;
        bc = new double[maxZoom];
        cc = new double[maxZoom];
        zc = new Point[maxZoom];
        int c = size;
        for (int i=0; i< maxZoom; i++) {
            int e = c/2;
            bc[i] = c / 360d;
            cc[i] = c / (2*Math.PI);
            zc[i] = new Point(e, e);
            c *=2;
        }
    }

    public Point toPoint(LonLat ll, int zoom) {
        Point d = zc[zoom];
        int e = (int) Math.round(d.x + ll.getLon() * bc[zoom]);
        double f = minmax(Math.sin(DEG_TO_RAD * ll.getLat()),-0.9999,0.9999);
        int g = (int) Math.round(d.y + 0.5*Math.log((1+f)/(1-f))*-cc[zoom]);
        return new Point(e,g);
    }

    public LonLat toLonLat(Point p, int zoom) {
        Point e = zc[zoom];
        double f = (p.x - e.x)/bc[zoom];
        double g = (p.y - e.y)/-cc[zoom];
        double h = RAD_TO_DEG * ( 2 * Math.atan(Math.exp(g)) - 0.5 * Math.PI);
        return new LonLat(f,h);
    }

    private static double minmax(double value, double min, double max) {
        return Math.min(Math.max(value,min),max);
    }

    public int getMaxZoom() {
        return maxZoom;
    }

    public int getTileSize() {
        return tileSize;
    }
}
