package no.ums.pas.icons;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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
        return Toolkit.getDefaultToolkit().getImage(getResource(name));
    }
    
    public static ImageIcon getImageWithBorder(String name, int bordersize)
    {
    	ImageIcon ico = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getResource(name)));
    	return new ImageIcon(generateBufferedImage(ico, bordersize));
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
    
    
    public static ImageIcon makeBlurred(ImageIcon icon)
    {
    	int border = 1;
		BufferedImage bufferedImage = generateBufferedImage(icon, border);
		int distort1 = 2;
		float distort2 = distort1 * 3;
		Kernel kernel = new Kernel(distort1, distort1, new float[] {
		        1f/distort2, 1f/distort2, 1f/distort2,
		        1f/distort2, 1f/distort2, 1f/distort2,
		        1f/distort2, 1f/distort2, 1f/distort2
		        });
		BufferedImageOp op = new ConvolveOp(kernel);
		bufferedImage = op.filter(bufferedImage, null);
		ImageIcon img_w_border = new ImageIcon(bufferedImage);
		if(border<=0)
			return img_w_border;
		return new ImageIcon(img_w_border.getImage().getScaledInstance(img_w_border.getIconWidth()-border*2, img_w_border.getIconHeight()-border*2, Image.SCALE_SMOOTH));
    }
    

    
	public static ImageIcon makeGrayscale(ImageIcon icon)
	{
		BufferedImage bufferedImage = generateBufferedImage(icon, 0);
		RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, Boolean.TRUE);
		BufferedImageOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), hints); 
		BufferedImage gray = op.filter(bufferedImage, null);
		if(gray!=null)
			return new ImageIcon(gray);

		return new ImageIcon(bufferedImage);			
	}

	
    private static BufferedImage generateBufferedImage(ImageIcon icon, int add_border)
    {
		BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth() + add_border*2, icon.getIconHeight() + add_border*2, BufferedImage.TYPE_INT_ARGB);
		Graphics gi = bufferedImage.getGraphics();

		Image source = icon.getImage();
		PixelGrabber pgsource = new PixelGrabber(source, 0, 0, source.getWidth(null), source.getHeight(null), true);  
		try
		{
			if(pgsource.grabPixels())
			{
				int [] pixels = (int[])pgsource.getPixels();
				//copy/paste image
				bufferedImage.setRGB(add_border, add_border, source.getWidth(null), source.getHeight(null), pixels, 0, source.getWidth(null));
				
				/*for(int x=0; x < source.getWidth(null); x++)
				{
					for(int y=0; y < source.getHeight(null); y++)
					{
						int rgb = pixels[x + y * source.getWidth(null)];
						int a = (((int)rgb & 0xFF) << 24);
						int r = (((int)rgb & 0xFF) << 16);
						int g = (((int)rgb & 0xFF) << 8);
						int b = (((int)rgb & 0xFF) << 0);
						r /= 1;
						g /= 1;
						b /= 1;
						rgb = a + r + g + b;
						int luma = (((int)rgb & 0xFF) << 24) | //alpha
			            			(((int)rgb & 0xFF) << 16) | //red
			            			(((int)rgb & 0xFF) << 8)  | //green
			            			(((int)rgb & 0xFF) << 0); //blue
						bufferedImage.setRGB(x, y, rgb);
					}
				}*/
			}
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		gi.dispose();
		return bufferedImage;
    }
}
