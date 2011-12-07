package no.ums.map.tiled;

import java.awt.Dimension;
import java.awt.Image;

/**
 * A tile lookup is responsible for finding the correct map tiles to
 * use for a given image viewing an area.
 *
 * @author Ståle Undheim <su@ums.no>
 */
public interface TileLookup {

    interface BoundsMatch {
        int getZoom();
        LonLat getTopLeft();
        LonLat getBottomRight();
    }

    /**
     *
     *
     * @param zoom zoom level to get
     * @param topLeft what position the top left corner should be at.
     * @param size of the target pixel view
     * @return tile info to render the world view at the position at the given size
     */
    TileInfo getTileInfo(int zoom, LonLat topLeft, Dimension size);

    BoundsMatch getBestMatch(LonLat topLeft, LonLat bottomRight, Dimension size);

    ZoomLookup getZoomLookup(int zoom);

    Image getImageFast(TileData tileInfo);

    Image getImage(TileData tileInfo);

    boolean exists(TileData tileInfo);

}
