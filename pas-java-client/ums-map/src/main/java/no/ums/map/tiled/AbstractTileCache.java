package no.ums.map.tiled;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

import javax.annotation.Nullable;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.lang.Math;
import java.lang.Override;
import java.util.Map;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public abstract class AbstractTileCache {

    private final Map<TileCell, Image> cache;

    private final int maxZoom;
    private final ZoomLookup[] zoomLookups;
    private final int tileSize;

    public AbstractTileCache(int maxZoom, int tileSize) {
        this.maxZoom = maxZoom;
        this.tileSize = tileSize;
        zoomLookups = new ZoomLookup[maxZoom+1];
        for (int i = 0; i < zoomLookups.length; i++) {
            zoomLookups[i] = new ZoomLookup(i, tileSize);
        }

        cache = new MapMaker().softValues().makeComputingMap(new Function<TileCell, Image>() {
            @Override
            public Image apply(@Nullable TileCell input) {
                return getImage(input);
            }
        });
    }

    protected abstract Image getImage(TileCell input);

    public int getTileSize() {
        return tileSize;
    }

    public ZoomLookup getZoomLookup(int zoom) {
        return zoomLookups[Math.max(0, Math.min(maxZoom, zoom))];
    }

    public Image render(TileData tile) {
        return render(tile.getZoom(), tile.getRow(), tile.getColumn());
    }

    public Image render(int zoom, int row, int column) {
        return cache.get(new TileCell(zoom, row, column));
    }

    public boolean exists(TileData tile) {
        return exists(tile.getZoom(), tile.getRow(), tile.getColumn());
    }

    public boolean exists(int zoom, int row, int column) {
        return cache.containsKey(new TileCell(zoom, row, column));
    }

    public TileLookup.BoundsMatch getBestMatch(final LonLat topLeft, final LonLat bottomRight, final Dimension size) {
        for (int i = 0; i < zoomLookups.length; i++) {
            final ZoomLookup zoomLookup = zoomLookups[i];
            final Point p1 = zoomLookup.getPoint(topLeft);
            final Point p2 = zoomLookup.getPoint(bottomRight);
            if (Math.abs(p1.x-p2.x) > size.width || Math.abs(p1.y-p2.y) > size.height) {
                return new BoundsMatchImpl(Math.max(0, i-1), topLeft, bottomRight, size);
            }
        }
        return new BoundsMatchImpl(maxZoom, topLeft, bottomRight, size);
    }

    private class BoundsMatchImpl implements TileLookup.BoundsMatch {
        private final int zoom;
        private final LonLat topLeft;
        private final LonLat bottomRight;

        public BoundsMatchImpl(int zoom, LonLat topLeft, final LonLat bottomRight, Dimension size) {
            Point p1 = zoomLookups[zoom].getPoint(topLeft);
            Point p2 = zoomLookups[zoom].getPoint(bottomRight);

            this.zoom = zoom;
            this.topLeft = zoomLookups[zoom].getLonLat((p1.x+ p2.x- size.width ) / 2 , (p1.y+ p2.y- size.height) /  2);
            this.bottomRight = zoomLookups[zoom].getLonLat((p1.x+ p2.x+ size.width ) / 2 , (p1.y+ p2.y+ size.height) /  2);
        }

        @Override
        public int getZoom() {
            return zoom;
        }

        @Override
        public LonLat getTopLeft() {
            return topLeft;
        }

        public LonLat getBottomRight() {
            return bottomRight;
        }
    }
}
