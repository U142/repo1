package no.ums.map.tiled;

/**
* @author Ståle Undheim <su@ums.no>
*/
public final class TileCell {
    private final int zoom;
    private final int column;
    private final int row;

    public TileCell(int zoom, int row, int column) {
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

        TileCell cell = (TileCell) o;

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
