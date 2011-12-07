package no.ums.map.tiled;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public final class TileCacheUmtsCoverage extends AbstractLayerTileCache {

    private static final String LAYER = "1";

    private static final ConcurrentMap<String, TileCacheUmtsCoverage> INSTANCE_CACHE = new MapMaker()
            .softValues()
            .makeComputingMap(new Function<String, TileCacheUmtsCoverage>() {
                @Override
                public TileCacheUmtsCoverage apply(@Nullable final String input) {
                    return new TileCacheUmtsCoverage(input);
                }
            });
    private static final int RED_SHIFT = 16;
    private static final int GREEN_SHIFT = 8;
    private static final int ALPHA_SHIFT = 24;
    private static final int BYTE_MASK = 0xff;
    private static final int ALPHA_VALUE = 190;
    private static final int OUTLINE_COLOR = (255 << ALPHA_SHIFT) + 50;

    public static AbstractTileCache of(final String jobid) {
        return INSTANCE_CACHE.get(jobid);
    }

    public static void clearTileCache() {
        for (TileCacheUmtsCoverage tileCacheUmtsCoverage : INSTANCE_CACHE.values()) {
            tileCacheUmtsCoverage.clear();
        }
    }

    private TileCacheUmtsCoverage(final String jobId) {
        super(jobId, LAYER);
    }

    @Override
    protected void markFilled(final BufferedImage bi, final int x, final int y, final int rgbBase) {
        final int red = ((rgbBase >> RED_SHIFT) & BYTE_MASK);
        final int green = ((rgbBase >> GREEN_SHIFT) & BYTE_MASK);
        final int blue = ((rgbBase) & BYTE_MASK);
        final int baseColor = (ALPHA_VALUE << ALPHA_SHIFT)  | (red << RED_SHIFT) | (green << GREEN_SHIFT) | (blue);
        bi.setRGB(x, y, baseColor);
    }

    @Override
    protected void markOutline(final BufferedImage image, final int x, final int y) {
        image.setRGB(x, y, OUTLINE_COLOR);
    }
}
