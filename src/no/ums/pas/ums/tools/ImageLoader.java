package no.ums.pas.ums.tools;

import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import no.ums.pas.ums.errorhandling.Error;



public class ImageLoader {
	/*
	 * load external resources
	 */
	public static ClassLoader m_cl = null; //Toolkit.getDefaultToolkit().getClass().getClassLoader();
	public static void setClassLoader(ClassLoader cl) { m_cl = cl; }
	
	public static ImageIcon load_icon(String sz_site, String sz_dir, String sz_filename) {
		//m_cl = Toolkit.getDefaultToolkit().getClass().getClassLoader();
		m_cl = ImageLoader.class.getClassLoader();
		
		URL url_icon = null;
		ImageIcon icon = null;
		String sz_path;
		if(sz_dir.length()==0)
			sz_path = "/images/map/user_objects/";
		else
			sz_path = sz_dir;
		try { 
			url_icon = new URL(sz_site + sz_path + sz_filename);
			icon = new ImageIcon(url_icon);
		} catch(MalformedURLException e) {
			Error.getError().addError("ImageLoader","MalFormedURLException in load_icon",e,1);
		}
		return icon;
	}
	
	public static Icon load_and_scale_icon(String sz_filename, int width, int height)
	{
		ImageIcon icon = load_icon(sz_filename);
		if(icon!=null)
		{
			Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
			return new ImageIcon(img);
		}
		return null;
	}
	/*
	 * load resources from downloaded JAR
	 */
	public static ImageIcon load_icon(String sz_filename) {
		//m_cl = Toolkit.getDefaultToolkit().getClass().getClassLoader();
		
		m_cl = ImageLoader.class.getClassLoader();
		try {
			return new ImageIcon(m_cl.getResource("no/ums/pas/icons/" + sz_filename));
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("ImageLoader","Exception in load_icon (" + sz_filename + ")",e,1);
			return null;
		}
	}
	
	/*
	 * load resources from specified namespace
	 */
	
    public static Image makeTransparent(Image i, float f_alpha){
    	try{
    	    int width=i.getWidth(null);
    	    int height=i.getHeight(null);
    	    int[] pixels=new int[width*height];
    	    PixelGrabber pg=new
    		PixelGrabber(i,0,0,width,height,pixels,0,width);
    	    
    	    if(pg.grabPixels() && ((pg.status() & java.awt.image.ImageObserver.ALLBITS)!=0)){
    		changePixels( pixels, width, height );
    		return java.awt.Toolkit.getDefaultToolkit().createImage(new java.awt.image.MemoryImageSource(width,height,pixels,0,width));
    	    }
    	}catch(InterruptedException e){
    		Error.getError().addError("Error in makeTransparent", "Could not make image transparent", e, Error.SEVERITY_WARNING);
    	}
	    return null;
    }
    public static void changePixels( int pixels[], int width, int height )
    {
		//changing bits
		for(int y=0; y<height; y++){
		    for(int x=0; x<width; x++){
				int pixel=y*width+x;			
				int alpha = (pixels[pixel] >> 24 );
				int red=(pixels[pixel] & 0x00ff0000)>>16;
				int green=(pixels[pixel] & 0x0000ff00)>>8;
				int blue=pixels[pixel] & 0x000000ff;
		
			    float f_alpha = 0.1f; 
			    alpha = (int)(f_alpha*256);
	 		    pixels[pixel]= alpha <<24 | red<<16 |
	 			green<<8 | blue;
		    }
		}
    }
}