package no.ums.map.tiled.component;

import no.ums.map.tiled.LonLat;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;

/**
 * @author Svein Anfinnsen <sa@ums.no>
 */
public class MapTools {

    public static Polygon createPolygon(Path2D.Double path, java.util.List<LonLat> shape) {
        try {
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
        } catch (Exception e) {
            return null;
        }
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

    // find entry and exit point
    // try one and get lon lats for all points also sum distance
    // try the other way and sum distance
    // Compare distance and return shortest

    public static java.util.List<LonLat> getShapeShortestRoute(java.util.List<LonLat> shape, LonLat start, LonLat finish) {
        double distance = 0;
        java.util.List<LonLat> route = new ArrayList<LonLat>();
        boolean distanceFound = false;

        int startIndex = shape.indexOf(start);
        int finishIndex = shape.indexOf(finish);


        for(int i=startIndex;i<shape.size();i++) {
            LonLat next = null;
            LonLat current = shape.get(i);

            route.add(current);

            if(finishIndex == i) {
                distanceFound = true;
                break;
            }

            if(i+1<shape.size()) {
                next = shape.get(i+1);
            }

            if(next != null) {
                distance += current.distanceToInM(next);
            }
        }

        if(startIndex != 0 && !distanceFound) {
            for(int i=0;i<=startIndex;i++) {
                LonLat next = null;
                LonLat current = shape.get(i);

                route.add(current);

                if(finishIndex == i) {
                    distanceFound = true;
                    break;
                }

                if(i+1<startIndex) {
                    next = shape.get(i+1);
                }

                if(next != null) {
                    distance += current.distanceToInM(next);
                }
            }
        }

        // Go the other way
        boolean altDistanceFound = false;
        java.util.List<LonLat> altRoute = new ArrayList<LonLat>();
        double altDistance = 0;

        for(int i=startIndex;i>=0;i--) {
            LonLat next = null;
            LonLat current = shape.get(i);

            altRoute.add(current);

            if(finishIndex == i) {
                altDistanceFound = true;
                break;
            }

            if(i-1>0) {
                next = shape.get(i-1);
            }

            if(next != null) {
                altDistance += current.distanceToInM(next);
            }
        }

        if(startIndex != shape.size() && !altDistanceFound) {
            for(int i=shape.size()-1;i>=0;i--) {
                LonLat next = null;
                LonLat current = shape.get(i);

                altRoute.add(current);

                if(finishIndex == i) {
                    altDistanceFound = true;
                    break;
                }

                if(i-1>=0) {
                    next = shape.get(i-1);
                }

                if(next != null) {
                    altDistance += current.distanceToInM(next);
                }
            }
        }

        if(distance < altDistance) {
            return route;
        }
        else {
            return altRoute;
        }
    }
}
