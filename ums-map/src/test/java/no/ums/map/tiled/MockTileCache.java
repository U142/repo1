package no.ums.map.tiled;

import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class MockTileCache extends AbstractTileCache {

    public MockTileCache() {
        super(18, 256);
    }

    @Override
    protected Image getImage(TileCell input) {
        return new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
    }
}
