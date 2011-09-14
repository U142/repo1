package no.ums.map.tiled;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class TileCacheUmtsCoverage extends AbstractLayerTileCache {

    private static final String LAYER = "1";

    private static final ConcurrentMap<String, TileCacheUmtsCoverage> instanceCache = new MapMaker()
            .softValues()
            .makeComputingMap(new Function<String, TileCacheUmtsCoverage>() {
                @Override
                public TileCacheUmtsCoverage apply(@Nullable String input) {
                    return new TileCacheUmtsCoverage(input);
                }
            });

    public static AbstractTileCache of(String jobid) {
        return instanceCache.get(jobid);
    }

    public static void clearTileCache() {
        for (TileCacheUmtsCoverage tileCacheUmtsCoverage : instanceCache.values()) {
            tileCacheUmtsCoverage.clear();
        }
    }

    private TileCacheUmtsCoverage(String jobId) {
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
