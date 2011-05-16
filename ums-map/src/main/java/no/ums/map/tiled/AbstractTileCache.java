package no.ums.map.tiled;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

import javax.annotation.Nullable;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.lang.Math;import java.lang.Object;import java.lang.Override;import java.lang.String;import java.lang.StringBuilder;import java.util.Map;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public abstract class AbstractTileCache {



    public static final class Cell {
        private final int zoom;
        private final int column;
        private final int row;

        public Cell(int zoom, int row, int column) {
            this.zoom = zoom;
            this.column = column;
            this.row = row;
        }

        public int getZoom() {
            return zoom;
        }

        public int getColumn() {
            return column;
        }

        public int getRow() {
            return row;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Cell cell = (Cell) o;

            return column == cell.column && row == cell.row && zoom == cell.zoom;

        }

        @Override
        public int hashCode() {
            return 31 * (31 * zoom + column) + row;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("Cell");
            sb.append("{zoom=").append(zoom);
            sb.append(", column=").append(column);
            sb.append(", row=").append(row);
            sb.append('}');
            return sb.toString();
        }
    }

    private final Map<Cell, Image> cache;

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

        cache = new MapMaker().softValues().makeComputingMap(new Function<Cell, Image>() {
            @Override
            public Image apply(@Nullable Cell input) {
                return getImage(input);
            }
        });
    }

    protected abstract Image getImage(Cell input);

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
        return cache.get(new Cell(zoom, row, column));
    }

    public boolean exists(TileData tile) {
        return exists(tile.getZoom(), tile.getRow(), tile.getColumn());
    }

    public boolean exists(int zoom, int row, int column) {
        return cache.containsKey(new Cell(zoom, row, column));
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
