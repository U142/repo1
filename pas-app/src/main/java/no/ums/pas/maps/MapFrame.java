package no.ums.pas.maps;

//import no.ums.log.Log;
//import no.ums.log.UmsLog;
import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.Draw;
import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.dataexchange.HTTPReq;
import no.ums.pas.core.logon.Settings.MAPSERVER;
import no.ums.pas.core.menus.ViewOptions;
import no.ums.pas.core.popupmenus.PUPolyPoint;
import no.ums.pas.maps.defines.*;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.ImageLoader;
import no.ums.pas.ums.tools.PrintCtrl;
import org.jvnet.substance.SubstanceLookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;

//import netscape.javascript.JSObject;
//import sun.plugin.liveconnect.JavaScriptPermission;


public class MapFrame extends JPanel implements ActionListener, ComponentListener, MouseWheelListener, MouseListener {
    private static final Log log = UmsLog.getLogger(MapFrame.class);

	public class MapOverlay
	{
		MapOverlay(String jobid, int layer, JCheckBox ref, String provider)
		{
			sz_jobid = jobid;
			n_layer = layer;
			chk_ref = ref;
			sz_provider = provider;
		}
		public String sz_provider;
		public String sz_jobid;
		public Image img_load;
		public Image img_onscreen;
		public int n_layer;
		public JCheckBox chk_ref;
		public boolean b_isdownloaded = false;
		public boolean b_needupdate = false;
		public boolean b_visible = true;
	}
	
	private static final long serialVersionUID = 1L;
	public final static int MAP_MODE_PAN			= 0;
	public final static int MAP_MODE_ZOOM			= 1;
	public final static int MAP_MODE_HOUSESELECT	= 2;
	public final static int MAP_MODE_SENDING_POLY	= 3;
	public final static int MAP_MODE_OBJECT_MOVE	= 4;
	public final static int MAP_MODE_HOUSEEDITOR_	= 5;
	public final static int MAP_MODE_SENDING_ELLIPSE= 6;
	public final static int MAP_MODE_ASSIGN_EPICENTRE = 7;
	public final static int MAP_MODE_PAN_BY_DRAG = 8;
	public final static int MAP_MODE_SENDING_ELLIPSE_POLYGON = 9;
	public final static int MAP_MODE_PAINT_RESTRICTIONAREA = 10;
	
	public final static int MAP_HOUSEEDITOR_SET_NEWPOS			= 1;
	public final static int MAP_HOUSEEDITOR_SET_PRIVATE_COOR	= 2;
	public final static int MAP_HOUSEEDITOR_SET_COMPANY_COOR	= 3;
	public final static int MAP_HOUSEEDITOR_SET_COOR_NONE		= 4;
	
	Dimension m_dimension;
	public MapLoader m_maploader; 
	int m_n_mapsite;
	private Cursor m_cursor_draw;
	private Cursor m_cursor_illegal_draw;
	private Cursor m_cursor_draw_merge;
	private Cursor m_cursor_draw_pin_to_border;
	private Cursor m_cursor_houseeditor_private_coor;
	private Cursor m_cursor_houseeditor_company_coor;
	private Cursor m_cursor_epicentre;
	private ShapeStruct m_active_shape = null;
	public ShapeStruct get_active_shape() { return m_active_shape; }
	public MapLoader getMapLoader() { return m_maploader; }
	
	public boolean isInPaintMode()
	{
		switch(get_mode())
		{
		case MAP_MODE_PAINT_RESTRICTIONAREA:
		case MAP_MODE_SENDING_ELLIPSE:
		case MAP_MODE_SENDING_ELLIPSE_POLYGON:
		case MAP_MODE_SENDING_POLY:
			return true;
		}
		return false;
	}
	
