package no.ums.pas.icons;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class ImageFetcher {
    public static Image getImage(String name) {
        return Toolkit.getDefaultToolkit().getImage(getResource(name));
    }

    private static URL getResource(String name) {
        URL resource = ImageFetcher.class.getResource(name);
        if (resource == null) {
            throw new NullPointerException("Could not find an image resource named "+name);
        }
        return resource;
    }

    public static ImageIcon getIcon(String url) {
        return new ImageIcon(getResource(url));
    }
}
