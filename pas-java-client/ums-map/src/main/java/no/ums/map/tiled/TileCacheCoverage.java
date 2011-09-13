package no.ums.map.tiled;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PasApplication;
import no.ums.ws.pas.UMapInfoLayerCellVision;
import no.ums.ws.pas.UPASMap;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class TileCacheCoverage extends AbstractTileCache {

    private static final Log log = UmsLog.getLogger(TileCacheCoverage.class);

    private static final int TILE_SIZE = 256;
    private static final String LAYER = "1";
    private static final int MIN_ALPHA = 50;

    private final String jobId;
    private final Image blank = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);

    public TileCacheCoverage(String jobId) {
        super(18, TILE_SIZE);
        this.jobId = jobId;
    }

    @Override
    protected Image getImage(TileCell input) {
        final LonLat topLeft = getZoomLookup(input.getZoom()).getTopLeft(input.getRow(), input.getColumn());
        final LonLat bottomRight = getZoomLookup(input.getZoom()).getBottomRight(input.getRow(), input.getColumn());

        UMapInfoLayerCellVision info = new UMapInfoLayerCellVision();
        info.setLBo(topLeft.getLon());
        info.setRBo(bottomRight.getLon());
        info.setUBo(topLeft.getLat());
        info.setBBo(bottomRight.getLat());
        info.setHeight(TILE_SIZE);
        info.setWidth(TILE_SIZE);
        info.setLayers(LAYER);
        info.setSRS("4326");
        info.setSzJobid(jobId);
        info.setSzUserid("jone");
        info.setSzPassword("jone");
        info.setVersion("1.1.1");
        info.setSzRequest("GetMap");

        try {
            final UPASMap map = PasApplication.getInstance().getPaswsSoap().getMapOverlay(info);

            if (map.getImage().length < 100) {
                String sz = new String(map.getImage(), 0, map.getImage().length, "iso-8859-1");
                log.error("Error loading map overlay %s JobID=%s", sz, jobId, new Exception(sz));
                return blank;
            } else {
                Image img = Toolkit.getDefaultToolkit().createImage(map.getImage());

                BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = bi.createGraphics();
                g.drawImage(img, 0, 0, null);
                g.setComposite(AlphaComposite.Src);
                g.dispose();

                for (int i = 0; i < bi.getHeight(); i++) {
                    for (int j = 0; j < bi.getWidth(); j++) {
                        int rgb = bi.getRGB(j, i);
                        int alpha = ((rgb >> 24) & 0xff);

                        if (i > 0 && j > 0 && i < bi.getHeight() - 1 && j < bi.getWidth() - 1 && alpha > MIN_ALPHA) //check neighbours
                        {
                            //this is a visible pixel. if one neighbour is not visible, then outline this pixel
                            if (getA(j - 1, i - 1, bi) < MIN_ALPHA ||
                                getA(j    , i - 1, bi) < MIN_ALPHA ||
                                getA(j + 1, i - 1, bi) < MIN_ALPHA ||

                                getA(j - 1, i, bi) < MIN_ALPHA ||
                                getA(j + 1, i, bi) < MIN_ALPHA ||

                                getA(j - 1, i + 1, bi) < MIN_ALPHA ||
                                getA(j    , i + 1, bi) < MIN_ALPHA ||
                                getA(j + 1, i + 1, bi) < MIN_ALPHA) {
                                bi.setRGB(j, i, (255 << 24));
                            } else if (alpha > MIN_ALPHA) {
                                int red = ((rgb >> 16) & 0xff);
                                int green = ((rgb >> 8) & 0xff);
                                int blue = ((rgb) & 0xff);
                                int luma = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
                                int ca = 190;
                                int baseColor = (ca << 24) | (luma << 16) | (luma << 8) | luma;
                                bi.setRGB(j, i, baseColor);
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

    private int getA(int x, int y, BufferedImage bufferedImage) {
        return ((bufferedImage.getRGB(x, y) >> 24) & 0xff);
    }

}
