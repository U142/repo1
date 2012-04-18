package no.ums.map.tiled;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

import javax.annotation.Nullable;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.lang.Math;
import java.lang.Override;
import java.util.Map;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public abstract class AbstractTileCache {

    private final Map<TileCell, Image> cache;
    private final HashSet<TileCell> invalidTiles = new HashSet<TileCell>();

    private final int maxZoom;
    private final ZoomLookup[] zoomLookups;
    private final int tileSize;
    
    public static class InvalidImage extends BufferedImage
    {
    	public InvalidImage()
    	{
    		super(1, 1, BufferedImage.TYPE_INT_ARGB);
    	}
    }
    
    public AbstractTileCache(int maxZoom, int tileSize) {
        this.maxZoom = maxZoom;
        this.tileSize = tileSize;
        zoomLookups = new ZoomLookup[maxZoom+1];
        for (int i = 0; i < zoomLookups.length; i++) {
            zoomLookups[i] = new ZoomLookup(i, tileSize);
        }
        
        TimerTask task = new TimerTask() {
			@Override
			public void run() {
				doInvalidImageCleanup();
			}
		};
        new Timer().scheduleAtFixedRate(task, 500, 500);
        cache = new MapMaker()
        		.softValues()
				.makeComputingMap(new Function<TileCell, Image>() {
		            @Override
		            public Image apply(@Nullable TileCell input) {
		                try
		                {
		                	Image img = getImage(input);
		                	if(img==null)
		                		invalidTiles.add(input);
		                	else
		                		invalidTiles.remove(input);
			                return (img == null ? new InvalidImage() : img);
		                }
		                catch(Exception e)
		                {
		                	invalidTiles.add(input);
		                	return new InvalidImage();
		                }
	            }
            
        });
        
    }
    
    
    protected void doInvalidImageCleanup()
    {
    	for(TileCell cell : invalidTiles)
    	{
   			cache.remove(cell);
    	}
    	invalidTiles.clear();
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

    public void clear() {
        cache.clear();
    }
    
    /**
     * If an alternate coordinate system is used, here's the place to reproject the image.
     * @param img The downloaded image
     * @param input The TileCell parameters for the image
     * @return modified image
     */
    public Image applyImageFilter(final Image img, final TileCell input)
    {
    	return img;
    }

    public TileLookup.BoundsMatch getBestMatch(final LonLat topLeft, final LonLat bottomRight, final Dimension size, int maxZoomLevel) {
        for (int i = 0; i < zoomLookups.length; i++) {
            final ZoomLookup zoomLookup = zoomLookups[i];
            final Point p1 = zoomLookup.getPoint(topLeft);
            final Point p2 = zoomLookup.getPoint(bottomRight);
            if ((Math.abs(p1.x-p2.x) > size.width || Math.abs(p1.y-p2.y) > size.height) || i > maxZoomLevel) {
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
