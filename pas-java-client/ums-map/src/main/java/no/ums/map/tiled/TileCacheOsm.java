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
public class TileCacheOsm extends AbstractTileCacheUri {

    public TileCacheOsm() {
        super(18, 256);
    }

    @Override
    protected URI createUri(int zoom, int column, int row) {
        try {
            final String host = "abc".charAt((int) (3 * Math.random())) + ".tile.openstreetmap.org";
            return new URI("http", host, String.format(Locale.ENGLISH, "/%d/%d/%d.png", zoom, column, row), null);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to create URI", e);
        }
    }
}
