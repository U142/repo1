package no.ums.pas;


import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.ums.errorhandling.Error;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;


	public class Draw /*extends Thread */implements ImageObserver /*, Runnable*/ {

        private static final Log log = UmsLog.getLogger(Draw.class);

		Dimension m_dimension;
		boolean m_b_isrunning = false;
		boolean m_b_stop = false;
		Graphics m_gfx_buffer;
		protected Graphics get_gfxbuffer() { return m_gfx_buffer; }
		BufferedImage m_img_offscreen = null;
		int m_b_needrepaint = 1;
		private boolean m_b_neednewcoors = true;
		private MediaTracker m_tracker;
		private int m_n_currenttrackerid;
		//private PAS m_pas;
		private boolean m_b_suspended = false;
//		private Image m_mapimg = null;
		//private Image m_mapoverlay = null;
		private boolean m_b_imgpaint_success = false;
		public boolean isImgpaintSuccess() { return m_b_imgpaint_success; }
		public synchronized void set_suspended(boolean b_suspend) {
			//m_n_suspension_instances += (b_suspend ? 1 : -1);
		}
		//private int m_n_suspension_instances = 0;
		//public int get_suspension_instances() { return m_n_suspension_instances; }
		public boolean isRunning() { return m_b_isrunning; }
		private Component m_parent;
		private String m_sz_lasterror;
		public String get_lasterror() { return m_sz_lasterror; }
		protected void set_lasterror(String sz_error) { m_sz_lasterror = sz_error; }
		protected MapFrame m_mappane = null;
		public void set_mappane(MapFrame f) { m_mappane = f; }
		protected MapFrame get_mappane() { return m_mappane; }
		protected boolean b_firstmap = true;
		public void setFirstMap(boolean b)
		{
			b_firstmap = b;
		}
		
		/*public boolean get_suspended() {
			return (m_n_suspension_instances > 0 ? true : false); 
		}*/
		public void set_dimension(Dimension d) {
			m_dimension = d;
			//m_img_offscreen = new BufferedImage(m_dimension.width, m_dimension.height, BufferedImage.TYPE_INT_ARGB);
		}
		public void resize(Dimension d) {
			set_dimension(d);
			try
			{
				m_img_offscreen = new BufferedImage(m_dimension.width, m_dimension.height, BufferedImage.TYPE_INT_ARGB);
				m_gfx_buffer = m_img_offscreen.getGraphics();
				Graphics2D gfx = (Graphics2D)m_gfx_buffer;
				gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);				
			}
			catch(Exception e)
			{
				
			}
			
		}
		
		public Draw(Component component, int l_pri, int x, int y)
		{
			//this.setPriority(l_pri);
			m_parent = component;
			m_n_currenttrackerid = 0;
			m_tracker = new MediaTracker(component);
			m_dimension = new Dimension(x,y);
			m_img_offscreen = new BufferedImage(m_dimension.width, m_dimension.height, BufferedImage.TYPE_INT_ARGB);
			
			m_gfx_buffer = m_img_offscreen.getGraphics();	
			
		}

        public void setPainted() {
			if(m_b_needrepaint>0) 
				m_b_needrepaint --;
			//log.debug("m_b_needrepaint="+m_b_needrepaint);
		}
		public MediaTracker get_tracker() { return m_tracker; }
		public Dimension get_dimension() { return m_dimension; }
		public void remove_image(Image img) {
			get_tracker().removeImage(img, m_n_currenttrackerid);	
		}
		//private PAS get_pas() { return m_pas; }
		public void add_image(Image img) {
			get_tracker().addImage(img, m_n_currenttrackerid);
			m_n_currenttrackerid++;
		}
		public Graphics get_offscreen()
		{
			return m_gfx_buffer;	
		}
		public Image get_buff_image()
		{
			return m_img_offscreen;	
		}
		public synchronized boolean need_new_coors() { return m_b_neednewcoors; }
		public synchronized void set_neednewcoors(boolean b_need) { 
			m_b_neednewcoors = b_need; 
		}
		
		public void stop_thread() { m_b_stop = true; }
		
		boolean m_b_imageupdate = false;
		public void set_need_imageupdate() {
			m_b_imageupdate = true;
		}
		public synchronized boolean imageUpdate(Image img, int infoflags, int x, int y, int w, int h)
		{
			//return true;
			if ((infoflags & ImageObserver.ALLBITS) != 0) {
				m_b_imageupdate = false;
				return false;
			}
			m_b_imageupdate = true;
			return true;

		}
		/*public void run()
		{
			m_b_isrunning = true;
			try {
				while(!m_b_stop)
				{
					try{
						Thread.sleep(20);					
					}catch(InterruptedException e){Error.getError().addError("Draw","Exception in run",e,1);}
					if(m_b_needrepaint && !m_b_imageupdate)
					//if(!m_b_imageupdate)
					{
						if(!get_suspended())
						{
							if(need_new_coors())
							{
								calc_new_coors();
								//set_neednewcoors(false);
							}
	
							//log.debug("Repaint");
							if(m_mapimg!=null) {
								try {
									m_gfx_buffer.drawImage(m_mapimg, 0, 0, m_dimension.width, m_dimension.height, this);
									m_b_imgpaint_success = true;
								} catch(Exception e) {
									Error.getError().addError("Draw","Exception in run",e,1);
									set_lasterror("m_gfx_buffer.drawImage failed " + e.getMessage());
									log.debug(get_lasterror());
									m_b_imgpaint_success = false;
								}
							}
							else {
								set_lasterror("m_mapimg == null");
								//log.debug(get_lasterror());
								continue;
							}
							draw_layers();
							//m_b_needrepaint = false;
							map_repaint();
						}
					}
					checkforupdates();
				}
			} catch(Exception e) {
				//set_lasterror("Error " + e.getMessage());
				
				//log.debug(get_lasterror());
				log.debug(e.getMessage());
				e.printStackTrace();
				Error.getError().addError("Draw","Exception in run",e,1);
			}
			m_b_isrunning = false;
		}
		*/
		public void create_image() {
			try
			{
			if(m_b_needrepaint>=0 || m_mappane.IsLoading())//&& !m_b_imageupdate)
			{
				//if(!get_suspended()) 
				{
						//set_neednewcoors(false);
					//m_mapimg = null;
                    final Image m_mapimg = (get_mappane() == null) ? null : get_mappane().get_image();
					if(m_mapimg!=null) {
						try {
							/*MediaTracker tracker = new MediaTracker(get_mappane());
							tracker.addImage(m_mapimg, 0);
							try {
								//Thread.sleep(100);
								tracker.waitForID(0);
								if (tracker.isErrorAny()) {
									log.debug("Error loading image ");
								}
							} catch (Exception ex) { ex.printStackTrace(); }
							*/
							/*if(PAS.get_pas().get_mappane().getMapLoader().getMediaTracker().statusID(0, false) != MediaTracker.COMPLETE)
							{
								log.debug("!!!REPAINT AGAIN!!!!");
								PAS.get_pas().kickRepaint();
								//return;
							}*/
							AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC);
							Graphics2D g2d = (Graphics2D)m_gfx_buffer;
							g2d.clearRect(0, 0, m_dimension.width, m_dimension.height);
							AlphaComposite prev_alpha = (AlphaComposite)g2d.getComposite();
							boolean b_alpha_set = false;
							if(get_mappane().getOverlays()!=null)
							{
								for(int i=0; i < get_mappane().getOverlays().size(); i++)
								{
									if(get_mappane().get_mapoverlay(i)!=null)
									{
										if(get_mappane().getOverlays().get(i).b_visible)
										{
											AlphaComposite alpha2 = alpha.derive(0.7f);
											g2d.setComposite(alpha2);
											b_alpha_set = true;
											break;
										}
									}
								}
							}
							/*Graphics2D gfx = (Graphics2D)m_gfx_buffer;
							gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
									RenderingHints.VALUE_ANTIALIAS_ON);				
*/
							/*int translate_x = m_dimension.width/2;
							int translate_y = m_dimension.height/2;
							double rotate = 0;//Math.PI/400.0f;
							g2d.translate(translate_x, translate_y);
							g2d.rotate(rotate);
							g2d.translate(-translate_x, -translate_y);
							*/
							m_gfx_buffer.drawImage(m_mapimg, 0, 0, m_dimension.width, m_dimension.height, 0, 0, m_dimension.width, m_dimension.height, this);
							//g2d.rotate(-rotate, -translate_x, -translate_y);
							/*g2d.translate(translate_x, translate_y);
							g2d.rotate(-rotate);
							g2d.translate(-translate_x, -translate_y);*/
							
							//revert
							if(b_alpha_set)
								g2d.setComposite(prev_alpha);
							m_b_imgpaint_success = true;
						} catch(Exception e) {
							setPainted();
							Error.getError().addError("Draw","Exception in run",e,1);
							set_lasterror("m_gfx_buffer.drawImage failed " + e.getMessage());
							e.printStackTrace();
							m_b_imgpaint_success = false;
						}
						if(need_new_coors())
						{
							calc_new_coors();
							//log.debug("DRAW: Calc new coors");
						}
						
					}
					else {
						//get_mappane().superPaint(m_gfx_buffer);
						//get_mappane().paint(m_gfx_buffer);
						
						Graphics2D g2d = (Graphics2D)m_gfx_buffer;
						//clear screen
						Color oldBg = g2d.getBackground();
						g2d.setBackground(SystemColor.control);
						g2d.clearRect(0, 0, get_mappane().getWidth(), get_mappane().getHeight());
						g2d.setBackground(oldBg);
						//FontSet g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));

						
						g2d.setColor(SystemColor.textText);
												

						//if(get_suspended())
						if(!get_mappane().getMapLoader().IsLoadingMapImage() && !b_firstmap)
						{
							g2d.setFont(new java.awt.Font(null, java.awt.Font.BOLD, 18));
							g2d.drawString(Localization.l("maps_error_loading"), 20, 50);
							if(PAS.get_pas() != null) {
								
								g2d.setFont(new java.awt.Font(null, java.awt.Font.BOLD, 14));
								g2d.drawString(Localization.l("maps_error_send_from_parm"), 20, 100);
								g2d.drawString(Localization.l("maps_error_alt_quicksend_from_parm"), 40, 150);
								g2d.drawString(Localization.l("maps_error_alt_generate_sending_from_parm"), 40, 180);
								g2d.drawString(Localization.l("maps_error_alt_gis_import"), 40, 210);
							}
							
							g2d.setFont(new java.awt.Font(null, java.awt.Font.BOLD, 12));
							//int sec = 10;
							g2d.drawString(Localization.l("maps_error_auto_retry_in") + " " + get_mappane().getMapLoader().getSecondsToReload() + " " + Localization.l("common_seconds"), 20, 280);
							g2d.setColor(new java.awt.Color(255, 0, 0));
							g2d.drawString(Localization.l("maps_error_extended_error"), 20, 300);
							if(get_mappane().getMapLoader().getErrorMsg()!=null)
								g2d.drawString(get_mappane().getMapLoader().getErrorMsg(), 40, 320);
							set_lasterror("m_mapimg == null");
						}
						else if(b_firstmap)
						{
							g2d.setFont(new java.awt.Font(null, java.awt.Font.BOLD, 18));
							g2d.drawString(Localization.l("common_initializing"), 20, 50);
						}
						else if(m_mapimg==null)
							log.debug("m_mapimg==null");
						else
							log.debug("None of the above");
						//b_firstmap = false;
						setPainted();			
						return;
						//log.debug(get_lasterror());
					}
					b_firstmap = false;
					if(get_mappane().getOverlays()!=null)
					{
						for(int i=0; i < get_mappane().getOverlays().size(); i++)
						{
							if(get_mappane().get_mapoverlay(i)!=null)
							{
								if(get_mappane().getOverlays().get(i).b_visible)
								{
									try {
										m_gfx_buffer.drawImage(get_mappane().get_mapoverlay(i), 0, 0, m_dimension.width, m_dimension.height, this);
										} catch(Exception e) {
											m_b_imgpaint_success = false;
											setPainted();
										}
								}
							}
						}
					}
					
					//m_b_needrepaint = false;
					draw_layers(m_gfx_buffer);
					map_repaint();						
				}
				setPainted();
				//setPainted();				//checkforupdates();
			
			}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		protected void calc_new_coors() {
			set_neednewcoors(false);
		}
		public void draw_layers(Graphics g) {
			if(get_mappane()!=null)
				get_mappane().drawOnEvents(m_gfx_buffer);
			//m_b_needrepaint --;
		}
		protected void checkforupdates() {
			
		}
		protected void map_repaint() {
			//set_suspended(true);
			//m_parent.repaint();
			//set_suspended(false);
		}
	}