	public boolean setPaintModeBasedOnActiveShape(boolean bForcePolyIsElliptical)
	{
		if(get_active_shape()==null)
			return false;
		
		Class cl = get_active_shape().getClass();
		if(cl.equals(PolygonStruct.class))
		{
			PolygonStruct poly = (PolygonStruct)get_active_shape();
			if(poly.isElliptical() || bForcePolyIsElliptical)
				set_mode(MAP_MODE_SENDING_ELLIPSE_POLYGON);
			else
				set_mode(MAP_MODE_SENDING_POLY);
		}
		else if(cl.equals(EllipseStruct.class))
			set_mode(MAP_MODE_SENDING_ELLIPSE);
		else if(cl.equals(PLMNShape.class))
			set_mode(MAP_MODE_PAN);
		else if(cl.equals(TasStruct.class))
			set_mode(MAP_MODE_PAN);
		
		return true;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		setMouseInsideCanvas(true);
	}
	@Override
	public void mouseExited(MouseEvent e) {
		setMouseInsideCanvas(false);
	}
	@Override
	public void mousePressed(MouseEvent e) {
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	public void set_active_shape(ShapeStruct s) { 
		m_active_shape = s;
		try
		{
			if(s.get_fill_color().getBlue() == 255) {
				System.out.println(s.get_fill_color());
				System.out.println(s.numParts);
				System.out.println(s.shapeID);
				System.out.println(s.shapeName);
				System.out.println(s.getType());
				System.out.println(s.parts);
			}
			if(s.get_fill_color().getRed() == 255) {
				System.out.println(s.get_fill_color());
				System.out.println(s.numParts);
				System.out.println(s.shapeID);
				System.out.println(s.shapeName);
				System.out.println(s.getType());
				System.out.println(s.parts);
			}
		}
		catch(Exception e)
		{
			
		}
		Variables.getMapFrame().kickRepaint();
		//set_mode(MAP_MODE_SENDING_POLY);
	}	
	private PUPolyPoint m_polypoint_popup = null;
	private PolySnapStruct m_polysnapstruct = null;
	public void set_current_snappoint(PolySnapStruct p) { m_polysnapstruct = p; }
	public PolySnapStruct get_current_snappoint() { return m_polysnapstruct; } 

	protected boolean b_mouse_inside_canvas = true;
	protected void setMouseInsideCanvas(boolean b) { 
		b_mouse_inside_canvas = b;
		if(PAS.get_pas()!=null)
			PAS.get_pas().kickRepaint();
		else {
			Variables.getDraw().setRepaint(Variables.getMapFrame().get_mapimage());
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					//get_mappane().repaint(0, 0, get_mappane().getWidth(), get_mappane().getHeight());
					//get_mappane().paintImmediately(0, 0, get_mappane().getWidth(), get_mappane().getHeight());
					//System.out.println("!!!!!EXECUTING KICKREPAINT!!!!!");
					Variables.getMapFrame().repaint();
					Variables.getMapFrame().validate();
				}
			});
		}
	}
	public boolean getMouseInsideCanvas() { return b_mouse_inside_canvas; }
	
	public Cursor get_cursor_draw() { return m_cursor_draw; }
	public Cursor get_cursor_illegal_draw() { return m_cursor_illegal_draw; }
	public Cursor get_cursor_epicentre() { return m_cursor_epicentre; }
	public Cursor get_cursor_draw_merge() { return m_cursor_draw_merge; }
	public Cursor get_cursor_draw_pin_to_border() { return m_cursor_draw_pin_to_border; }

	public Image m_img_onscreen = null;
	public Image m_img_loading = null;
	//Image m_img_overlay = null;
	//Image m_img_overlay_onscreen = null;
	
	ArrayList<MapOverlay> m_overlays = null;//new ArrayList<MapOverlay>();
	public ArrayList<MapOverlay> getOverlays() { return m_overlays; }
	
	
	
	public void showAllOverlays(int layer, boolean b_show, String jobid, JCheckBox chkref, String provider)
	{
		try
		{
			/*if(m_overlays==null)
			{
				setAllOverlays();
			}
			for(int i = 0; i < m_overlays.size(); i++)
			{
				if(m_overlays.get(i).n_layer==layer)
				{
					m_overlays.get(i).b_visible = b_show;
					if(!m_overlays.get(i).b_isdownloaded && b_show) //not yet downloaded and should be visible
						m_overlays.get(i).b_needupdate = true; //mark need update before starting loader

					start_gsm_coverage_loader();
				}
			}*/
			if(m_overlays==null)
				m_overlays = new ArrayList<MapOverlay>();
			for(int i=0; i < m_overlays.size(); i++) //disable old overlays
			{
				JCheckBox chk = m_overlays.get(i).chk_ref;
				if(chk!=null)
					chk.setSelected(false);
			}
			m_overlays.clear();
			m_overlays.add(new MapOverlay(jobid, layer, chkref, provider));
			for(int i = 0; i < m_overlays.size(); i++)
			{
				if(m_overlays.get(i).n_layer==layer)
				{
					m_overlays.get(i).b_visible = b_show;
					chkref.setSelected(b_show);
					if(!m_overlays.get(i).b_isdownloaded && b_show) //not yet downloaded and should be visible
						m_overlays.get(i).b_needupdate = true; //mark need update before starting loader

					start_gsm_coverage_loader();
				}
			}
			
		}
		catch(Exception e)
		{
			
		}
	}
	
	//when new map is downloaded
	public void setAllOverlaysDirty()
	{
		if(m_overlays==null)
			return;
		for(int i=0; i < m_overlays.size(); i++)
		{
			m_overlays.get(i).b_isdownloaded = false;
			m_overlays.get(i).b_needupdate = true;
		}
	}
	public void setAllOverlays()
	{
		try
		{
			m_overlays = new ArrayList<MapOverlay>();
			//resetAllOverlays();
			for(int i=0; i < PAS.get_pas().get_statuscontroller().get_sendinglist().size(); i++)
			{
				try
				{
					String sz_jobid = PAS.get_pas().get_statuscontroller().get_sendinglist().get(i).getLBA().sz_jobid;
					
					//m_overlays.add(new MapOverlay(sz_jobid, 1)); //gsm900/1800
					//m_overlays.add(new MapOverlay(sz_jobid, 4)); //UMTS	
					//start_gsm_coverage_loader();
				}
				catch(Exception e)
				{
					
				}
				
			}
		}
		catch(Exception e)
		{
			return ;
		}					

	}

	//close status should run this
	public void resetAllOverlays()
	{
		if(m_overlays!=null)
		{
			for(int i=0; i < m_overlays.size(); i++)
			{
				JCheckBox chk = m_overlays.get(i).chk_ref;
				if(chk!=null)
					chk.setSelected(false);
			}
			m_overlays.clear();
			m_overlays = null;
			System.out.println("Overlays reset");
		}
	}
	
	MapFrameActionHandler m_actionhandler;
	int m_n_current_cursor = Cursor.DEFAULT_CURSOR;
	protected int m_n_current_mode = MAP_MODE_PAN;
	protected int m_n_prev_mode = MAP_MODE_PAN;
	protected int m_n_prev_paint_mode = MAP_MODE_SENDING_POLY;
	int m_n_current_submode = 0;
	private Inhabitant m_current_inhab_move = null;
	public Cursor m_current_cursor;
	Robot m_robot;
	String m_sz_mapportrayal = "By";
	UMSMapObject m_current_object = null;
	protected UMSMapObject get_current_object() { return m_current_object; }
	void set_current_object(UMSMapObject obj) { m_current_object = obj; }
	private Draw m_drawthread;
	private Navigation m_navigation;
	protected Draw get_drawthread() { return m_drawthread; }
	protected Navigation get_navigation() { return m_navigation; }
	public void addActionListener(ActionListener callback) { m_actionhandler.addActionListener(callback); }
	private ImageIcon m_icon_pinpoint = null;
	private ImageIcon m_icon_adredit = null;
	private ImageIcon m_icon_sethousecoor_private = null;
	private ImageIcon m_icon_sethousecoor_company = null;
	public ImageIcon get_icon_pinpoint() { return m_icon_pinpoint; }
	public ImageIcon get_icon_adredit() { return m_icon_adredit; }

    private Image img_loader_snake = null;
	private String m_sz_what_is_loading = "";
	
	public boolean get_draw_adredit() {
        return get_mode() == MAP_MODE_HOUSEEDITOR_;
		
	}
	public void setCurrentInhabitant(Inhabitant i) {
		m_current_inhab_move = i;
	}
	public Inhabitant getCurrentInhabitant() {
		return m_current_inhab_move;
	}
	private MapPointLL m_pinpointll = null;
	public MapPointLL get_pinpointll() { return m_pinpointll; }
	private MapPointLL m_adreditll = null;
	public MapPointLL get_adreditll() { return m_adreditll; }
	private ArrayList<HouseItem> m_mouseoverhouse = null;
	public ArrayList<HouseItem> get_mouseoverhouse() { return m_mouseoverhouse; }
	public void set_mouseoverhouse(ArrayList<HouseItem> i) { m_mouseoverhouse = i; }
	public void set_pinpoint(MapPointLL p) {
		m_pinpointll = p;
        ViewOptions.TOGGLE_SEARCHPOINTS.setSelected(true);
    }
	public void set_adredit(MapPointLL p) {
		m_adreditll = p;
	}
	
	protected boolean b_show_tooltip = false;
	protected JToolTip m_tooltip = null;
	public Point getToolTipLocation(MouseEvent event) {
		//if(b_show_tooltip)
		//	return new Point(get_current_mousepos().x, get_current_mousepos().y);
		//else
		//	return null;
		return super.getToolTipLocation(event);
	}
	public JToolTip createToolTip() {
        JToolTip tip = super.createToolTip();
        //Substance 3.3
		//Color _c1 = SubstanceLookAndFeel.getActiveColorScheme().getDarkColor();
		
        //Substance 5.2
        //Color _c1 = SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme().getDarkColor();
		
		//Color c1 = new Color(_c1.getRed(), _c1.getGreen(), _c1.getBlue(), 220);
        Color _c1 = SystemColor.controlShadow;
		Color c1 = new Color(_c1.getRed(), _c1.getGreen(), _c1.getBlue(), 220);
		
        tip.setBackground(c1);
		
		//Substance 3.3
        tip.setForeground(SubstanceLookAndFeel.getActiveColorScheme().getForegroundColor());
        
		//Substance 5.2
		//tip.setForeground(SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme().getForegroundColor());
        
        m_tooltip = tip;
        return tip;
    }
	public void setToolTipText(final String s, final boolean b_show)
	{
		b_show_tooltip = b_show;
		/*setToolTipText((b_show ? s : null));
		if(m_tooltip!=null)
		{
			m_tooltip.setVisible(b_show);
			m_tooltip.repaint();
			
		}*/
		try
		{
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					setToolTipText((b_show ? s: null));
					
					//m_tooltip.setTipText((b_show ? s:null));
					//System.out.println("Tooltip: "+ s);
				}
			});
		}
		catch(Exception e)
		{
			
		}
		//m_actionhandler.mouseMoved(m_actionhandler.m_mouseoverwait._callevent);
		//m_tooltip.repaint();
		
	}

	public MapFrame(int n_width, int n_height, Draw drawthread, Navigation nav, HTTPReq http, boolean b_enable_snap)
	{
		super();
		setPreferredSize(new Dimension(100,100));
		setLayout(new BorderLayout());
		
		try
		{
			//img_loader_snake = ImageLoader.load_icon("ajax-loader.gif").getImage();
			img_loader_snake = ImageLoader.load_icon("convert_32.png").getImage();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		m_drawthread = drawthread;
		m_navigation = nav;
		m_n_mapsite = 0;
		//this.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		this.setBackground(Color.WHITE);
		m_actionhandler = new MapFrameActionHandler(this, b_enable_snap);
		m_dimension = new Dimension(n_width, n_height);
		setSize(m_dimension.width, m_dimension.height);	
		m_maploader = new MapLoader(this, http);
		m_current_cursor = new Cursor(Cursor.DEFAULT_CURSOR);
		this.setPreferredSize(m_dimension);
		m_polypoint_popup = new PUPolyPoint(PAS.get_pas(), "Menu", this);
		m_current_mousepos = new Point();


		String sz_url = "edit.gif";
		if(PAS.icon_version==2)
			sz_url = "brush_16_paint.png";
		try {
			Dimension best_size = Toolkit.getDefaultToolkit().getBestCursorSize(16, 16);
			ImageIcon icon = ImageLoader.load_icon(sz_url);
			//m_cursor_draw = Toolkit.getDefaultToolkit().createCustomCursor(icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH), 
			//		  new Point(15, 22), "Draw");
			m_cursor_draw = Toolkit.getDefaultToolkit().createCustomCursor(icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH), 
					  new Point(7, 24), "Draw");
			sz_url = "brush_16_paint_illegal.gif";
			icon = ImageLoader.load_icon(sz_url);
			m_cursor_illegal_draw = Toolkit.getDefaultToolkit().createCustomCursor(icon.getImage(), 
					  new Point(7, 24), "Illegal Draw");
			
			sz_url = "brush_16_merge.gif";
			icon = ImageLoader.load_icon(sz_url);
			m_cursor_draw_merge = Toolkit.getDefaultToolkit().createCustomCursor(icon.getImage(), 
					  new Point(7, 24), "Merge");

			sz_url = "brush_16_pin.gif";
			icon = ImageLoader.load_icon(sz_url);
			m_cursor_draw_pin_to_border = Toolkit.getDefaultToolkit().createCustomCursor(icon.getImage(), 
					  new Point(7, 24), "Pin to border");
			
			//m_cursor_draw = new Cursor(Cursor.CROSSHAIR_CURSOR);
		} catch(Exception e) {
            //logger.warn("An error occured", e);
        }
		sz_url = "epicentre_pinpoint.png";
		try {
			ImageIcon icon_epicentre = ImageLoader.load_icon(sz_url);
			m_cursor_epicentre = Toolkit.getDefaultToolkit().createCustomCursor(icon_epicentre.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH),
					new Point(0,0), "Epicentre");
		} catch(Exception e) {
            //logger.warn("Failed to create custom cursor", e);
        }
		String sz_pinpointfile = "pinpoint_blue.png";
		try {
			m_icon_pinpoint = ImageLoader.load_icon(sz_pinpointfile);
		} catch(Exception e) {
			m_icon_pinpoint = null;
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError(PAS.l("common_error"),"Exception in MapFrame",e,1);
		}
		try {
			m_icon_adredit = ImageLoader.load_icon("pinpoint.png");
		} catch(Exception e) {
			m_icon_adredit = null;
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("MapFrame","Exception in MapFrame",e,1);
		}
		Dimension dim = Toolkit.getDefaultToolkit().getBestCursorSize(10, 10);
		//System.out.println("Best cursor size " + dim.width + ", " + dim.height);
		try {
			m_icon_sethousecoor_private = ImageLoader.load_icon("cursor_private.png");
			m_cursor_houseeditor_private_coor = Toolkit.getDefaultToolkit().createCustomCursor(m_icon_sethousecoor_private.getImage().getScaledInstance(dim.width, dim.height, Image.SCALE_SMOOTH), 
					  							new Point(Math.min(dim.width-1, 16), Math.min(dim.height-1, 16)), "Private");
		} catch(Exception e) {
            log.warn("Failed to set custom cursor", e);
			m_icon_sethousecoor_private = null;
		}
		try {
			m_icon_sethousecoor_company = ImageLoader.load_icon("cursor_company.png");
			m_cursor_houseeditor_company_coor = Toolkit.getDefaultToolkit().createCustomCursor(m_icon_sethousecoor_company.getImage().getScaledInstance(dim.width, dim.height, Image.SCALE_SMOOTH),
						new Point(Math.min(dim.width-1, 16), Math.min(dim.height-1, 16)), "Company");
		} catch(Exception e) {
			m_icon_sethousecoor_company = null;
            log.warn("Failed to set custom cursor", e);
		}
		addComponentListener(this);
		addMouseWheelListener(this);
		addMouseListener(this);
	}
	public Dimension getMinimumSize() {
		return m_dimension;
	}
	public Dimension getMaximumSize() {
		return m_dimension;
	}
	public void set_dimension(Dimension d) {
		m_dimension = d;
		//setSize(m_dimension.width, m_dimension.height);
		setPreferredSize(m_dimension);
	}
	public void draw_pinpoint(Graphics g) {
        if(ViewOptions.TOGGLE_SEARCHPOINTS.isSelected() && get_pinpointll() != null) {
			try {
				MapPoint p = new MapPoint(get_navigation(), get_pinpointll());
				g.drawImage(get_icon_pinpoint().getImage(), p.get_x() - get_icon_pinpoint().getIconWidth()/2, p.get_y() - get_icon_pinpoint().getIconHeight()/2, get_icon_pinpoint().getIconWidth(), get_icon_pinpoint().getIconHeight(), this);
			} catch(Exception e) {
                log.warn("Failed to draw pinpoint", e);
			}
		}
	}
	public void draw_adredit(Graphics g) {
		try {
			if(get_draw_adredit() && this.get_adreditll() != null) {
				MapPoint p = new MapPoint(get_navigation(), this.get_adreditll());
				g.drawImage(get_icon_adredit().getImage(), p.get_x() - get_icon_pinpoint().getIconWidth()/2, p.get_y() - get_icon_pinpoint().getIconHeight()/2, get_icon_pinpoint().getIconWidth(), get_icon_pinpoint().getIconHeight(), this);
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("MapFrame","Exception in draw_adredit",e,1);
		}
	}
	public void draw_moveinhab_text(Graphics g) {
		try {
			g.drawString(getCurrentInhabitant().get_adrname(), this.get_current_mousepos().x+15, this.get_current_mousepos().y+15);
		} catch(Exception e) {
			
		}
	}
	
	public Dimension get_dimension() { return m_dimension; }
	
	/*public void robot_movecursor(Point p) {
		m_robot.mouseMove(p.x, p.y);
	}*/
		
	public void print_map()
	{
		//Graphics2D g2d = (Graphics2D)this.getGraphics();
		//PrintCtrl ctrl = new PrintCtrl(get_pas().get_mappane(), get_pas(), null);
		//ctrl.print();
		PrintCtrl.printComponent(this, null);
	}
	public void save_map(Image i) {
		try {
			PrintCtrl.printComponentToFile(i, null);
		} catch(Exception e) {
			
		}
	}
	
	public void set_mode(int n_mode)
	{
		boolean b_was_in_paint_mode = isInPaintMode();
		if(b_was_in_paint_mode)
			m_n_prev_paint_mode = get_mode();
		//let settings decide
		if(n_mode==MAP_MODE_PAN || n_mode==MAP_MODE_PAN_BY_DRAG)
		{
			if(PAS.get_pas() != null && PAS.get_pas().get_settings().getPanByDrag())
				n_mode = MAP_MODE_PAN_BY_DRAG;
			else
				n_mode = MAP_MODE_PAN;
		}
		switch(n_mode) {
			case MAP_MODE_PAN:
				set_cursor(new Cursor(Cursor.HAND_CURSOR));
				m_n_prev_mode = m_n_current_mode;
				if(PAS.get_pas() != null)
					PAS.get_pas().get_mainmenu().clickMapMode(n_mode, true);
				break;
			case MAP_MODE_PAN_BY_DRAG:
				set_cursor(new Cursor(Cursor.MOVE_CURSOR));
				m_n_prev_mode = m_n_current_mode;
				if(PAS.get_pas() != null)
					PAS.get_pas().get_mainmenu().clickMapMode(n_mode, true);
				break;
			case MAP_MODE_ZOOM:
				set_cursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				m_n_prev_mode = m_n_current_mode;
				if(PAS.get_pas() != null)
					PAS.get_pas().get_mainmenu().clickMapMode(n_mode, true);
				break;
			case MAP_MODE_HOUSESELECT:
				set_cursor(new Cursor(Cursor.DEFAULT_CURSOR));
				m_n_prev_mode = m_n_current_mode;
				break;
			case MAP_MODE_SENDING_POLY:
			case MAP_MODE_PAINT_RESTRICTIONAREA:
				try {
					//setCursor(PAS.get_pas().get_mainmenu().get_cursor_draw());
					//setCursor(get_cursor_draw());
					set_cursor(get_cursor_draw());
				} catch(Exception e) {
					Error.getError().addError("MapFrame","Exception in set_mode",e,1);
				}
				break;
			case MAP_MODE_OBJECT_MOVE:
				try {
					get_current_object().setMoving(true);
					//setCursor(new Cursor(Cursor.MOVE_CURSOR));
					set_cursor(new Cursor(Cursor.MOVE_CURSOR));
				} catch(Exception e) {
					
				}
				break;
			case MAP_MODE_HOUSEEDITOR_:
				setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				//set_submode(m_n_current_submode);
				//new Core.MainUI.HouseEditorDlg(null);
				m_n_prev_mode = m_n_current_mode;
				break;
			case MAP_MODE_SENDING_ELLIPSE:
			case MAP_MODE_SENDING_ELLIPSE_POLYGON:
				//setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
				set_cursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
				break;
			case MAP_MODE_ASSIGN_EPICENTRE:
				//setCursor(get_cursor_epicentre());
				set_cursor(get_cursor_epicentre());
				break;
		}
		m_n_current_mode = n_mode;
		boolean b_is_in_paint_mode = isInPaintMode();
		if(b_was_in_paint_mode || b_is_in_paint_mode)
			PAS.get_pas().kickRepaint();

	}
	public void set_submode(int n_mode) {
		m_n_current_submode = n_mode;
		switch(m_n_current_mode) {
			case MAP_MODE_HOUSEEDITOR_: {
				switch(m_n_current_submode) {
					case MAP_HOUSEEDITOR_SET_NEWPOS:
						set_cursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
						break;
					case MAP_HOUSEEDITOR_SET_PRIVATE_COOR:
						System.out.println("set cursor private");
						set_cursor(m_cursor_houseeditor_private_coor);
						break;
					case MAP_HOUSEEDITOR_SET_COMPANY_COOR:
						System.out.println("set cursor company");
						set_cursor(m_cursor_houseeditor_company_coor);
						break;
					case MAP_HOUSEEDITOR_SET_COOR_NONE:
						System.out.println("set cursor wait");
						set_cursor(new Cursor(Cursor.WAIT_CURSOR));
						break;
					default:
						set_cursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
						break;
				}
			}
			break;
		}
	}
	public void set_prev_mode()
	{
		set_mode(m_n_prev_mode);
	}
	public void set_prev_paintmode()
	{
		set_mode(m_n_prev_paint_mode);
	}
	public int get_mode() { return m_n_current_mode; }
	public int get_submode() { return m_n_current_submode; }
	
	public void set_mode_objectmove(UMSMapObject obj) {
		set_current_object(obj);
		set_mode(MAP_MODE_OBJECT_MOVE);
	}
	
	public void initialize()
	{
		setVisible(true);
		//load_map();
		setFocusable(true);
		addMouseMotionListener(m_actionhandler);
		addMouseListener(m_actionhandler);
		try {
			this.addKeyListener(m_actionhandler);
		} catch(Exception e) { Error.getError().addError("MapFrame","Exception in initialize",e,1);/*PAS.get_pas().add_event(e.getMessage());*/ }
	}
	public void drawOnEvents(Graphics gfx)
	{
		if(get_actionhandler().get_isdragging() && get_mode()==MapFrame.MAP_MODE_ZOOM)
		{
			draw_dragging_square(gfx);
		}
		if(IsLoading())
		{
			draw_loading_image(gfx);
		}
	}

	
	void draw_loading_image(Graphics gfx)
	{
		Graphics2D g2d = (Graphics2D)gfx;
		if(img_loader_snake==null)
			return;
		int scale = 1;
		int imgw = img_loader_snake.getWidth(null);
		int imgh = img_loader_snake.getHeight(null);
		//Substance 3.3
		Color c1 = SubstanceLookAndFeel.getTheme().getDefaultColorScheme().getUltraDarkColor();
		//Color c2 = SubstanceLookAndFeel.getActiveColorScheme().getLightColor();
		Color c2 = SubstanceLookAndFeel.getTheme().getDefaultColorScheme().getLightColor();
		
		//Substance 5.2
		//Color c1 = SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme().getUltraDarkColor();
		//Color c2 = SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme().getLightColor();
				
		gfx.setColor(new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 160));
		
		gfx.drawRoundRect(getWidth()/2-100, getHeight()/2-70, 200, 100, 25, 25);
		
		gfx.setColor(new Color(c2.getRed(), c2.getGreen(), c2.getBlue(), 160));
		
		gfx.fillRoundRect(getWidth()/2-100, getHeight()/2-70, 200, 100, 25, 25);
		
		gfx.setColor(new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 225));
		gfx.setFont(new Font(null, Font.BOLD, 18));
		gfx.drawString(m_sz_what_is_loading, getWidth()/2-60, getHeight()/2-40);
		
		
		gfx.drawImage(img_loader_snake, getWidth()/2-imgw/2*scale, getHeight()/2-imgh/2*scale, imgw*scale, imgh*scale, null);
	}
	
	void draw_dragging_square(Graphics gfx)
	{
		gfx.setColor(new Color((float)0.0, (float)0.0, (float)0.0, (float)0.2));
		int x1, x2, y1, y2;
		x1 = get_actionhandler().getMousedownPos().width - Math.abs(get_actionhandler().getMousedownPos().width - get_actionhandler().getCursorPos().width);
		x2 = get_actionhandler().getMousedownPos().width + Math.abs(get_actionhandler().getMousedownPos().width - get_actionhandler().getCursorPos().width);
		y1 = get_actionhandler().getMousedownPos().height -  Math.abs(get_actionhandler().getMousedownPos().height - get_actionhandler().getCursorPos().height);
		y2 = get_actionhandler().getMousedownPos().height +  Math.abs(get_actionhandler().getMousedownPos().height - get_actionhandler().getCursorPos().height);
		gfx.fillRect(x1, y1, x2-x1, y2-y1);
		gfx.setColor(Color.black);
		gfx.drawRect(x1, y1, x2-x1, y2-y1);		 
	}
	
	public int get_mapsite() { return m_n_mapsite; }
	public String get_mapportrayal() { return m_sz_mapportrayal; }
	//void set_mapportrayal(String sz_portrayal) { m_sz_mapportrayal = sz_portrayal; } 
	/*void set_mapsite(int n_mapsite) { 
		m_n_mapsite = n_mapsite; 
	}*/
	public void set_mapsite(MapSite site) {
		m_n_mapsite = site.get_mapsite();
		m_sz_mapportrayal = site.get_portrayal();
	}
	public MapFrameActionHandler get_actionhandler() { return m_actionhandler; }
	
	protected void start_gsm_coverage_loader()
	{
		if(m_maploader.IsLoadingOverlay())
			return;
		if(m_overlays==null)
			return;
		new Thread("GSM Coverage loader thread")
		{
			public void run() {
				try
				{
					if(m_overlays==null)
					{
						SetIsLoading(false, "");
						return;
					}
					for(int i=0; i < m_overlays.size(); i++)
					{
						MapOverlay overlay = m_overlays.get(i);
						if(!overlay.b_needupdate) //marked to need update
							continue;
						if(overlay.b_isdownloaded) //already downloaded
							continue;
						if(!overlay.b_visible) //not visible
							continue;
						String sz_jobid = overlay.sz_jobid;
						/*try
						{
							sz_jobid = PAS.get_pas().get_statuscontroller().get_sendinglist().get(0).getLBA().sz_jobid;
						}
						catch(Exception e)
						{
							return ;
						}*/					
						switch(overlay.n_layer)
						{
						case 1:
							SetIsLoading(true, "GSM (" + overlay.sz_provider + ")");
							break;
						case 4:
							SetIsLoading(true, "UMTS (" + overlay.sz_provider + ")");
							break;
						}
						//PAS.get_pas().get_drawthread().setMapOverlay(null);
						//m_img_overlay = null;
						//m_img_overlay_onscreen = null;
						overlay.img_load = null;
						overlay.img_onscreen = null;
						int layer = overlay.n_layer;
						overlay.img_load = m_maploader.load_overlay(sz_jobid, layer, get_navigation().getNavLBO(), get_navigation().getNavRBO(), get_navigation().getNavUBO(), get_navigation().getNavBBO(), get_navigation().getDimension());
						
						//prepareImage(m_img_overlay_onscreen, get_drawthread());
						if(overlay.img_load==null)
						{
							overlay.img_onscreen = null;
							overlay.b_needupdate = true;
							overlay.b_isdownloaded = false;	
							//PAS.get_pas().get_statuscontroller().get_sendinglist()
						}
						else
						{
							prepareImage(overlay.img_onscreen, get_drawthread());
							overlay.img_onscreen = overlay.img_load;
							overlay.b_needupdate = false;
							overlay.b_isdownloaded = true;
							//PAS.get_pas().get_drawthread().setMapOverlay(m_img_overlay_onscreen);
						}
						SetIsLoading(false, "");
					}
				}
				catch(Exception e)
				{
					
				}
			}
		}.start();

	}
	
	public void load_map() {
		//m_img_loading.flush();

		System.out.println("Loading map " + PAS.get_pas().get_settings().getMapServer().name());
		PAS.get_pas().get_mainmenu().enableUglandPortrayal((PAS.get_pas().get_settings().getMapServer()==MAPSERVER.DEFAULT ? true : false));
		if(m_maploader.IsLoadingMapImage())
			return;
		setAllOverlaysDirty();
		set_cursor(new Cursor(Cursor.WAIT_CURSOR));
		get_drawthread().set_need_imageupdate();
		//m_img_onscreen = null;
		try {
			switch(PAS.get_pas().get_settings().getMapServer())
			{
			case DEFAULT:
				m_img_loading = m_maploader.load_map(get_navigation().getNavLBO(), get_navigation().getNavRBO(), get_navigation().getNavUBO(), get_navigation().getNavBBO(), this.getSize(), get_mapsite(), get_mapportrayal());
				break;
			case WMS:
				m_img_loading = m_maploader.load_map_wms(get_navigation().getNavLBO(), get_navigation().getNavRBO(), get_navigation().getNavUBO(), get_navigation().getNavBBO(), this.getSize(), Variables.getSettings().getWmsSite());
			}
			
			//m_img_loading = wmsimg;
			if(m_img_loading==null)
			{
			}
			if(m_overlays!=null)
			{
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
			}
		} catch(Exception e) {
			//System.out.println("Error loading map " + e.getMessage());
			//Error.getError().addError("MapFrame","Exception in load_map",e,1);
			e.printStackTrace();
			PAS.pasplugin.onMapLoadFailed(this.get_maploader());
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
		PAS.get_pas().kickRepaint();
		
	}
	
	HashMap<String, Boolean> hash_loading = new HashMap<String, Boolean>();  
	int b_loading_in_progress = 0;
	public boolean IsLoading() { 
		if(b_loading_in_progress > 0)
			return true;
		else return false;
	}
	public void SetIsLoading(boolean b, String what_is_loading) { 
		
		//b_loading_in_progress += (b ? 1 : (b_loading_in_progress>=1 ? -1 : 0));
		if(b)
			b_loading_in_progress++;
		else
			b_loading_in_progress--;
		if(b_loading_in_progress<0)
			b_loading_in_progress=0;
		if(what_is_loading.length()>0)
			m_sz_what_is_loading = what_is_loading;
		if(PAS.get_pas() != null)
			PAS.get_pas().kickRepaint();
		else
			kickRepaint();
	}
	
	public synchronized void load_map(boolean b_threaded) {
		//ensures that nothing is drawn until this new map is ready
		try
		{
			PAS.pasplugin.onBeforeLoadMap(PAS.get_pas().get_settings());
			if(b_threaded)
			{
				
				Thread th = new Thread("Load map thread") {
					
					public void run()
					{
						//b_loading_in_progress = true;
						SetIsLoading(true, PAS.l("common_loading") + " " + PAS.l("common_map"));
						PAS.get_pas().kickRepaint();
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
						PAS.get_pas().kickRepaint();
					}
				};
				th.setPriority(Thread.NORM_PRIORITY);
				th.start();
			} else
				load_map();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void set_cursor(Cursor cur)
	{		
		if(cur.getType() != Cursor.WAIT_CURSOR)
		{
			setCursor(cur);
			m_n_current_cursor = cur.getType();
			m_current_cursor = cur;
		}
		else
		{
			//wait cursor is current. must be overridden by reset_cursor
			//m_n_current_cursor = getCursor().getType();
			setCursor(cur);
		}
	}
	protected void reset_cursor()
	{
		m_n_current_cursor = -1;
	}
	/*public void superPaint(Graphics g)
	{
		super.paint(g);
	}*/
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
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
	public void setSize(Dimension d) {
		super.setSize(d);
	}
	@Override
	public void setPreferredSize(Dimension d) {
		super.setPreferredSize(d);
	}
	
	public Image get_mapimage() {
		return m_img_onscreen;	
	}
	public void set_mapimage(Image i) {
		m_img_onscreen = i;
	}
	public Image get_mapoverlay(int n) {
		//return m_img_overlay_onscreen;
		Image ret = null;
		try
		{
			ret = getOverlays().get(n).img_onscreen;
		}
		catch(Exception e)
		{
		}
		return ret;
	}
	
	MapLoader get_maploader() {
		return m_maploader;
	}

	public void draw_background() {

	}
	
	public Image get_image() {
		return m_img_onscreen;	
	}
	
	public void clear_screen()
	{

	}
	@Override 
	public void update(Graphics g)
	{
		super.update(g);
		System.out.println("!!!!!!!!!!UPDATE!!!!!!!!!!!!!");
		paint(g);
	}
	public void update()
	{
		clear_screen();
		draw_background();				
	}
	
	private Point m_current_mousepos = null;
	public Point get_current_mousepos() { return m_current_mousepos; }
	public void set_current_mousepoint(Point p) {
		m_current_mousepos = p;
	}
	
	public void kickRepaint() {
		get_drawthread().setRepaint(Variables.getMapFrame().get_mapimage());
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				//get_mappane().repaint(0, 0, get_mappane().getWidth(), get_mappane().getHeight());
				//get_mappane().paintImmediately(0, 0, get_mappane().getWidth(), get_mappane().getHeight());
				//System.out.println("!!!!!EXECUTING KICKREPAINT!!!!!");
				Variables.getMapFrame().repaint();
				Variables.getMapFrame().validate();
			}
		});
		/*if(m_n_repaints % 20 == 0) {
			this.repaint();
			m_n_repaints = 0;
		}
		else {
			get_mappane().repaint();
		}*/
		//m_n_repaints ++;
	}

	public void actionPerformed(ActionEvent e) {
		if("act_set_active_shape".equals(e.getActionCommand())) {
			set_active_shape((ShapeStruct)e.getSource());
		}
		else if("act_add_polypoint".equals(e.getActionCommand())) {
			MapPoint p;
			
			p = new MapPoint(Variables.getNavigation(), new MapPointPix(get_current_mousepos().x, get_current_mousepos().y));
			
				
			//((SendPropertiesPolygon)get_activesending().get_sendproperties()).get_polygon().add_coor(new Double(p.get_lon()), new Double(p.get_lat()));
			try
			{
				get_active_shape().typecast_polygon().add_coor(new Double(p.get_lon()), new Double(p.get_lat()));
			}
			catch(Exception err)
			{}
			if(PAS.get_pas() != null)
				PAS.get_pas().kickRepaint();
			else
				kickRepaint();
		}
		else if("act_rem_polypoint".equals(e.getActionCommand())) {
			get_active_shape().typecast_polygon().rem_last_coor();
			if(PAS.get_pas() != null)
				PAS.get_pas().kickRepaint();
			else
				kickRepaint();
		}
		else if("act_insat_polypoint".equals(e.getActionCommand())) {
			get_active_shape().typecast_polygon().set_activepoint(get_current_snappoint());
			if(PAS.get_pas() != null)
				PAS.get_pas().kickRepaint();
			else
				kickRepaint();
		}
		else if("act_delat_polypoint".equals(e.getActionCommand())) {
			get_active_shape().typecast_polygon().remove_at(get_current_snappoint().get_polyindex());
			if(PAS.get_pas() != null)
				PAS.get_pas().kickRepaint();
			else
				kickRepaint();
		}
		else if("act_moveat_polypoint".equals(e.getActionCommand())) {
			get_active_shape().typecast_polygon().move_at(get_current_snappoint().get_polyindex());
			set_mode_objectmove(get_current_snappoint());
		}
		else if("act_reverse_polypoints".equals(e.getActionCommand())) {
			get_active_shape().typecast_polygon().reverse_coor_order();
			if(PAS.get_pas() != null)
				PAS.get_pas().kickRepaint();
			else
				kickRepaint();
		}
		else if("act_set_ellipse_center".equals(e.getActionCommand())) {
			MapPoint p = (MapPoint)e.getSource();
			try {
				get_active_shape().typecast_ellipse().set_ellipse_center(Variables.getNavigation(), p);
			} catch(Exception err) {  }
		}
		else if("act_set_ellipse_corner".equals(e.getActionCommand())) {
			MapPoint p = (MapPoint)e.getSource();
			try {
				get_active_shape().typecast_ellipse().set_ellipse_corner(Variables.getNavigation(), p);
			} catch(Exception err) { }
		}
		else if("act_set_polygon_ellipse_center".equals(e.getActionCommand()))
		{
			MapPoint p = (MapPoint)e.getSource();
			try {
				get_active_shape().typecast_polygon().set_ellipse_center(Variables.getNavigation(), p);
			} catch(Exception err) {
				err.printStackTrace();
			}			
		}
		else if("act_set_polygon_ellipse_corner".equals(e.getActionCommand()))
		{
			MapPoint p = (MapPoint)e.getSource();
			try {
				get_active_shape().typecast_polygon().set_ellipse_corner(Variables.getNavigation(), p);
			} catch(Exception err) { 
				err.printStackTrace();
			}			
		}
		else if("act_onmouseover_houses".equals(e.getActionCommand())) {
			ArrayList<HouseItem> arr = (ArrayList<HouseItem>)e.getSource();
			if(arr.size() > 0) {
				set_mouseoverhouse(arr);
				if(PAS.get_pas() != null)
					PAS.get_pas().kickRepaint();
				else
					kickRepaint();
				//System.out.println("set_mouseoverhouse");
			} else
				set_mouseoverhouse(null);
		}
		else if("act_mousemove".equals(e.getActionCommand())) {
			int n_x, n_y;
			n_x = ((MapPoint)e.getSource()).get_x();
			n_y = ((MapPoint)e.getSource()).get_y();
			try {
				set_current_mousepoint(new Point(n_x, n_y));
				/*try {
					PolySnapStruct p = PAS.get_pas().get_sendcontroller().snap_to_point(new Point(n_x, n_y), 10);
					if(p != null) { //do snap
						SnapMouseEvent mouseevent = new SnapMouseEvent(this, 0, System.currentTimeMillis(), 16, p.p().x, p.p().y, 0, false);
						try {
							PAS.get_pas().get_mappane().get_actionhandler().mouseMoved(mouseevent);
							set_current_snappoint(p);
						} catch(Exception err) {
							System.out.println(err.getMessage());
							err.printStackTrace();
							Error.getError().addError("MapFrame","Exception in actionPerformed",err,1);
						}
						//get_pas().get_mappane().robot_movecursor(p);
					} else {
						set_current_snappoint(null);
					}
				} catch(Exception err) {
					System.out.println(err.getMessage());
					err.printStackTrace();
					Error.getError().addError("MapFrame","Exception in actionPerformed",err,1);
				}*/
				//PAS.get_pas().kickRepaint();
			} catch(Exception err) {
				System.out.println(err.getMessage());
				err.printStackTrace();
				Error.getError().addError("MapFrame","Exception in actionPerformed",err,1);
			}
		}
		/*else if("act_check_mousesnap".equals(e.getActionCommand())) {
			int n_x, n_y;
			n_x = ((MapPoint)e.getSource()).get_x();
			n_y = ((MapPoint)e.getSource()).get_y();			
			try {
				PolySnapStruct p = PAS.get_pas().get_sendcontroller().snap_to_point(new Point(n_x, n_y), 10);
				if(p != null) { //do snap
					SnapMouseEvent mouseevent = new SnapMouseEvent(this, 0, System.currentTimeMillis(), 16, p.p().x, p.p().y, 0, false);
					try {
						PAS.get_pas().get_mappane().get_actionhandler().mouseMoved(mouseevent);
						set_current_snappoint(p);
					} catch(Exception err) {
						System.out.println(err.getMessage());
						err.printStackTrace();
						Error.getError().addError("MapFrame","Exception in actionPerformed",err,1);
					}
					//get_pas().get_mappane().robot_movecursor(p);
				} else {
					set_current_snappoint(null);
				}
			} catch(Exception err) {
				System.out.println(err.getMessage());
				err.printStackTrace();
				Error.getError().addError("MapFrame","Exception in actionPerformed",err,1);
			}			
		}*/
		else if("act_mouse_rightclick".equals(e.getActionCommand())) {
			if(get_current_snappoint()!=null) {
				if(get_current_snappoint().isActive()) {//only activate menu for active sending
					m_polypoint_popup.enable_insert(!get_current_snappoint().isLastPoint());
					m_polypoint_popup.pop(this, get_current_snappoint().p());
				}
			}
		}
		//this is executed by MapFrameActionHandler.mouseMoved if act_mousemove has detected a snap
		else if("act_mousesnap".equals(e.getActionCommand())) {
			int n_x, n_y;
			n_x = ((MapPoint)e.getSource()).get_x();
			n_y = ((MapPoint)e.getSource()).get_y();
			set_current_mousepoint(new Point(n_x, n_y));
			if(PAS.get_pas() != null)
				PAS.get_pas().kickRepaint();
			else
				kickRepaint();
		}
	}


	@Override
	public void componentHidden(ComponentEvent e) {
		
	}


	@Override
	public void componentMoved(ComponentEvent e) {
		
	}


	@Override
	public void componentResized(ComponentEvent e) {
		int w = getWidth();
		int h = getHeight();
		if(w<=0 || h<=0)
		{
			return;
		}

		setPreferredSize(new Dimension(w, h));
	}


	@Override
	public void componentShown(ComponentEvent e) {
		
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(e.getSource().equals(this))
		{
			System.out.println(e.getWheelRotation());
			if(e.getWheelRotation()<0)
			{
				get_navigation().exec_quickzoom(Navigation.Zoom.ZOOMIN);
			}
			else if(e.getWheelRotation()>0)
				get_navigation().exec_quickzoom(Navigation.Zoom.ZOOMOUT);
		}
	}
}
