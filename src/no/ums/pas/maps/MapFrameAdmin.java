package no.ums.pas.maps;

import java.awt.Cursor;
import java.awt.Graphics;

import no.ums.pas.core.dataexchange.HTTPReq;
import no.ums.pas.core.logon.Settings.MAPSERVER;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.Draw;
import no.ums.pas.PAS;

public class MapFrameAdmin extends MapFrame {

	public MapFrameAdmin(int n_width, int n_height, Draw drawthread,
			Navigation nav, HTTPReq http, boolean b_enable_snap) {
		super(n_width, n_height, drawthread, nav, http, b_enable_snap);
	}
	public synchronized void load_map(boolean b_threaded) {
		//ensures that nothing is drawn until this new map is ready
		if(b_threaded)
		{
			
			Thread th = new Thread() {
				
				public void run()
				{
					//b_loading_in_progress = true;
					SetIsLoading(true, PAS.l("common_loading") + " " + PAS.l("common_map"));
					kickRepaint();
					load_map();
					SetIsLoading(false, "");
					/*while(get_mapimage()==null)
					{
						try
						{
							Thread.sleep(10);
						}
						catch(Exception e) { }
					}*/
					kickRepaint();
				}
			};
			th.setPriority(Thread.NORM_PRIORITY);
			th.start();
		} else
			load_map();
	}
	
	public void load_map() {
		//m_img_loading.flush();

		if(PAS.get_pas() != null)
			PAS.get_pas().get_mainmenu().enableUglandPortrayal((PAS.get_pas().get_settings().getMapServer()==MAPSERVER.DEFAULT ? true : false));
		if(m_maploader.IsLoadingMapImage())
			return;
		setAllOverlaysDirty();
		set_cursor(new Cursor(Cursor.WAIT_CURSOR));
		get_drawthread().set_need_imageupdate();
		//m_img_onscreen = null;
		try {
			switch(MAPSERVER.DEFAULT)
			{
			case DEFAULT:
				m_img_loading = m_maploader.load_map(get_navigation().getNavLBO(), get_navigation().getNavRBO(), get_navigation().getNavUBO(), get_navigation().getNavBBO(), this.getSize(), get_mapsite(), get_mapportrayal());
				break;
			case WMS:
				m_img_loading = m_maploader.load_map_wms(get_navigation().getNavLBO(), get_navigation().getNavRBO(), get_navigation().getNavUBO(), get_navigation().getNavBBO(), this.getSize(), PAS.get_pas().get_settings().getWmsSite());
			}
			
			//m_img_loading = wmsimg;
			if(m_img_loading==null)
			{
			}
			for(int i=0;i<m_overlays.size();++i) {
				/*if(PAS.get_pas().get_eastcontent().get_statuspanel().get_chk_layers_gsm().isSelected())
					showAllOverlays(1, true);
				else
					showAllOverlays(1, false);
				
				if(PAS.get_pas().get_eastcontent().get_statuspanel().get_chk_layers_umts().isSelected())
					showAllOverlays(4, true);
				else
					showAllOverlays(4, false);
					*/
				start_gsm_coverage_loader();
			}
		} catch(Exception e) {
			//System.out.println("Error loading map " + e.getMessage());
			//Error.getError().addError("MapFrame","Exception in load_map",e,1);
		}
		try
		{
			m_img_onscreen.flush();
		}
		catch(Exception e)
		{
		
		}
		m_img_onscreen= m_img_loading;
		/*m_img_onscreen = new BufferedImage(m_img_loading.getWidth(null), m_img_loading.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics tempg = m_img_onscreen.createGraphics();
		tempg.drawImage(m_img_loading, 0, 0, null);
		tempg.dispose();*/
		
		//PAS.get_pas().prepareImage(m_img_onscreen, PAS.get_pas().get_drawthread());
		try {
			prepareImage(m_img_onscreen, null); //get_drawthread());
		} catch(Exception e) {
			//System.out.println("prepareImage " + e.getMessage());
			//Error.getError().addError("MapFrame","Exception in load_map",e,1);
		}
		try {
			set_cursor(m_current_cursor);
		} catch(Exception e) {
			//Error.getError().addError("MapFrame","Exception in load_map",e,1);
		}
		//System.out.println("KICKREPAINT");
		kickRepaint();
		
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		try
		{
			//super.paint(g);
			get_drawthread().create_image();
			if(get_mode()==MAP_MODE_PAN_BY_DRAG && get_actionhandler().get_isdragging())
			{
				{
				}
			}
			g.drawImage(get_drawthread().get_buff_image(), get_actionhandler().getPanDragPoint().get_x(), get_actionhandler().getPanDragPoint().get_y(), m_dimension.width, m_dimension.height, this);
		}
		catch(Exception e)
		{
			
		}
	}
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
	}

}
