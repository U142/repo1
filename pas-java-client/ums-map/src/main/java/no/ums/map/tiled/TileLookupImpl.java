package no.ums.map.tiled;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public final class TileLookupImpl implements TileLookup {

    private final AbstractTileCache tileCache;

    public TileLookupImpl(final AbstractTileCache tileCache) {
        this.tileCache = tileCache;
    }
    
    @Override
    public void clearAllCache()
    {
    	tileCache.clear();
    }

    @Override
    public TileInfo getTileInfo(final int zoom, final LonLat topLeft, final Dimension size) {
        final ZoomLookup zoomLookup = getZoomLookup(zoom);
//        final MapBounds finalBounds = zoomFactory.resolveBounds(bounds, size);

        final Point origin = zoomLookup.getPoint(topLeft);
        final LonLat bottomRight = zoomLookup.getLonLat(origin.x + size.width, origin.y + size.height);

        final List<TileData> tiles = new ArrayList<TileData>();
        final Point p = zoomLookup.getPoint(topLeft);

        final int xPos = p.x;
        final int yPos = p.y;
        for (Point point : zoomLookup.getTiles(topLeft, bottomRight)) {
            final int tileSize = tileCache.getTileSize();
            final int xOffset = point.x * tileSize - xPos;
            final int yOffset = point.y * tileSize - yPos;
            tiles.add(new TileData(zoomLookup.getZoomLevel(), point.y, point.x, xOffset, yOffset, tileSize, tileSize));
        }

        return new TileInfo(tiles);
    }

    @Override
    public BoundsMatch getBestMatch(final LonLat topLeft, final LonLat bottomRight, final Dimension size) {
        return tileCache.getBestMatch(topLeft, bottomRight, size);
    }

    @Override
    public ZoomLookup getZoomLookup(final int zoom) {
        return tileCache.getZoomLookup(zoom);
    }

    @Override
    public Image getImageFast(final TileData data) {
        return getImageFast(data, 0, 1);
    }

    /**
     * Fetches an image by just examining the cache
     * <p/>
     * This method will attempt to generate the desired image from the cache. If the exact tile
     * exists, it's just. Otherwise we check higher and higher (lower zoom value) zoom levels
     * to see if we can find an image. That image is then cropped and scaled to fit the desired
     * tile that we need.
     *
     * @param data       description off tile to fetch
     * @param zoomAdjust amount to adjust the zoom in the data when looking for the image
     * @param amplify    amount to scale up the cache image
     * @return best match image for the desired tile.
     */
    private Image getImageFast(final TileData data, final int zoomAdjust, final int amplify) {
        final int zoom = data.getZoom();
        final int row = data.getRow();
        final int column = data.getColumn();
        final int tileSize = tileCache.getTileSize();
        if (amplify == tileSize || zoom - zoomAdjust == 0) {
            // We reached max zoom without finding any cached image, return an empty image
            return new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
        } else if (tileCache.exists(zoom - zoomAdjust, row / amplify, column / amplify)) {
            // We have an image for the specified zoom level (which might be a smaller value than
            // the requested zoom).
            // Get the image from the cache
            final Image image = tileCache.render(zoom - zoomAdjust, row / amplify, column / amplify);
            if (zoomAdjust == 0 && amplify == 1) {
                // No amplification, return image as is
                return image;
            }
            // Image needs to be amplified, create a new image to hold the magnified version
            BufferedImage result = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_RGB);
            final int sSize = tileSize / amplify;
            // Calculate offset in the original image to extract and scale the parts needed for this tile
            final int sx = (column % amplify) * sSize;
            final int sy = (row % amplify) * sSize;
            // Draw zoomed in a selected part of the source image
            result.getGraphics().drawImage(image, 0, 0, tileSize, tileSize, sx, sy, sx + sSize, sy + sSize, null);
            return result;
        } else {
            return getImageFast(data, zoomAdjust + 1, amplify * 2);
        }
    }

    @Override
    public Image getImage(final TileData data) {
        return tileCache.render(data);
    }

    @Override
    public boolean exists(final TileData data) {
        return tileCache.exists(data);
    }
}
