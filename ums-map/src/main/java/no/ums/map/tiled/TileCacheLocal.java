package no.ums.map.tiled;

import com.google.common.io.ByteStreams;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class TileCacheLocal extends AbstractTileCache {

    private final File base;

    public TileCacheLocal(File base) {
        super(18, 256);
        this.base = base;
    }

    @Override
    protected Image getImage(Cell tile) {
        if (getFile(tile).exists()) {
            return readImage(getFile(tile));
        }
        try {
            final Process tileProcess = new ProcessBuilder("/usr/bin/python",
                    "/storage/osm/mapnik/staale_tiles.py",
                    base.getAbsolutePath() + "/",
                    String.valueOf(tile.getZoom()),
                    String.valueOf(tile.getColumn()),
                    String.valueOf(tile.getRow()))
                    .directory(base.getParentFile())
                    .start();

            ByteStreams.copy(tileProcess.getInputStream(), System.out);
            ByteStreams.copy(tileProcess.getErrorStream(), System.err);
            tileProcess.waitFor();
            return readImage(getFile(tile));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to execute tasks", e);
        } catch (InterruptedException e) {
            // Ignored, cancelled
            return new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        }
    }

    private File getFile(Cell tile) {
        return getFile(base, tile.getZoom(), tile.getRow(), tile.getColumn());
    }

    private static File getFile(File base, int zoom, int row, int column) {
        return new File(new File(new File(base, String.valueOf(zoom)), String.valueOf(column)), row + ".png");
    }

    private BufferedImage readImage(File fn) {
        try {
            return ImageIO.read(fn);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read image from " + fn, e);
        }
    }

}
