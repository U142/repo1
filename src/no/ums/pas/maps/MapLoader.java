package no.ums.pas.maps;

import java.awt.*;
import java.awt.image.BufferedImage;

import no.ums.pas.PAS;
import no.ums.pas.core.variables;
import no.ums.pas.core.dataexchange.*;
import no.ums.pas.core.ws.vars;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.Timeout;
import no.ums.ws.pas.*;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import java.util.*;
import java.util.List;


import org.geotools.data.ows.Layer;
import org.geotools.data.ows.WMSCapabilities;
import org.geotools.data.wms.WMSUtils;
import org.geotools.data.wms.WebMapServer;
import org.geotools.data.wms.response.GetMapResponse;

import java.awt.color.*;
import java.awt.image.*;

public class MapLoader {
	Image m_img_load = null;
	MediaTracker m_mtracker;
	HTTPReq m_httpreq;
	Navigation m_navigation;
	MapFrame m_mappane;
	//LoadingFrame m_loadingframe = null;
	Component m_mapcomponent;
	boolean b_loading_overlay_in_progress = false;
	boolean b_loading_mapimage = false;
	public boolean IsLoadingOverlay() { return b_loading_overlay_in_progress; }
	public boolean IsLoadingMapImage() { return b_loading_mapimage; }
	protected String sz_errormsg = "";
	protected int m_n_wait_for_mediatracker_ms = 5000;
	public String getErrorMsg() {return sz_errormsg; }
	//protected MediaTracker tracker;
	public MediaTracker getMediaTracker() {
		return m_mtracker;
	}
	
	protected void setErrorMsg(String s) { 
		if(s==null)
			s = "Unknown error";
		sz_errormsg = s; 
	}
	
	public WMSCapabilities getCapabilitiesTest()
	{
		return capabilitiestest;
	}

	
	public MapLoader(Component c, HTTPReq http)
	{
		m_mapcomponent = c;
		m_httpreq = http;
		m_mtracker = new MediaTracker(m_mapcomponent);
	}


	HTTPReq get_httpreq()
	{
		return m_httpreq;
	}
	
