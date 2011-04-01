package no.ums.pas.icons;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.PixelGrabber;
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
    
    public static ImageIcon makeGrayscale(String url) {
    	return makeGrayscale(getIcon(url));
    }
    
    
	public static ImageIcon makeGrayscale(ImageIcon icon)
	{
		BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics gi = bufferedImage.getGraphics();

		Image source = icon.getImage();
		PixelGrabber pgsource = new PixelGrabber(source, 0, 0, source.getWidth(null), source.getHeight(null), true);
		try
		{
			if(pgsource.grabPixels())
			{
				int [] pixels = (int[])pgsource.getPixels();
				for(int x=0; x < source.getWidth(null); x++)
				{
					for(int y=0; y < source.getHeight(null); y++)
					{
						int rgb = pixels[x + y * source.getWidth(null)];
						int luma = (((int)rgb & 0xFF) << 24) | //alpha
			            			(((int)rgb & 0xFF) << 16) | //red
			            			(((int)rgb & 0xFF) << 8)  | //green
			            			(((int)rgb & 0xFF) << 0); //blue
						bufferedImage.setRGB(x, y, rgb);
					}
				}
			}
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		//g2d.drawImage(icon.getImage(), 0, 0, new Color(255,255,255,0), null);
		gi.dispose();
		
		
		BufferedImageOp op = new ColorConvertOp(
			       ColorSpace.getInstance(ColorSpace.CS_GRAY), null); 
		BufferedImage gray = op.filter(bufferedImage, null);
		if(gray!=null)
			return new ImageIcon(gray);

		return new ImageIcon(bufferedImage);			
	}

}
