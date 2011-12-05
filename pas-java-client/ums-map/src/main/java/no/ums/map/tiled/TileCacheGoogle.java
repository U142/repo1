package no.ums.map.tiled;

import com.google.common.io.Resources;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class TileCacheGoogle extends AbstractTileCacheUri {


    public TileCacheGoogle() {
        super(18, 256);
    }

    protected URI createUri(int zoom, int row, int column) {
        try {
            final String host = "mt" + ("0123".charAt((int) (4 * Math.random()))) + ".google.com";
            return new URI("http", host, String.format(Locale.ENGLISH, "/vt/lyrs=m@152000000&hl=no&x=%d&y=%d&z=%d&s=", column, row, zoom), null);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to create URI", e);
        }
    }

}