    static BufferedImage toImage(byte[] pixels, int w, int h) {
        DataBuffer db = new DataBufferByte(pixels, w*h);
        WritableRaster raster = Raster.createInterleavedRaster(db,
            w, h, w, 1, new int[]{0}, null);
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorModel cm = new ComponentColorModel(cs, false, false,
            Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        return new BufferedImage(cm, raster, false, null);
    }	
	Image load_overlay(String sz_jobid, int n_layer, double n_lbo, double n_rbo, double n_ubo, double n_bbo, Dimension dim)
	{
		b_loading_overlay_in_progress = true;
		try
		{
			ObjectFactory of = new ObjectFactory();
			UMapInfoLayerCellVision info = of.createUMapInfoLayerCellVision();
			info.setLBo((float)(n_lbo));
			info.setRBo((float)(n_rbo));
			info.setUBo((float)(n_ubo));
			info.setBBo((float)(n_bbo));
			info.setHeight(dim.height);
			info.setWidth(dim.width);
			info.setLayers(new Integer(n_layer).toString());
			info.setSRS("4326");
			info.setSzJobid(sz_jobid);
			info.setSzUserid("jone");
			info.setSzPassword("jone");
			info.setVersion("1.1.1");
			info.setSzRequest("GetMap");
			
			try
			{
				//URL wsdl = new URL("http://localhost/WS/PAS.asmx?WSDL");
				//URL wsdl = new URL(PAS.get_pas().get_sitename() + "/ExecAlert/WS/PAS.asmx?WSDL");
				QName service =  new QName("http://ums.no/ws/pas/", "pasws");
				UPASMap map;
				//try
				{
					//URL wsdl = new URL("http://81.191.35.194:8088/vb4/Execalert/WS/PAS.asmx?WSDL");
					//Pasws pas = new Pasws(wsdl, service); //new java.net.URL(PAS.get_pas().get_sitename() + "/ExecAlert/WS/PAS.asmx?WSDL"), new javax.xml.namespace.QName("http://ums.no/", "pasws"));
					//map = pas.getPaswsSoap12().getMapOverlay(info);
				}
				//catch(Exception e)
				//{
					try
					{
						//URL wsdl = new URL("http://localhost/WS/PAS.asmx?WSDL");
						URL wsdl = new URL(vars.WSDL_PAS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/PAS.asmx?WSDL");
						Pasws pas = new Pasws(wsdl, service);
						map = pas.getPaswsSoap12().getMapOverlay(info);
						
					}
					catch(Exception err)
					{
						throw err;
					}
				//}
				
				
				//BufferedImage buf = new javax.imageio.ImageIO().
				if(map.getImage().length < 100)
				{
					//ASSUME ERRORTEXT
					String sz = new String(map.getImage(), 0, map.getImage().length, "iso-8859-1");
					Error.getError().addError("Error loading map overlay", sz + "    JobID=" + sz_jobid, new Exception(sz), 2);
					b_loading_overlay_in_progress = false;
					return null;
				}
				else
				{
					Image img =  Toolkit.getDefaultToolkit().createImage(map.getImage());
					//BufferedImage buf = toImage(map.getImage(), dim.width, dim.height);
					//img.flush();
					MediaTracker tracker = new MediaTracker(PAS.get_pas().get_mappane());
					//MediaTracker tracker = m_mtracker;
					tracker.addImage(img, 0);
					try {
						long start = System.currentTimeMillis();
						tracker.waitForID(0, m_n_wait_for_mediatracker_ms);
						//System.out.println("Waited " + (System.currentTimeMillis()-start) + " millisecs for image");
						if (tracker.isErrorAny()) {
							System.out.println("Error loading overlay image ");
							Error.getError().addError(PAS.l("common_error"), "Error loading overlay image", new Exception(), Error.SEVERITY_ERROR);
							b_loading_overlay_in_progress = false;
							return null;
						}
					} catch (Exception ex) { ex.printStackTrace(); }
					
					
					BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
					Graphics2D g = bi.createGraphics();
					
					/*AffineTransform saveXform = g.getTransform();
					AffineTransform at = new AffineTransform();
					at.rotate(Math.toRadians(10));
					AffineTransform toCenterAt = new AffineTransform();
					toCenterAt.concatenate(at);
					toCenterAt.translate(0,0);//img.getWidth(null)/2, img.getHeight(null)/2);
					g.transform(toCenterAt);*/
					
					g.setComposite(AlphaComposite.Src);
					g.drawImage(img, 0, 0, PAS.get_pas().get_drawthread());
					
					//g.setTransform(saveXform);
					
					g.dispose();
					
					for(int i=0; i < bi.getHeight(); i++)
					{
						for(int j=0; j < bi.getWidth(); j++)
						{
						    int rgb = bi.getRGB(j, i);
						    int min_alpha = 50;
	
						    int alpha = ((rgb >> 24) & 0xff);
						    if(i>0 && j>0 && i<bi.getHeight()-1 && j<bi.getWidth()-1 && alpha>min_alpha) //check neighbours
						    {
						    	//this is a visible pixel. if one neighbour is not visible, then outline this pixel
							    //int n_alpha_neighbours [] = new int[4];
						    	if(		GetARGB(j-1, i, bi)[0] < min_alpha ||
						    			GetARGB(j+1, i, bi)[0] < min_alpha ||
						    			GetARGB(j, i-1, bi)[0] < min_alpha ||
						    			GetARGB(j, i+1, bi)[0] < min_alpha ||
						    			GetARGB(j-1, i-1, bi)[0] < min_alpha ||
						    			GetARGB(j-1, i+1, bi)[0] < min_alpha ||
						    			GetARGB(j+1, i-1, bi)[0] < min_alpha ||
						    			GetARGB(j+1, i+1, bi)[0] < min_alpha)
						    	{
						    		if(n_layer==1)
						    			SetARGB(j, i, bi, 255, 0, 0, 0);//GetARGB(j, i, bi)[1], GetARGB(j, i, bi)[2], GetARGB(j, i, bi)[3]);
						    		else
						    			SetARGB(j, i, bi, 255, 0, 0, 50);
						    	}
							    else if(alpha>min_alpha)
							    {
								    int red = ((rgb >> 16) & 0xff); 
								    int green = ((rgb >> 8) & 0xff); 
								    int blue = ((rgb ) & 0xff);
								    //double n_gray = red>>2 + green>>2 + blue>>2;
								    //n_gray = Math.sqrt(n_gray/3);
								    int luma = (int)(red*0.3 + green*0.59+ blue*0.11);
								    int ca = 190;
								    int n_basecolor = 0;
								    switch(n_layer)
								    {
								    case 1: //GSM900/1800
								    	n_basecolor = rgb = (ca << 24) | ((int)luma << 16) | ((int)luma << 8) | (int)luma;
								    	break;
								    case 4: //UMTS
								    	n_basecolor = rgb = (ca << 24) | ((int)blue << 16) | ((int)green << 8) | (int)red;
								    	break;
								    }
								    int cr = 255>>2;
								    int cg = 255>>2;
								    int cb = 255>>2;
								    //rgb = (ca << 24) | ((int)cr << 16) | ((int)cg << 8) | (int)cb;
								    bi.setRGB(j, i, n_basecolor);
							    }
							    
						    }
						    else
						    {
						    	//rgb = (0 << 24) | (255 << 16) | (255 << 8) | 255;
						    	//bi.setRGB(j, i, rgb);
						    }
						}
					}
					b_loading_overlay_in_progress = false;

					return bi;
				}
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
				Error.getError().addError(PAS.l("common_error"), e.toString(), e, 1);
			}

		}
		catch(Exception e)
		{
		}
		b_loading_overlay_in_progress = false;
		return null;
	}
	
	private void SetARGB(int x, int y, BufferedImage bi, int ca, int cr, int cg, int cb)
	{
	    int rgb = (ca << 24) | (cr << 16) | (cg << 8) | cb;
    	bi.setRGB(x, y, rgb);		
	}
	
	private int GetColor(int x, int y, BufferedImage i)
	{
	    int rgb = i.getRGB(x, y);
	    return rgb;
	}
	private int[] GetARGB(int x, int y, BufferedImage i)
	{
		int rgb = GetColor(x,y,i);
		int [] c = new int[4];
	    c[0] = ((rgb >> 24) & 0xff);
	    c[1] = ((rgb >> 16) & 0xff); 
	    c[2] = ((rgb >> 8) & 0xff); 
	    c[3] = ((rgb ) & 0xff);
	    return c;
	}
	WebMapServer wms = null;
	org.geotools.data.wms.request.GetMapRequest request;
	String m_sz_wms_url = "";
	public String getWmsUrl() { return m_sz_wms_url; }
	WMSCapabilities capabilities = null;
	boolean m_wms_server_changed = true;
	java.util.List<Layer> m_selected_layers = new ArrayList<Layer>();
	public List<String> m_wms_formats;
	public WebMapServer wmstest;
	public WMSCapabilities capabilitiestest;
	public boolean testWmsUrl(String sz_url, String usr, char [] pass) throws Exception
	{
		try
		{
			setWmsAuthenticator(usr, pass);
			URL url = new java.net.URL(sz_url);
			wmstest = new WebMapServer(url);
			capabilitiestest = wmstest.getCapabilities();
			// Layer[] layers = WMSUtils.getNamedLayers(capabilitiestest);
			System.out.println(wmstest.getInfo().getTitle());
			System.out.println(wmstest.getInfo().getDescription());
			System.out.println("WMS Version " + capabilitiestest.getVersion());
			//List<Style> styles = capabilitiestest.getLayer().getStyles();
			//System.out.println(styles);
			m_wms_formats = wmstest.getCapabilities().getRequest().getGetMap().getFormats();//capabilitiestest.getRequest().getGetLegendGraphic().getFormats();
			
		}
		catch(Exception e)
		{
			Error.getError().addError(PAS.l("common_error"), "Error receiving WMS capabilities", e, Error.SEVERITY_ERROR);
			throw e;
		}
		finally{
			resetWmsAuthenticator();
		}
		return true;
		
	}
	
	private void setWmsAuthenticator(final String usr, final char[] pass)
	{
		if(usr.length()>0 && pass.length > 0)
		{
			java.net.Authenticator.setDefault(new java.net.Authenticator() {
			    protected java.net.PasswordAuthentication getPasswordAuthentication() {
			      return new java.net.PasswordAuthentication(usr,
			pass);
			    }
			  });		
		}
	}
	
	private void setWmsAuthenticator()
	{
		setWmsAuthenticator(PAS.get_pas().get_settings().getWmsUsername(), PAS.get_pas().get_settings().getWmsPassword().toCharArray());
	}
	private void resetWmsAuthenticator()
	{
		java.net.Authenticator.setDefault(new java.net.Authenticator() {
			protected java.net.PasswordAuthentication getPasswordAuthentication() {
				return new java.net.PasswordAuthentication("",
						"".toCharArray());
			}
		});
	}

	public Image load_map_wms(double n_lbo, double n_rbo, double n_ubo, double n_bbo, Dimension dim, String sz_wms_url)
	{
		try
		{
			setWmsAuthenticator();
		}
		catch(Exception e)
		{
			
		}

		UMapInfo info = new UMapInfo();
		info.setLBo((float)n_lbo);
		info.setRBo((float)n_rbo);
		info.setUBo((float)n_ubo);
		info.setBBo((float)n_bbo);
		info.setHeight(dim.height);
		info.setWidth(dim.width);

		try
		{
			b_loading_mapimage = true;
			boolean b_error = false;
			Image ret = null;
			//WebMapServer wms = new WebMapServer(new java.net.URL("http://services.interactive-instruments.de/xtra/cgi-bin/wms"));//?OID=UMS_TEST&UID=UMS&UPA=MSG&OP=drawarea&LBO=5.6&rBo=5.9&TBO=59.9&bBO=59.5&iW=400&iH=400&IT=Image&FileFormat=1&PL=;M"));
			//WebMapServer wms = new WebMapServer(new java.net.URL("http://www2.demis.nl/mapserver/request.asp"));//?OID=UMS_TEST&UID=UMS&UPA=MSG&OP=drawarea&LBO=5.6&rBo=5.9&TBO=59.9&bBO=59.5&iW=400&iH=400&IT=Image&FileFormat=1&PL=;M"));
			//WebMapServer wms = new WebMapServer(new java.net.URL("http://onearth.jpl.nasa.gov/wms.cgi"));//?OID=UMS_TEST&UID=UMS&UPA=MSG&OP=drawarea&LBO=5.6&rBo=5.9&TBO=59.9&bBO=59.5&iW=400&iH=400&IT=Image&FileFormat=1&PL=;M"));
			//WebMapServer wms = new WebMapServer(new java.net.URL("http://maps2.sgu.se/wmsconnector/com.esri.wms.Esrimap/SGU_Bedrock_geology_Fennoscandian_shield")); 
			//WebMapServer wms = new WebMapServer(new java.net.URL("http://gdsc.nlr.nl/wms/lufo2005"));
			//WebMapServer wms = new WebMapServer(new java.net.URL("http://webservice.nieuwekaart.nl/cgi-bin/nkn"));
			
			//********** http://www.ngu.no/kart/arealis/ ***********
			//http://wms.geonorge.no/skwms1/wms.kartdata2/TI_AG6B2HE?
			//http://wms.geonorge.no/skwms1/wms.topo2
			//http://wms.geonorge.no/skwms1/wms.norgeibilder
			//http://openmetoc.met.no:8080/metoc/metocwms
			//http://onearth.jpl.nasa.gov/wms.cgi
			//http://ortho1.webatlas.no/wms/
			//http://wms.globexplorer.com/gexservlets/wms
			
			//http://www2.demis.nl/WMS/wms.asp
			//WebMapServer wms = new WebMapServer(new java.net.URL(sz_wms_url));
			if(!m_sz_wms_url.equals(sz_wms_url))
			{
				wms = new WebMapServer(new java.net.URL(sz_wms_url));
				wms.getCapabilities().getRequest().getGetMap().setGet(new URL(sz_wms_url));
				//TEMP solution
			}
			request =  wms.createGetMapRequest();
			if(capabilities==null || !m_sz_wms_url.equals(sz_wms_url))
				capabilities = wms.getCapabilities();
			//System.out.println(wms.getInfo().getTitle());
			//System.out.println(wms.getInfo().getDescription());
			//System.out.println("WMS Version " + capabilities.getVersion());
			m_sz_wms_url = sz_wms_url;
			
			 Layer[] layers = WMSUtils.getNamedLayers(capabilities);
			 request.setDimensions(dim.width, dim.height);
			 request.setTransparent(true);
			 request.setSRS("EPSG:4326");
			 //request.setFormat("image/png");
			 request.setFormat(variables.SETTINGS.getSelectedWmsFormat());
			 
			 m_selected_layers.clear(); //remove
			 for(int i=0; i < layers.length; i++) 
			 {
				 if(variables.SETTINGS.getSelectedWmsLayers().contains(layers[i].getName()))
					 m_selected_layers.add(layers[i]);
			 }
			 
			 for(int i=m_selected_layers.size()-1; i >= 0 ; i--)
			 {
				 request.addLayer(m_selected_layers.get(i));
			 }
			 //request.addLayer(layers[0]);
			 
			 NavStruct nav = no.ums.pas.maps.defines.Navigation.preserve_aspect(n_lbo, n_rbo, n_ubo, n_bbo, dim);
			 n_lbo = nav._lbo;
			 n_rbo = nav._rbo;
			 n_ubo = nav._ubo;
			 n_bbo = nav._bbo;
			 
			 
			 request.setBBox(n_lbo + ","+n_bbo+","+n_rbo+","+n_ubo);

			 System.out.println(request.getFinalURL());
			 
			 GetMapResponse response =  wms.issueRequest(request);
			 //BufferedReader in = new BufferedReader(new InputStreamReader(response.getInputStream()));
			 m_img_load = ImageIO.read(response.getInputStream());
				MediaTracker tracker = new MediaTracker(variables.MAPPANE);
			 	//MediaTracker tracker = m_mtracker;
				tracker.addImage(m_img_load, 0);
				try {
					long start = System.currentTimeMillis();
					tracker.waitForID(0, m_n_wait_for_mediatracker_ms);
					//System.out.println("Waited " + (System.currentTimeMillis()-start) + " millisecs for image");
					if (tracker.isErrorAny()) {
						System.out.println("Error loading overlay image ");
						Error.getError().addError(PAS.l("common_error"), "Error loading overlay image", new Exception(), Error.SEVERITY_ERROR);
						setErrorMsg("Error loading map into media tracker");
						m_img_load = null;;
					}
				} catch (Exception ex) { 
					//ex.printStackTrace();
					b_error = true;
					m_img_load =  null;
					setErrorMsg(ex.getMessage());
					Error.getError().addError(PAS.l("common_error"), "An error occured communicating with the WMS server", ex, Error.SEVERITY_ERROR);
					
				}

				variables.NAVIGATION.setHeaderBounds(n_lbo,n_rbo,n_ubo,n_bbo);
			 
			if(m_img_load==null)
			{
				variables.NAVIGATION.setHeaderBounds(n_lbo,n_rbo,n_ubo,n_bbo);
				m_img_load =  null;
				if(m_retry==null)
					m_retry = new AutoLoadRetry(info);
			}
			b_loading_mapimage = false;

		}
		catch(Exception e)
		{
			//m_retry = null;
			setErrorMsg(e.getMessage());
			variables.NAVIGATION.setHeaderBounds(n_lbo,n_rbo,n_ubo,n_bbo);
			m_img_load =  null;
			e.printStackTrace();
			if(m_retry==null)
				m_retry = new AutoLoadRetry(info);
		}
		b_loading_mapimage = false;
		resetWmsAuthenticator();
		return m_img_load;

	}
		
	public Image load_map(double n_lbo, double n_rbo, double n_ubo, double n_bbo, Dimension dim, int n_mapsite, String sz_portrayal) {	
		ObjectFactory of = new ObjectFactory();
		UMapInfo info = of.createUMapInfo();
		info.setLBo((float)n_lbo);
		info.setRBo((float)n_rbo);
		info.setUBo((float)n_ubo);
		info.setBBo((float)n_bbo);
		info.setHeight(dim.height);
		info.setWidth(dim.width);
		info.setPortrayal(sz_portrayal);

		// This is used for GIS import, if there is only one record the getMap function fails because of UBO and BBO + RBO and LBO are the same
		if(info.getUBo() == info.getBBo()) {
			info.setUBo(info.getUBo()-0.001);
			info.setBBo(info.getBBo()+0.001);
		}
		if(info.getRBo() == info.getLBo()) {
			info.setRBo(info.getRBo()+0.001);
			info.setLBo(info.getLBo()-0.001);
		}
			
		
		try {

			b_loading_mapimage = true;
			boolean b_error = false;
			try {
				try
				{
					URL wsdl = new URL(vars.WSDL_PAS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/PAS.asmx?WSDL");
					//URL wsdl = new URL("http://localhost/WS/PAS.asmx?WSDL");
					QName service =  new QName("http://ums.no/ws/pas/", "pasws");
					Pasws pas = new Pasws(wsdl, service); //new java.net.URL(PAS.get_pas().get_sitename() + "/ExecAlert/WS/PAS.asmx?WSDL"), new javax.xml.namespace.QName("http://ums.no/", "pasws"));
					UPASMap map = pas.getPaswsSoap12().getMap(info);
					UMapInfo mapinfo = map.getMapinfo();
					
					variables.NAVIGATION.setHeaderBounds((float)mapinfo.getLBo(), (float)mapinfo.getRBo(), (float)mapinfo.getUBo(), (float)mapinfo.getBBo());
					
					//variables.NAVIGATION.setHeaderBounds(0, 0, 0, 0);
					
					m_img_load = Toolkit.getDefaultToolkit().createImage(map.getImage());
					MediaTracker tracker;
					tracker = new MediaTracker(variables.MAPPANE);
					//MediaTracker tracker = m_mtracker;
					tracker.addImage(m_img_load, 0);
					try {
						long start = System.currentTimeMillis();
						tracker.waitForID(0, m_n_wait_for_mediatracker_ms);
						//System.out.println("Waited " + (System.currentTimeMillis()-start) + " millisecs for image");
						if (tracker.isErrorAny()) {
							System.out.println("Error loading overlay image ");
							Error.getError().addError(PAS.l("common_error"), "Error loading overlay image", new Exception(), Error.SEVERITY_ERROR);
							setErrorMsg("Error loading map into media tracker");
							m_img_load = null;;
						}
					} catch (Exception ex) { 
						//ex.printStackTrace();
						b_error = true;
						m_img_load =  null;
						ex.printStackTrace();
						setErrorMsg(ex.getMessage());
						
					}
				}
				catch(Exception e)
				{
					b_error = true;
					m_img_load =  null;
					setErrorMsg(e.getMessage());
					//System.out.println(e.getMessage());
					//Error.getError().addError("Error", "Error loading map", e, 1);
				}
				
			} catch(Exception e) {
				b_error = true;
				setErrorMsg(e.getMessage());
				/*System.out.println(m_httpreq.get_last_error());
				//System.out.println("Error " + e.getMessage());
				System.out.println(e.getMessage());
				e.printStackTrace();
				Error.getError().addError("MapLoader","Exception in load_map",e,1);*/
				m_img_load =  null;
			}
			/*if(b_error) {
				//javax.swing.JOptionPane.showMessageDialog(null, "Could not load map");
				m_img_load =  null;
				b_error = true;
			}*/
		} catch(Exception e) {
			setErrorMsg(e.getMessage());
			//System.out.println("Image load_map() failed " + e.getMessage());
			//Error.getError().addError("MapLoader","Exception in load_map",e,1);
			m_img_load =  null;
		}

		//loadingframe(false);
		//m_pas.get_drawthread().set_neednewcoors(true);
		//m_pas.get_drawthread().add_image(m_img_load);
		if(m_img_load==null)
		{
			if(PAS.get_pas() == null)
				variables.NAVIGATION.setHeaderBounds(n_lbo,n_rbo,n_ubo,n_bbo);
			else
				m_navigation.setHeaderBounds(n_lbo,n_rbo,n_ubo,n_bbo);
			if(m_retry==null)
				m_retry = new AutoLoadRetry(info);
		}
		b_loading_mapimage = false;

		return m_img_load;
	}
	protected AutoLoadRetry m_retry = null;
	protected int n_seconds_to_reload = 0;
	public int getSecondsToReload() { return n_seconds_to_reload; }
	
	class AutoLoadRetry extends Thread
	{
		protected UMapInfo retry_mapinfo = null;
		public AutoLoadRetry(UMapInfo m)
		{
			super("AutoLoadRetry thread");
			retry_mapinfo = m;
			n_seconds_to_reload = 10;
			start();
		}
		public void run()
		{
			Timeout time = new Timeout(n_seconds_to_reload, 1000);
			try
			{
				while(!time.timer_exceeded() && m_img_load==null)
				{
					Thread.sleep(time.get_msec_interval());	 
					time.inc_timer();
					n_seconds_to_reload = (int)(time.get_remaining_seconds());
					PAS.get_pas().kickRepaint();
				}
				if(m_img_load!=null)
				{
					m_retry = null;
					return;
				}
				if(retry_mapinfo!=null)
				{
					/*load_map(retry_mapinfo.getLBo(), retry_mapinfo.getRBo(), 
							retry_mapinfo.getUBo(), retry_mapinfo.getBBo(), 
							new Dimension(retry_mapinfo.getWidth(), retry_mapinfo.getHeight()), 
							0, retry_mapinfo.getPortrayal());*/
					m_retry = null;
					PAS.get_pas().get_mappane().load_map();
					return ;
				}
			}
			catch(Exception e)
			{
				m_retry = null;				
			}
		}
	}
	/*Image retrieve_map(String s_url)
	{
		m_img_load = PAS.get_pas().retrieve_map(s_url); //image-loader in JApplet
		if(m_img_load==null)
		{
			System.out.println("Failed to load map");
			return null;
		}
		else
			System.out.println("Map loaded");
		return m_img_load;
	}	*/
}


