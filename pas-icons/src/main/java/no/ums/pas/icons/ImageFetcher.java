package no.ums.pas.icons;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Cursor;
import java.awt.Dimension;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.PixelGrabber;
import java.net.URL;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class ImageFetcher {

    public static Image getImage(String name) {
        return Toolkit.getDefaultToolkit().getImage(getImageUrl(name));
    }
    
    public static ImageIcon getImageWithBorder(String name, int bordersize)
    {
    	ImageIcon ico = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getImageUrl(name)));
    	return new ImageIcon(generateBufferedImage(ico, bordersize));
    }

    public static Image getImage(String name, int width, int height) {
        return getImage(name).getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    public static Cursor createCursor(String name, int width, int height, Point focusPoint) {
        return Toolkit.getDefaultToolkit().createCustomCursor(getImage(name, width, height), focusPoint, name);
    }

    public static Cursor createCursor(String name, Point focusPoint) {
        return Toolkit.getDefaultToolkit().createCustomCursor(getImage(name), focusPoint, name);
    }

    public static ImageIcon getIcon(String name) {
        return new ImageIcon(getImage(name));
    }

    public static ImageIcon getIcon(String name, int width, int height) {
        return new ImageIcon(getImage(name, width, height));
    }

    public static Point getToolkitSafePoint(Point point) {
        final Dimension dim = Toolkit.getDefaultToolkit().getBestCursorSize(10, 10);
        return null;
    }

    public static URL getImageUrl(String name) {
        URL resource = ImageFetcher.class.getResource(name);
        if (resource == null) {
            throw new NullPointerException("Could not find an image resource named "+name);
        }
        return resource;
    }
    
    public static ImageIcon makeGrayscale(String url) {
    	return makeGrayscale(getIcon(url));
    }

    public static ImageIcon makeBlurred(ImageIcon icon) {
        int border = 1;
        BufferedImage bufferedImage = generateBufferedImage(icon, border);
        int distort1 = 2;
        float distort2 = distort1 * 3;
        Kernel kernel = new Kernel(distort1, distort1, new float[]{
                1f / distort2, 1f / distort2, 1f / distort2,
                1f / distort2, 1f / distort2, 1f / distort2,
                1f / distort2, 1f / distort2, 1f / distort2
        });
        BufferedImageOp op = new ConvolveOp(kernel);
        bufferedImage = op.filter(bufferedImage, null);
        ImageIcon img_w_border = new ImageIcon(bufferedImage);
        if (border <= 0)
            return img_w_border;
        return new ImageIcon(img_w_border.getImage().getScaledInstance(img_w_border.getIconWidth() - border * 2, img_w_border.getIconHeight() - border * 2, Image.SCALE_SMOOTH));
    }


    public static ImageIcon makeGrayscale(ImageIcon icon) {
        BufferedImage bufferedImage = generateBufferedImage(icon, 0);
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, Boolean.TRUE);
        BufferedImageOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), hints);
        BufferedImage gray = op.filter(bufferedImage, null);
        if (gray != null)
            return new ImageIcon(gray);

        return new ImageIcon(bufferedImage);
    }


    private static BufferedImage generateBufferedImage(ImageIcon icon, int add_border) {
        BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth() + add_border * 2, icon.getIconHeight() + add_border * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics gi = bufferedImage.getGraphics();

        Image source = icon.getImage();
        PixelGrabber pgsource = new PixelGrabber(source, 0, 0, source.getWidth(null), source.getHeight(null), true);
        try {
            if (pgsource.grabPixels()) {
                int[] pixels = (int[]) pgsource.getPixels();
                //copy/paste image
                bufferedImage.setRGB(add_border, add_border, source.getWidth(null), source.getHeight(null), pixels, 0, source.getWidth(null));
            }
        } catch (InterruptedException e) {
            // Mark thread interrupted - Java Specialist issue 146 http://www.javaspecialists.eu/archive/Issue146.html
            Thread.currentThread().interrupt();
        }
        gi.dispose();
        return bufferedImage;
    }

}
