package no.ums.map.tiled;

import com.google.common.io.Resources;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public abstract class AbstractTileCacheUri extends AbstractTileCache {

    public AbstractTileCacheUri(int maxZoom, int tileSize) {
        super(maxZoom, tileSize);
    }

    @Override
    protected Image getImage(TileCell input) {
        final URI uri = createUri(input.getZoom(), input.getRow(), input.getColumn());
        try {
            final URL url = uri.toURL();
            final byte[] img = Resources.toByteArray(url);
            Image image = ImageIO.read(new ByteArrayInputStream(img));
            if(image==null)
            	throw new NullPointerException("No image retrieved from " + uri);
            return image;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to download from " + uri, e);
        }
    }

    protected abstract URI createUri(int zoom, int row, int column);


}
