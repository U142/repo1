package no.ums.map.tiled;

import java.util.Locale;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public final class TileData {
    
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    private final int zoom;
    private final int row;
    private final int column;

    public TileData(int zoom, int row, int column, int x, int y, int width, int height) {
        Preconditions.checkElementIndex(column, (int) Math.pow(2, zoom), "column");
        Preconditions.checkElementIndex(row, (int) Math.pow(2, zoom), "row");
        Preconditions.checkArgument(width > 0, "width must be positive");
        Preconditions.checkArgument(height > 0, "height must be positive");
        this.zoom = zoom;
        this.row = row;
        this.column = column;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getZoom() {
        return zoom;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TileData tileData = (TileData) o;

        return height == tileData.height
                && width == tileData.width
                && zoom == tileData.zoom
                && row == tileData.row
                && column == tileData.column
                && x == tileData.x
                && y == tileData.y;

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(zoom, row, column, x, y, width, height);
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "TileData{(z:%d, r: %d, c: %d) (x: %d, y: %d) (%d x %d)}", zoom, row, column, x, y, width, height);
    }
}
