package no.ums.map.tiled;

import com.google.common.collect.MapMaker;
import no.ums.pas.cache.Cache;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class TileCacheGsmCoverage extends AbstractLayerTileCache {

    private static final String LAYER = "1";

    private static final ConcurrentMap<String, TileCacheGsmCoverage> gsmCoverage = new MapMaker()
            .softValues()
            .makeComputingMap(new Cache<String, TileCacheGsmCoverage>() {
                @Override
                public TileCacheGsmCoverage apply(@Nullable String input) {
                    return new TileCacheGsmCoverage(input);
                }
            });

    public static TileCacheGsmCoverage of(String jobId) {
        return gsmCoverage.get(jobId);
    }

    public static void clearCache() {
        for (TileCacheGsmCoverage tileCacheGsmCoverage : gsmCoverage.values()) {
            tileCacheGsmCoverage.clear();
        }
    }

    public TileCacheGsmCoverage(String jobId) {
        super(jobId, LAYER);
    }

    @Override
    protected void markFilled(BufferedImage bi, int x, int y, int rgbBase) {
        final int red = ((rgbBase >> 16) & 0xff);
        final int green = ((rgbBase >> 8) & 0xff);
        final int blue = ((rgbBase) & 0xff);
        final int luma = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
        final int ca = 190;
        final int baseColor = (ca << 24) | (luma << 16) | (luma << 8) | luma;
        bi.setRGB(x, y, baseColor);
    }

    @Override
    protected void markOutline(BufferedImage image, int x, int y) {
        image.setRGB(x, y, (255 << 24));
    }

}
