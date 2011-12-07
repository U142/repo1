package no.ums.map.tiled;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PasApplication;
import no.ums.ws.pas.UMapInfoLayerCellVision;
import no.ums.ws.pas.UPASMap;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public abstract class AbstractLayerTileCache extends AbstractTileCache {

    private static final Log log = UmsLog.getLogger(AbstractLayerTileCache.class);

    private static final int TILE_SIZE = 256;
    private static final int MIN_ALPHA = 50;
    private static final int MAX_ZOOM = 18;
    private static final int MIN_IMAGE_SIZE = 100;
    private static final int ALPHA_SHIFT = 24;
    private static final int BYTE_MASK = 0xff;

    private final Image blank = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
    private final String jobId;
    private final String layer;

    public AbstractLayerTileCache(final String jobId, final String layer) {
        super(MAX_ZOOM, TILE_SIZE);
        this.jobId = jobId;
        this.layer = layer;
    }

    @Override
    protected final Image getImage(final TileCell input) {
        final LonLat topLeft = getZoomLookup(input.getZoom()).getTopLeft(input.getRow(), input.getColumn());
        final LonLat bottomRight = getZoomLookup(input.getZoom()).getBottomRight(input.getRow(), input.getColumn());

        UMapInfoLayerCellVision info = new UMapInfoLayerCellVision();
        info.setLBo(topLeft.getLon());
        info.setRBo(bottomRight.getLon());
        info.setUBo(topLeft.getLat());
        info.setBBo(bottomRight.getLat());
        info.setHeight(TILE_SIZE);
        info.setWidth(TILE_SIZE);
        info.setLayers(layer);
        info.setSRS("4326");
        info.setSzJobid(jobId);
        info.setSzUserid("jone");
        info.setSzPassword("jone");
        info.setVersion("1.1.1");
        info.setSzRequest("GetMap");

        log.debug("Requesting cell coverage off area (%.4f %.4f %.4f %.4f) jobId: %s",
                topLeft.getLon(), topLeft.getLat(), bottomRight.getLon(), bottomRight.getLat(), jobId);

        try {
            final UPASMap map = PasApplication.getInstance().getPaswsSoap().getMapOverlay(info);

            if (map.getImage().length < MIN_IMAGE_SIZE) {
                String sz = new String(map.getImage(), 0, map.getImage().length, "iso-8859-1");
                log.warn("Error loading map overlay %s JobID=%s", sz, jobId);
                return blank;
            } else {
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(map.getImage()));
                BufferedImage bi = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = bi.createGraphics();
                g.drawImage(img, 0, 0, TILE_SIZE, TILE_SIZE, 0, 0, img.getWidth(), img.getHeight(), null);

                for (int i = 0; i < bi.getHeight(); i++) {
                    for (int j = 0; j < bi.getWidth(); j++) {
                        int rgb = bi.getRGB(j, i);
                        int alpha = ((rgb >> ALPHA_SHIFT) & BYTE_MASK);

                        //check neighbours
                        if (i > 0 && j > 0 && i < bi.getHeight() - 1 && j < bi.getWidth() - 1 && alpha > MIN_ALPHA) {
                            //this is a visible pixel. if one neighbour is not visible, then outline this pixel
                            if (getA(j - 1, i - 1, bi) < MIN_ALPHA
                                    || getA(j, i - 1, bi) < MIN_ALPHA
                                    || getA(j + 1, i - 1, bi) < MIN_ALPHA

                                    || getA(j - 1, i, bi) < MIN_ALPHA
                                    || getA(j + 1, i, bi) < MIN_ALPHA

                                    || getA(j - 1, i + 1, bi) < MIN_ALPHA
                                    || getA(j, i + 1, bi) < MIN_ALPHA
                                    || getA(j + 1, i + 1, bi) < MIN_ALPHA) {
                                markOutline(bi, j, i);
                            } else if (alpha > MIN_ALPHA) {
                                markFilled(bi, j, i, rgb);
                            }
                        }
                    }
                }
                return bi;
            }
        } catch (Exception e) {
            log.error("Failed to fetch map from server", e);
            return blank;
        }
    }

    protected abstract void markFilled(BufferedImage bi, int x, int y, int rgbBase);

    protected abstract void markOutline(BufferedImage image, int x, int y);

    private int getA(final int x, final int y, final BufferedImage bufferedImage) {
        return ((bufferedImage.getRGB(x, y) >> ALPHA_SHIFT) & BYTE_MASK);
    }

}
