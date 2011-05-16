package no.ums.map.tiled;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class TileLookupImpl implements TileLookup {

    private final AbstractTileCache tileCache;

    public TileLookupImpl(AbstractTileCache tileCache) {
        this.tileCache = tileCache;
    }

    @Override
    public TileInfo getTileInfo(int zoom, LonLat topLeft, Dimension size) {
        final ZoomLookup zoomLookup = getZoomLookup(zoom);
//        final MapBounds finalBounds = zoomFactory.resolveBounds(bounds, size);

        final Point origin = zoomLookup.getPoint(topLeft);
        final LonLat bottomRight = zoomLookup.getLonLat(origin.x+size.width, origin.y+size.height);

        final List<TileData> tiles = new ArrayList<TileData>();
        final Point p = zoomLookup.getPoint(topLeft);

        final int xPos = p.x;
        final int yPos = p.y;

        for (Point point : zoomLookup.getTiles(topLeft, bottomRight)) {
            final int xOffset = point.x * tileCache.getTileSize() - xPos;
            final int yOffset = point.y * tileCache.getTileSize() - yPos;
            tiles.add(new TileData(zoomLookup.getZoomLevel(), point.y, point.x, xOffset, yOffset, tileCache.getTileSize(), tileCache.getTileSize()));
        }

        return new TileInfo(tiles);
    }

    @Override
    public BoundsMatch getBestMatch(LonLat topLeft, LonLat bottomRight, Dimension size) {
        return tileCache.getBestMatch(topLeft, bottomRight, size);
    }

    @Override
    public ZoomLookup getZoomLookup(int zoom) {
        return tileCache.getZoomLookup(zoom);
    }

    @Override
    public Image getImageFast(TileData data) {
        return getImage(data, 0, 1);
    }

    private Image getImage(TileData data, int zoomAdjust, int amplify) {
        final int zoom = data.getZoom();
        final int row = data.getRow();
        final int column = data.getColumn();
        final int tileSize = tileCache.getTileSize();
        if (amplify == tileSize || zoom - zoomAdjust == 0) {
            return new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
        }
        else if (tileCache.exists(zoom - zoomAdjust, row / amplify, column / amplify)) {
            final Image image = tileCache.render(zoom - zoomAdjust, row / amplify, column / amplify);
            if (zoomAdjust == 0 && amplify == 1) {
                return image;
            }
            BufferedImage result = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_RGB);
            final int sSize = tileSize / amplify;
            final int sx = (column % amplify) * sSize;
            final int sy = (row % amplify) * sSize;
            result.getGraphics().drawImage(image, 0, 0, tileSize, tileSize, sx, sy, sx + sSize, sy + sSize, null);
            return result;
        } else {
            return getImage(data, zoomAdjust+1, amplify*2);
        }
    }

    @Override
    public Image getImage(TileData data) {
        return tileCache.render(data);
    }

    @Override
    public boolean exists(TileData data) {
        return tileCache.exists(data);
    }
}