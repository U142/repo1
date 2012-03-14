package no.ums.map.tiled.component;

import no.ums.map.tiled.LonLat;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;

/**
 * @author Svein Anfinnsen <sa@ums.no>
 */
public class MapTools {

    public static Polygon createPolygon(Path2D.Double path, java.util.List<LonLat> shape) {
        if(shape.size()>0) {
            PathIterator iterator = path.getPathIterator(new AffineTransform());
            double[] coords = new double[2];
            int[] x = new int[shape.size()+1];
            int[] y = new int[shape.size()+1];
            int i=0;
            while(!iterator.isDone()) {
                iterator.currentSegment(coords);
                try {
                    x[i] = (int)coords[0];
                }
                catch (Exception ex) {
                    System.out.print(ex.getMessage());
                }
                y[i] = (int)coords[1];
                i++;
                iterator.next();
            }
            return new Polygon(x, y, shape.size());
        }
        return null;
    }

    public static int getNumberOfPathPoints(Path2D.Double path) {
        PathIterator pathIterator = path.getPathIterator(new AffineTransform());
        int i=0;
        while(!pathIterator.isDone()) {
            i++;
            pathIterator.next();
        }
        return i;
    }
}
