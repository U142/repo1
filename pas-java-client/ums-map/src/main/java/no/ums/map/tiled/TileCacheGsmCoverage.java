package no.ums.map.tiled;

import com.google.common.collect.MapMaker;
import no.ums.pas.cache.Cache;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public final class TileCacheGsmCoverage extends AbstractLayerTileCache {

    private static final String LAYER = "1";

    private static final ConcurrentMap<String, TileCacheGsmCoverage> GSM_COVERAGE = new MapMaker()
            .softValues()
            .makeComputingMap(new Cache<String, TileCacheGsmCoverage>() {
                @Override
                public TileCacheGsmCoverage apply(@Nullable final String input) {
                    return new TileCacheGsmCoverage(input);
                }
            });
    private static final int RED_SHIFT = 16;
    private static final int BYTE_SHIFT = 0xff;
    private static final int ALPHA_SHIFT = 24;
    private static final int GREEN_SHIFT = 8;
    private static final int ALPHA_VALUE = 190;
    private static final double RED_WEIGHT = 0.3;
    private static final double GREEN_WEIGHT = 0.59;
    private static final double BLUE_WEIGHT = 0.11;

    public static TileCacheGsmCoverage of(final String jobId) {
        return GSM_COVERAGE.get(jobId);
    }

    public static void clearCache() {
        for (TileCacheGsmCoverage tileCacheGsmCoverage : GSM_COVERAGE.values()) {
            tileCacheGsmCoverage.clear();
        }
    }

    public TileCacheGsmCoverage(final String jobId) {
        super(jobId, LAYER);
    }

    @Override
    protected void markFilled(final BufferedImage bi, final int x, final int y, final int rgbBase) {
        final int red = ((rgbBase >> RED_SHIFT) & BYTE_SHIFT);
        final int green = ((rgbBase >> GREEN_SHIFT) & BYTE_SHIFT);
        final int blue = ((rgbBase) & BYTE_SHIFT);
        final int luma = (int) (red * RED_WEIGHT + green * GREEN_WEIGHT + blue * BLUE_WEIGHT);
        final int baseColor = (ALPHA_VALUE << ALPHA_SHIFT) | (luma << RED_SHIFT) | (luma << GREEN_SHIFT) | luma;
        bi.setRGB(x, y, baseColor);
    }

    @Override
    protected void markOutline(final BufferedImage image, final int x, final int y) {
        image.setRGB(x, y, (255 << 24));
    }

}
