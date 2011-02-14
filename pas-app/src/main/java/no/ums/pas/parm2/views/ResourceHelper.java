package no.ums.pas.parm2.views;

import no.ums.log.Log;
import no.ums.log.UmsLog;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class ResourceHelper {

    private static final Log log = UmsLog.getLogger(ResourceHelper.class);


    public static Icon getIcon(final long categoryPk, final int width, final int height) {
        BufferedImage before;
        try {
            URL iconUrl = ResourceHelper.class.getResource(String.format("/no/ums/pas/icons/cat%d.gif", categoryPk));
            if (iconUrl == null) {
                return null;
            }
            before = ImageIO.read(iconUrl);
        } catch (IOException e) {
            log.warn("Failed to load image for category %d.", categoryPk, e);
            return null;
        }

        BufferedImage icon = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = icon.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(before, 0, 0, width, height, 0, 0, before.getWidth(), before.getHeight(), null);
        g.dispose();

        return new ImageIcon(icon);
    }
}
