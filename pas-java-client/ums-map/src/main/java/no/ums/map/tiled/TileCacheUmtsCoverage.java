package no.ums.map.tiled;

import java.awt.image.BufferedImage;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class TileCacheUmtsCoverage extends AbstractLayerTileCache {

    private static final String LAYER = "1";

    public TileCacheUmtsCoverage(String jobId) {
        super(jobId, LAYER);
    }

    @Override
    protected void markFilled(BufferedImage bi, int x, int y, int rgbBase) {
        final int red = ((rgbBase >> 16) & 0xff);
        final int green = ((rgbBase >> 8) & 0xff);
        final int blue = ((rgbBase) & 0xff);
        final int ca = 190;
        final int baseColor = (ca << 24) | (blue << 16) | (green << 8) | red;
        bi.setRGB(x, y, baseColor);
    }

    @Override
    protected void markOutline(BufferedImage image, int x, int y) {
        image.setRGB(x, y, (255 << 24)+50);
    }

}
