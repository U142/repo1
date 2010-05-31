package no.ums.pas.maps;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.*;


import java.util.*;
import java.util.List;
//import PAS.*;

import no.ums.pas.PAS;
import no.ums.pas.core.logon.DeptArray;
import no.ums.pas.core.logon.DeptInfo;
import no.ums.pas.core.mainui.HouseEditorDlg;
import no.ums.pas.maps.MapFrame.MapOverlay;
import no.ums.pas.maps.defines.*;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.CoorConverter;




public class MapFrameActionHandler implements ActionListener, MouseListener, MouseMotionListener, KeyListener {
	public class ActionHouseSelect extends Object {
		public boolean _b1;
		public boolean _b2;
		ActionHouseSelect(boolean b1, boolean b2) {
			_b1 = b1;
			_b2 = b2;
		}
	}	
	
	Dimension m_dim_mousedownpos;
	Dimension m_dim_mouseuppos;
	Dimension m_dim_cursorpos;
	protected MapPointPix m_pan_drag = new MapPointPix(0,0);
	public MapPointPix getPanDragPoint() { return m_pan_drag; }
	public enum HOTKEYS
	{
		SHIFT,
		CTRL,
		ALT,
	};
	protected Hashtable<HOTKEYS, Boolean> hot_keys = new Hashtable<HOTKEYS, Boolean>();
	public boolean isKeyHot(HOTKEYS key)
	{
		if(!hot_keys.containsKey(key))
			return false;
		return hot_keys.get(key);
	}
	
	public void updatePanDrag()
	{
		if(getCursorPos()!=null && getMousedownPos()!=null)
		{
			m_pan_drag.set_x(getCursorPos().width - getMousedownPos().width);
			m_pan_drag.set_y(getCursorPos().height - getMousedownPos().height);
		}
	}
	public void resetPanDrag()
	{
		m_pan_drag.set_x(0);
		m_pan_drag.set_y(0);
	}
	
	public void updateOverlay() {
		for(int i=0;i<get_mappane().m_overlays.size();++i)
			((MapOverlay)get_mappane().m_overlays.get(i)).b_needupdate = true;
		get_mappane().start_gsm_coverage_loader();
	}

	boolean m_b_isdragging = false;
	private MapFrame m_map;
	protected MapFrame get_mappane() { return m_map; }
	private ArrayList<ActionListener> m_callback;
	protected ArrayList<ActionListener> get_callback() { return m_callback; }
	protected HouseEditorDlg m_houseeditordlg = null;
	protected boolean get_enable_snap() { return m_b_enable_snap; }
	private boolean m_b_enable_snap = false;
	
	public MapFrameActionHandler(MapFrame map, boolean b_enable_snap) {
		m_map = map;
		m_callback = new ArrayList<ActionListener>();
		m_b_enable_snap = b_enable_snap;
		m_dim_cursorpos = new Dimension();
	}
	
	public void addActionListener(ActionListener callback) {
		m_callback.add(callback);
	}
	
	private void set_isdragging(boolean b) { 
		m_b_isdragging = b;
		System.out.println("dragging = " + b);
	}
	public boolean get_isdragging() { return m_b_isdragging; }
	
	private void setCursorPos(int x, int y)
	{
		m_dim_cursorpos = new Dimension(x,y);	
	}
	private void setMousedownPos(int x, int y)
	{
		m_dim_mousedownpos = new Dimension(x,y);	
	}
	private void setMouseupPos(int x, int y)
	{
		m_dim_mouseuppos = new Dimension(x,y);	
	}
	public Dimension getMousedownPos() { return m_dim_mousedownpos; }
	public Dimension getMouseupPos() { return m_dim_mouseuppos; }
	public Dimension getCursorPos() { return m_dim_cursorpos; }

	/*KEY-LISTENER*/
	public void keyTyped(KeyEvent e)
	{
		//key(e.getKeyCode(), true);
	}
	public void keyPressed(KeyEvent e)
	{
		//get_pas().add_event("Key");
		key(e.getKeyCode(), true);
	}
	public void keyReleased(KeyEvent e)
	{
		key(e.getKeyCode(), false);
	}
	public synchronized void key(int n_keycode, boolean b_down)
	{
		switch(n_keycode)
		{
			case KeyEvent.VK_CONTROL:
				if(b_down)
				{
					hot_keys.put(HOTKEYS.CTRL, true);
					if(get_mappane().get_mode() == MapFrame.MAP_MODE_SENDING_POLY ||
						get_mappane().get_mode() == MapFrame.MAP_MODE_SENDING_ELLIPSE ||
						get_mappane().get_mode() == MapFrame.MAP_MODE_SENDING_ELLIPSE_POLYGON ||
						get_mappane().get_mode() == MapFrame.MAP_MODE_PAINT_RESTRICTIONAREA)
					{
						
					}
					else if(get_mappane().get_mode() != MapFrame.MAP_MODE_HOUSESELECT)
						addAction("act_toggle_houseselect", new ActionHouseSelect(true, true));
						//PAS.get_pas().get_mainmenu().toggle_houseselect(true, true);
				}
				else
				{
					if(get_mappane().get_mode() == MapFrame.MAP_MODE_HOUSESELECT)
						addAction("act_toggle_houseselect", new ActionHouseSelect(false, true));
					hot_keys.put(HOTKEYS.CTRL, false);
				}
					//PAS.get_pas().get_mainmenu().toggle_houseselect(false, true);	
				break;
			case KeyEvent.VK_SHIFT:
				if(b_down)
				{
					hot_keys.put(HOTKEYS.SHIFT, true);
				}
				else
				{
					hot_keys.put(HOTKEYS.SHIFT, false);					
				}
				break;
			case KeyEvent.VK_ALT:
				if(b_down)
				{
					hot_keys.put(HOTKEYS.ALT, true);
				}
				else
				{
					hot_keys.put(HOTKEYS.ALT, false);					
				}
				break;
			case KeyEvent.VK_NUMPAD4:
			case KeyEvent.VK_LEFT:
				if(b_down)
					get_mappane().get_navigation().exec_pan_direction(Navigation.Pan.WEST);
				break;
			case KeyEvent.VK_NUMPAD6:
			case KeyEvent.VK_RIGHT:
				if(b_down)
					get_mappane().get_navigation().exec_pan_direction(Navigation.Pan.EAST);
				break;
			case KeyEvent.VK_NUMPAD2:
			case KeyEvent.VK_DOWN:
				if(b_down)
					get_mappane().get_navigation().exec_pan_direction(Navigation.Pan.SOUTH);
				break;
			case KeyEvent.VK_NUMPAD8:
			case KeyEvent.VK_UP:
				if(b_down)
					get_mappane().get_navigation().exec_pan_direction(Navigation.Pan.NORTH);
				break;
			case KeyEvent.VK_NUMPAD1:
				if(b_down)
					get_mappane().get_navigation().exec_pan_direction(Navigation.Pan.SOUTHWEST);
				break;
			case KeyEvent.VK_NUMPAD3:
				if(b_down)
					get_mappane().get_navigation().exec_pan_direction(Navigation.Pan.SOUTHEAST);
				break;
			case KeyEvent.VK_NUMPAD7:
				if(b_down)
					get_mappane().get_navigation().exec_pan_direction(Navigation.Pan.NORTHWEST);
				break;
			case KeyEvent.VK_NUMPAD9:
				if(b_down)
					get_mappane().get_navigation().exec_pan_direction(Navigation.Pan.NORTHEAST);
				break;
			case KeyEvent.VK_ADD:
				if(b_down)
					get_mappane().get_navigation().exec_quickzoom(Navigation.Zoom.ZOOMIN);
				break;
			case KeyEvent.VK_SUBTRACT:
				if(b_down)
					get_mappane().get_navigation().exec_quickzoom(Navigation.Zoom.ZOOMOUT);
				break;
			case KeyEvent.VK_DELETE:
				if(b_down) {
					if(get_mappane().get_mode()==MapFrame.MAP_MODE_SENDING_POLY ||
						get_mappane().get_mode()==MapFrame.MAP_MODE_PAINT_RESTRICTIONAREA) {
						//ActionEvent action = new ActionEvent(new String(""), ActionEvent.ACTION_PERFORMED, "act_rem_polypoint");
						//PAS.get_pas().get_sendcontroller().actionPerformed(action);
						addAction("act_rem_polypoint", new String(""));
					}
				}
				break;
			case KeyEvent.VK_T:
				PAS.get_pas().get_sendcontroller().get_activesending().get_sendproperties().get_shapestruct().typecast_polygon().ellipseToRestrictionlines(PAS.get_pas().get_userinfo().get_departments().get_combined_restriction_shape().get(0).typecast_polygon());
				break;
		}
	}
	
	public synchronized void addAction(String sz_action, Object action) {
		ActionEvent e = new ActionEvent(action, ActionEvent.ACTION_PERFORMED, sz_action);
		for(int i=0; i < get_callback().size(); i++) 
			((ActionListener)get_callback().get(i)).actionPerformed(e);
	}
	
	/*ACTION-LISTENER*/
	public synchronized void actionPerformed(ActionEvent e)  {	
		//addAction("act_mousesnap", e);
	}
	
	
	public class MouseOverWait extends Thread {
		int _waitms = 100;
		public MouseEvent _callevent;
		public boolean running = false;
		protected long lastupdate;
		
		public MouseOverWait(MouseEvent e) {
			super("MouseOverWait thread");
			_callevent = e;
		}
		
		public synchronized void setCallEvent(MouseEvent e)
		{
			_callevent = e;
			lastupdate = e.getWhen();
		}
		
		public void run() {
			running = true;
			while(1==1)
			{
				try {
					Thread.sleep(_waitms);
					check_snap(_callevent);
					if(System.currentTimeMillis() - lastupdate > 1000)
						break;
				} catch(InterruptedException e) {	
				}
			}
			running = false;
		}
		
	}
	
	public MouseOverWait m_mouseoverwait = null;
	//private MouseEvent prev_event = null;
	public void execMouseOver(MouseEvent e) {
		if(m_mouseoverwait!=null)
		{
			if(m_mouseoverwait.running)
			{
				//if(prev_event!=null)
				{
					//if(prev_event.getX()!=e.getX() && prev_event.getY()!=e.getY())
						m_mouseoverwait.setCallEvent(e);
				}
				//prev_event = e;
				return;
			}
		}
		m_mouseoverwait = new MouseOverWait(e);
		m_mouseoverwait.start();
	}
	public void killMouseOver() {
		try {
			//m_mouseoverwait.join(); //ensure it's finished before starting new thread
		} catch(Exception e) { }
		try {
			m_mouseoverwait.interrupt();
		} catch(Exception e) {	}
	}
	public void resetMouseOver() {
		//get_mappane().set_current_snappoint(null);
		if(get_mappane().get_current_snappoint()!=null)
			get_mappane().actionPerformed(new ActionEvent(new ArrayList<HouseItem>(), ActionEvent.ACTION_PERFORMED, "act_onmouseover_houses"));
	}
	
	/*MOUSE-MOTION-LISTENER*/
	public synchronized void mouseMoved(java.awt.event.MouseEvent e)
	{
		if(get_mappane().get_mouseoverhouse()!=null)
			get_mappane().set_mouseoverhouse(null);
		switch(get_mappane().get_mode())
		{
			case MapFrame.MAP_MODE_PAN_BY_DRAG:
				setCursorPos(e.getX(), e.getY());
				if(get_isdragging())
				{
					updatePanDrag();
					PAS.get_pas().kickRepaint();
					//if(get_mappane().m_overlays!=null)
						//updateOverlay();
				}
				//setMouseupPos(e.getX(), e.getY());
				break;
			case MapFrame.MAP_MODE_ZOOM:
				setCursorPos(e.getX(), e.getY());
				if(get_isdragging()) {
					//get_mappane().get_drawthread().setRepaint(get_mappane().get_mapimage());
					//PAS.get_pas().kickRepaint();
				}
					//PAS.get_pas().kickRepaint();
				break;
			case MapFrame.MAP_MODE_HOUSESELECT:
				
				break;
			case MapFrame.MAP_MODE_SENDING_POLY:
				if(get_enable_snap()) {
					//check_snap(e);
					//execMouseOver(e);
				}
				checkSendingRestriction(false, RESTRICTION_MODE.FORCE_INSIDE, -1);
				break;
			case MapFrame.MAP_MODE_PAINT_RESTRICTIONAREA:
				checkSendingRestriction(false, RESTRICTION_MODE.FORCE_OUTSIDE, -1);
				break;
			case MapFrame.MAP_MODE_SENDING_ELLIPSE_POLYGON:
				if(get_isdragging())
					checkSendingRestriction(false, RESTRICTION_MODE.FORCE_INSIDE, -1);
				break;
			case MapFrame.MAP_MODE_SENDING_ELLIPSE:
				break;
			case MapFrame.MAP_MODE_OBJECT_MOVE:
				try {
					if(get_mappane().get_current_object()!=null) {
						MapPoint mp = new MapPoint(get_mappane().get_navigation(), new MapPointPix(e.getX(), e.getY()));					
						get_mappane().get_current_object().set_pos(new Point(e.getX(), e.getY()), mp);
					}
				} catch(Exception err) {
					//PAS.get_pas().add_event("Error: " + err.getMessage(), err);
					Error.getError().addError("MapFrameActionHandler","Exception in mouseMoved",err,1);
				}
				//check_snap(e);
				get_mappane().get_drawthread().setRepaint(get_mappane().get_mapimage());
				break;
			case MapFrame.MAP_MODE_HOUSEEDITOR_:
				//execMouseOver(e);
				//check_snap(e);
				break;
		}
		try {
			//addAction("act_mousemoved", new Point(e.getX(), e.getY()));
			mouse_move(e);
			//System.out.println("act_mousemoved");
		} catch(Exception err) {
			/*System.out.println(err.getMessage());
			err.printStackTrace();
			Error.getError().addError("MapFrameActionHandler","Exception in mouseMoved",err,1);*/
		}
		//addAction("act_checkmouseover", new Point(e.getX(), e.getY()));
		//PAS.get_pas().get_housecontroller().check_mouseover(e.getX(), e.getY());
	}
	
	/**
	 * Points where poly-drawing is outside clip-area/restriction area
	 */
	List<MapPointLL> intersects = new ArrayList<MapPointLL>();
	List<MapPointLL> intersects_last = new ArrayList<MapPointLL>();
	List<MapPointLL> intersects_first = new ArrayList<MapPointLL>();
	
	enum PAINTMODE
	{
		NORMAL,
		RESTRICTED,
		MERGE,
		PIN_TO_POINT,
		PIN_TO_BORDER,
	};
	protected PAINTMODE paintmode = PAINTMODE.NORMAL;
	protected void setPaintMode(PAINTMODE m)
	{
		paintmode = m;
	}

	public enum RESTRICTION_MODE
	{
		FORCE_INSIDE,
		FORCE_OUTSIDE,
	};
	
	/**
	 * 
	 * @param b_click - if called from mouse click
	 * @param mode - force polygon to be painted by this rule
	 * @param n_deptpk - specify department restriction polygons. if <1, use the combined restriction polygon
	 * @return true if legal action, else return false
	 */
	protected boolean checkSendingRestriction(boolean b_click, RESTRICTION_MODE mode, int n_deptpk)
	{
		if(m_dim_cursorpos==null)
			return false;
		MapPoint p = new MapPoint(get_mappane().get_navigation(), new MapPointPix(m_dim_cursorpos.width, m_dim_cursorpos.height));
		//System.out.println("x = " + m_dim_cursorpos.width + " , y = " + m_dim_cursorpos.height);
		//boolean b = PAS.get_pas().get_sendcontroller().get_activesending().get_sendproperties().get_shapestruct().pointInsideShape(p.get_mappointll());
		//List<ShapeStruct> list = PAS.get_pas().get_userinfo().get_current_department().get_restriction_shapes();

		List<ShapeStruct> list = null;
		DeptArray depts = PAS.get_pas().get_userinfo().get_departments();
		if(n_deptpk<1) //use combined restriction area
		{
			list = PAS.get_pas().get_userinfo().get_departments().get_combined_restriction_shape();
		}
		else //use specified department's restriction area
		{
			for(int d = 0; d < depts.size(); d++)
			{
				DeptInfo department = (DeptInfo)depts.get(d);
				if(n_deptpk==department.get_deptpk())
				{
					list.addAll(((DeptInfo)depts.get(d)).get_restriction_shapes());
					break;
				}
			}
		}
		//for(int n_dept = 0; n_dept < depts.size(); n_dept++)
		{
			//List<ShapeStruct> list = ((DeptInfo)depts.get(n_dept)).get_restriction_shapes();
			//List<ShapeStruct> list = ;
			
			for(int i=0;i < list.size(); i++)
			{
				if(list.get(i).isHidden())
					continue;
				
				//check line intersects with last line (last polypoint and mouse pos)
				PolygonStruct current_polygon = null;
				try
				{
					current_polygon = PAS.get_pas().get_sendcontroller().get_activesending().get_sendproperties().get_shapestruct().typecast_polygon();
				}
				catch(Exception e)
				{
					current_polygon = PAS.get_pas().get_parmcontroller().get_shape().typecast_polygon();
				}
				if(get_mappane().get_mode()==MapFrame.MAP_MODE_SENDING_ELLIPSE_POLYGON)
				{
					current_polygon.ellipseToRestrictionlines(list.get(i).typecast_polygon());
					continue;
				}
				
				boolean b = list.get(i).pointInsideShape(p.get_mappointll());
				switch(mode)
				{
				case FORCE_OUTSIDE: //if cursor is outside and mode is to paint outside
					b = !b;
					break;
				}
				//b=true; //to paint outside
				MapPointLL nearest_point = list.get(i).typecast_polygon().findNearestPolypoint(p.get_mappointll());
				//PAS.get_pas().get_navigation().
				if(b)
				{
	
	
					MapPointLL ll1 = current_polygon.getLastPoint();
					MapPointLL ll2 = p.get_mappointll();
					if(ll1!=null && ll2!=null)
					{
						intersects_last = list.get(i).typecast_polygon().LineIntersect(ll1, ll2);
						//the user clicked, we need to configure polygon automatically to obay restriction area
						if((intersects_last.size()>0 || isKeyHot(HOTKEYS.CTRL)) && b_click) 
						{
							if(isKeyHot(HOTKEYS.CTRL))
							{
								paintmode = PAINTMODE.PIN_TO_BORDER;
							}
							//this variant will obey restrictions, but will draw a straight line within the restriction polygon
							/*for(int point=0; point < intersects_last.size() && (intersects_last.size() % 2)==0; point+=2)
							{
								PAS.get_pas().get_sendcontroller().get_activesending().get_sendproperties().get_shapestruct().typecast_polygon().FollowRestrictionLines(p.get_mappointll(), intersects_last.get(point), intersects_last.get(point+1), list.get(i).typecast_polygon());
							}*/
							//this variant will obey restrictions and follow the polygons borders
							MapPointLL prev_polypoint = new MapPointLL(current_polygon.get_coor_lon(current_polygon.get_size()-1),
									current_polygon.get_coor_lat(current_polygon.get_size()-1));
							MapPointLL nearest_point_from_last = list.get(i).typecast_polygon().findNearestPolypoint(prev_polypoint);
							switch(paintmode)
							{
							case PIN_TO_POINT:
							case PIN_TO_BORDER:
								MapPointLL snap_end_to = new MapPointLL(list.get(i).typecast_polygon().get_coor_lon(nearest_point.getPointReference()),
																		list.get(i).typecast_polygon().get_coor_lat(nearest_point.getPointReference()));
								prev_polypoint.setPointReference(nearest_point_from_last.getPointReference());
								snap_end_to.setPointReference(nearest_point.getPointReference());
								current_polygon.FollowRestrictionLines(
										p.get_mappointll(), //cursor
										nearest_point_from_last,
										snap_end_to, //last intersection
										list.get(i).typecast_polygon(),
										false,
										false,
										true);
									return false;
							default:
								if((intersects_last.size() % 2)==0 && intersects_last.size()>0)
								{
									current_polygon.FollowRestrictionLines(
											p.get_mappointll(), 
											intersects_last.get(0), 
											intersects_last.get(intersects_last.size()-1), 
											list.get(i).typecast_polygon(),
											true,
											true,
											false);
								}
								else if(intersects_last.size()>0)//assume the prev point is on top of another, no intersect
								{
									current_polygon.FollowRestrictionLines(
											p.get_mappointll(), //cursor
											nearest_point_from_last, //last intersection
											intersects_last.get(intersects_last.size()-1),
											list.get(i).typecast_polygon(),
											true,
											true,
											false);	
								}
								break;
							}
							
							return true;
						}
					}
					//check line intersects with last point to the first polypoint
					ll1 = current_polygon.typecast_polygon().getFirstPoint();//PAS.get_pas().get_sendcontroller().get_activesending().get_sendproperties().get_shapestruct().typecast_polygon().getFirstPoint();
					if(ll1!=null && ll2!=null)
					{
						intersects_first = list.get(i).typecast_polygon().LineIntersect(ll1, ll2);
					}
					intersects.addAll(intersects_first);
					intersects.addAll(intersects_last);
					
					double pixel_dist = PAS.get_pas().get_navigation().calc_pix_distance(nearest_point.getDegreeDistance());
					//System.out.println(pixel_dist+"");
					if(nearest_point!=null && pixel_dist < 15)
					{
						setPaintMode(PAINTMODE.PIN_TO_POINT);
						get_mappane().set_cursor(get_mappane().get_cursor_draw_pin_to_border());
						if(b_click)
						{
							current_polygon.add_coor(nearest_point.get_lon(), nearest_point.get_lat());
							return false;
						}
						return true;
					}
					else if(nearest_point!=null && nearest_point.getMeasurementReference() < 500)
					{
						setPaintMode(PAINTMODE.PIN_TO_BORDER);
						get_mappane().set_cursor(get_mappane().get_cursor_draw_pin_to_border());
						return true;
					}
					else if(intersects_last.size()==0 || ll1==null)
					{
						setPaintMode(PAINTMODE.NORMAL);
						get_mappane().set_cursor(get_mappane().get_cursor_draw());					
						return true;
					}
					else if(intersects_last.size()>0 && !b_click)
					{
						setPaintMode(PAINTMODE.MERGE);
						get_mappane().set_cursor(get_mappane().get_cursor_draw_merge());
						return true;
					}
				}
			}
		}
		get_mappane().setCursor(get_mappane().get_cursor_illegal_draw());
		return false;
	}
	
	private void mouse_move(MouseEvent e) {
		if(e instanceof no.ums.pas.send.SnapMouseEvent) {
			do_snap(e);
		} else {
			MapPoint p = new MapPoint(get_mappane().get_navigation(), new MapPointPix(e.getX(), e.getY()));
			m_dim_cursorpos.width = e.getX();
			m_dim_cursorpos.height = e.getY();
			addAction("act_mousemove", p);//new Point(e.getX(), e.getY()));
			if(!get_isdragging()) {
				execMouseOver(e);
			}
			//only repaint if
			switch(get_mappane().get_mode())
			{
			case MapFrame.MAP_MODE_SENDING_POLY:
			case MapFrame.MAP_MODE_SENDING_ELLIPSE:
			case MapFrame.MAP_MODE_SENDING_ELLIPSE_POLYGON:
			case MapFrame.MAP_MODE_OBJECT_MOVE:
			case MapFrame.MAP_MODE_PAINT_RESTRICTIONAREA:
				PAS.get_pas().kickRepaint();
				break;
			}
		}
	}
	private void check_snap(MouseEvent e) {
		MapPoint p = new MapPoint(get_mappane().get_navigation(), new MapPointPix(e.getX(), e.getY()));
		addAction("act_check_mousesnap", p);
	}
	
	private void do_snap(MouseEvent e) {
		try {
			MapPoint p = new MapPoint(get_mappane().get_navigation(), new MapPointPix(e.getX(), e.getY()));
//			ActionEvent action = null;
			//if(e.getClass().getName().equals("SnapMouseEvent")) {
			//if(e instanceof Send.SnapMouseEvent) {
			//if(e.getClass() instanceof SnapMouseEvent) {
				//action = new ActionEvent(p, ActionEvent.ACTION_PERFORMED, "act_mousesnap");
			addAction("act_mousesnap", p);
			//if(p!=null)
			//	PAS.get_pas().kickRepaint();

				/*switch(get_mappane().get_mode()) {
					case MapFrame.MAP_MODE_SENDING_POLY:
						addAction("act_mousesnap", p);
						break;
					case MapFrame.MAP_MODE_HOUSEEDITOR_:
					case MapFrame.MAP_MODE_PAN:
					case MapFrame.MAP_MODE_ZOOM:
						addAction("act_mousesnap_house", p);
						break;
				}*/
			/*} else {
				//action = new ActionEvent(p, ActionEvent.ACTION_PERFORMED, "act_mousemove");
				switch(get_mappane().get_mode()) {
					case MapFrame.MAP_MODE_SENDING_POLY:
						addAction("act_mousemove", p);
						break;
					case MapFrame.MAP_MODE_HOUSEEDITOR_:
						addAction("act_mousesnap_house", p);
						break;
				}
			}*/
		} catch(Exception err) {
			System.out.println(err.getMessage());
			err.printStackTrace();
			Error.getError().addError("MapFrameActionHandler","Exception in check_snap",err,1);
		}
		//PAS.get_pas().get_sendcontroller().actionPerformed(action);
		//get_mappane().get_drawthread().setRepaint(get_mappane().get_mapimage());
	}
	public synchronized void mouseDragged(java.awt.event.MouseEvent e)
	{
		switch(get_mappane().get_mode())
		{
			case MapFrame.MAP_MODE_ZOOM:
			//case MapFrame.MAP_MODE_SENDING_ELLIPSE:
				setCursorPos(e.getX(), e.getY());
				if(get_isdragging()) {
					if(PAS.get_pas()!= null)
						PAS.get_pas().kickRepaint();
					else
						m_map.kickRepaint();
					//get_mappane().get_drawthread().setRepaint(get_mappane().get_mapimage());
				}
				break;
			case MapFrame.MAP_MODE_SENDING_ELLIPSE:
				if(get_isdragging()) {
					try {
						MapPoint p = new MapPoint(get_mappane().get_navigation(), new MapPointPix(e.getX(), e.getY()));
						addAction("act_set_ellipse_corner", p);
					} catch(Exception err) {
						System.out.println(err.getMessage());
						err.printStackTrace();
						Error.getError().addError("MapFrameActionHandler","Exception in mouseDragged",err,1);
					}
				}
				break;
			case MapFrame.MAP_MODE_SENDING_ELLIPSE_POLYGON:
				if(get_isdragging()) {
					try {
						MapPoint p = new MapPoint(get_mappane().get_navigation(), new MapPointPix(e.getX(), e.getY()));
						addAction("act_set_polygon_ellipse_corner", p);
						if(get_isdragging())
						{
							checkSendingRestriction(false, RESTRICTION_MODE.FORCE_INSIDE, -1);
							//System.out.println("Checking");
						}
					}
					catch(Exception err)
					{
						
					}
				}
				break;
			case MapFrame.MAP_MODE_PAN_BY_DRAG:
				setCursorPos(e.getX(), e.getY());
				if(get_isdragging())
				{
					updatePanDrag();
					if(PAS.get_pas()!= null)
						PAS.get_pas().kickRepaint();
					else
						m_map.kickRepaint();
				}
				break;
		}
	}
	
	/*MOUSE-LISTENER*/	
	public synchronized void mousePressed(java.awt.event.MouseEvent e)
	{
		switch(e.getButton())
		{
			case MouseEvent.BUTTON1:
				setMousedownPos(e.getX(), e.getY());
				if(get_mappane().get_mode() == MapFrame.MAP_MODE_PAN) {
					try
					{
						get_mappane().get_navigation().exec_pan(getMousedownPos());
						//if(get_mappane().m_overlays!=null)
							//updateOverlay();
					}
					catch(Exception err)
					{
						
					}
				}
				else if(get_mappane().get_mode() == MapFrame.MAP_MODE_PAN_BY_DRAG) {
					try
					{
						set_isdragging(true);
					}
					catch(Exception err)
					{
						
					}
				}
				else if(get_mappane().get_mode() == MapFrame.MAP_MODE_ZOOM) {
					set_isdragging(true); //m_b_isdragging = true;
				}
				else if(get_mappane().get_mode() == MapFrame.MAP_MODE_HOUSESELECT)
					addAction("act_search_houses", new Dimension(e.getX(), e.getY()));
					//PAS.get_pas().get_statuscontroller().search_houses(new Dimension(e.getX(), e.getY()));
				else if(get_mappane().get_mode() == MapFrame.MAP_MODE_SENDING_POLY) {
					MapPoint p = new MapPoint(get_mappane().get_navigation(), new MapPointPix(e.getX(), e.getY()));
					if(checkSendingRestriction(true, RESTRICTION_MODE.FORCE_INSIDE, -1))
					{
						addAction("act_add_polypoint", p);
					}
					PAS.get_pas().kickRepaint();
				}
				else if(get_mappane().get_mode() == MapFrame.MAP_MODE_PAINT_RESTRICTIONAREA) {
					MapPoint p = new MapPoint(get_mappane().get_navigation(), new MapPointPix(e.getX(), e.getY()));
					if(checkSendingRestriction(true, RESTRICTION_MODE.FORCE_OUTSIDE, -1))
					{
						addAction("act_add_polypoint", p);
					}
					PAS.get_pas().kickRepaint();					
				}
				else if(get_mappane().get_mode() == MapFrame.MAP_MODE_SENDING_ELLIPSE) {
					try {
						MapPoint p = new MapPoint(get_mappane().get_navigation(), new MapPointPix(e.getX(), e.getY()));
						addAction("act_set_ellipse_center", p);
						set_isdragging(true);
					} catch(Exception err) {
						System.out.println(err.getMessage());
						err.printStackTrace();
						Error.getError().addError("MapFrameActionHandler","Exception in mousePressed",err,1);
					}
				}
				else if(get_mappane().get_mode() == MapFrame.MAP_MODE_SENDING_ELLIPSE_POLYGON)
				{
					try {
						MapPoint p = new MapPoint(get_mappane().get_navigation(), new MapPointPix(e.getX(), e.getY()));
						addAction("act_set_polygon_ellipse_center", p);
						set_isdragging(true);
					}
					catch(Exception err) {
						
					}
				}
				else if(get_mappane().get_mode() == MapFrame.MAP_MODE_OBJECT_MOVE) {
					//release object
					get_mappane().get_current_object().setMoving(false);
					get_mappane().set_current_object(null);
					get_mappane().set_prev_mode();
					//get_pas().get_mappane().set_mode(MapFrame.MAP_MODE_PAN);					
				}
				else if(get_mappane().get_mode() == MapFrame.MAP_MODE_HOUSEEDITOR_) {
					//new or edit object
					MapPoint p;
					this.check_snap(e);
					if(get_mappane().get_mouseoverhouse() != null) {
						//System.out.println("House found at same location with " + get_mappane().get_mouseoverhouse().get_inhabitantcount() + " inhabitants");
						p = new MapPoint(get_mappane().get_navigation(), new MapPointLL(get_mappane().get_mouseoverhouse().get_lon(), get_mappane().get_mouseoverhouse().get_lat()));
					}
					else 
						p = new MapPoint(get_mappane().get_navigation(), new MapPointPix(e.getX(), e.getY()));
					/*if(m_houseeditordlg==null)
						m_houseeditordlg = new HouseEditorDlg(PAS.get_pas(), PAS.get_pas(), p, get_mappane().get_mouseoverhouse());
					else {
						m_houseeditordlg.reinit(p, get_mappane().get_mouseoverhouse());
					}*/
					addAction("set_houseeditor_coor", p);
					get_mappane().set_adredit(p.get_mappointll());
					PAS.get_pas().kickRepaint();
				}
				else if(get_mappane().get_mode() == MapFrame.MAP_MODE_ASSIGN_EPICENTRE) {
					MapPoint p;
					if(get_mappane().get_active_shape() != null) {
						p = new MapPoint(get_mappane().get_navigation(), new MapPointPix(e.getX(), e.getY()));
						get_mappane().get_active_shape().set_epicentre(p);
					}
					get_mappane().set_prev_mode();
					if(get_mappane().get_mode() == MapFrame.MAP_MODE_ASSIGN_EPICENTRE
							|| get_mappane().get_mode() == MapFrame.MAP_MODE_SENDING_POLY
							|| get_mappane().get_mode() == MapFrame.MAP_MODE_SENDING_ELLIPSE
							|| get_mappane().get_mode() == MapFrame.MAP_MODE_SENDING_ELLIPSE_POLYGON
							|| get_mappane().get_mode() == MapFrame.MAP_MODE_PAINT_RESTRICTIONAREA)
						get_mappane().set_mode(MapFrame.MAP_MODE_PAN);
				}
				break;
			case MouseEvent.BUTTON2:
			case MouseEvent.BUTTON3:
				setMousedownPos(e.getX(), e.getY());
				this.killMouseOver();
				check_snap(e); //when pressed, always check snap right away
				if(get_mappane().get_mode() == MapFrame.MAP_MODE_ZOOM) {
					get_mappane().get_navigation().exec_zoom_out(getMousedownPos());
					if(get_mappane().m_overlays!=null)
						updateOverlay();
				} else if(get_mappane().get_mode() == MapFrame.MAP_MODE_SENDING_POLY ||
						get_mappane().get_mode() == MapFrame.MAP_MODE_PAINT_RESTRICTIONAREA) {
					MapPoint p = new MapPoint(get_mappane().get_navigation(), new MapPointPix(e.getX(), e.getY()));
					addAction("act_mouse_rightclick", p);
					//ActionEvent action = new ActionEvent(p, ActionEvent.ACTION_PERFORMED, "act_mouse_rightclick");
					//PAS.get_pas().get_sendcontroller().actionPerformed(action);					
				}
				break;
		}
	}
	
	public synchronized void mouseReleased(java.awt.event.MouseEvent e)
	{
		switch(e.getButton())
		{
			case MouseEvent.BUTTON1:
				setMouseupPos(e.getX(), e.getY());
				if(get_mappane().get_mode() == MapFrame.MAP_MODE_ZOOM)
				{
					get_mappane().get_navigation().exec_zoom_in(getMousedownPos(), getMouseupPos());
					//if(get_mappane().m_overlays!=null)
						//updateOverlay();
				}
				else if(get_mappane().get_mode() == MapFrame.MAP_MODE_SENDING_ELLIPSE) {
					//set_isdragging(false);
					addAction("act_set_ellipse_complete", this);
				}
				else if(get_mappane().get_mode() == MapFrame.MAP_MODE_SENDING_ELLIPSE_POLYGON) {
					checkSendingRestriction(true, RESTRICTION_MODE.FORCE_INSIDE, -1);
				}
				else if(get_mappane().get_mode() == MapFrame.MAP_MODE_PAN_BY_DRAG) {
					MapPointLL ll = PAS.get_pas().get_navigation().pix_to_ll(getPanDragPoint().get_x(), getPanDragPoint().get_y());
					NavStruct nav = new NavStruct();
					nav._ubo = PAS.get_pas().get_navigation().getHeaderUBO() + ll.get_lat();
					nav._bbo = PAS.get_pas().get_navigation().getHeaderBBO() + ll.get_lat();
					nav._lbo = PAS.get_pas().get_navigation().getHeaderLBO() - ll.get_lon();
					nav._rbo = PAS.get_pas().get_navigation().getHeaderRBO() - ll.get_lon();
					
					/*System.out.println("delta y = " + ll.get_lat());
					double mod1 = Math.cos(CoorConverter.deg2rad * nav._bbo); //old
					double mod2 = Math.cos(CoorConverter.deg2rad * (nav._bbo + ll.get_lat()));
					//double modifier = Math.cos(CoorConverter.deg2rad * ll.get_lat());
					double modifier = mod2-mod1;
					System.out.println("modifier = " + modifier);
					nav._ubo += ll.get_lat();// - ll.get_lat() * modifier;
					nav._bbo += ll.get_lat();
					if(ll.get_lat()>0) //up
						nav._bbo -= ll.get_lat() * modifier;
					else
						nav._ubo += ll.get_lat() * modifier;*/
					/*if(ll.get_lat()>0)
					{
						nav._lbo += ll.get_lon() * modifier;// * Math.signum(ll.get_lat());
						nav._rbo += ll.get_lon() * modifier;// * Math.signum(ll.get_lat());
					}
					else
					{
						//modifier = Math.sin(CoorConverter.deg2rad * ll.get_lat());
						nav._lbo += ll.get_lon() * modifier;// * Math.signum(ll.get_lat());
						nav._rbo += ll.get_lon() * modifier;// * Math.signum(ll.get_lat());						
					}*/
					
					/*double ydiff = (nav._ubo - nav._bbo + ll.get_lat()) / 2; //center
					System.out.println("ydiff="+ydiff);
					double mod_for_aspect = Math.sin(CoorConverter.deg2rad * (ydiff));
					//double mod_for_aspect = Math.sin(CoorConverter.deg2rad * nav._bbo);
					System.out.println("mod for aspect = " + mod_for_aspect);
					double xdiff = (nav._rbo - nav._lbo);
					System.out.println("xdiff = " + xdiff * mod_for_aspect);
					boolean b_north_wins = true;
					if(Math.abs(nav._bbo) > Math.abs(nav._ubo))
						b_north_wins = false;
					double add = ll.get_lat() * mod_for_aspect;
					System.out.println("Add=" + add);
					if(ll.get_lat()>0)
					{
						//if(nav._bbo > 0)
						//	nav._bbo += ll.get_lat() * Math.abs(mod_for_aspect);
						//else
							nav._bbo += add;//ll.get_lat() * mod_for_aspect;							
					}
					else
					{
						//if(nav._bbo > 0)
						//	nav._bbo += ll.get_lat() * Math.abs(mod_for_aspect);
						//else
							nav._ubo += add;
					}*/
					
					PAS.get_pas().get_navigation().setNavigation(nav);
					get_mappane().load_map();
					PAS.get_pas().get_eastcontent().actionPerformed(new ActionEvent(PAS.get_pas().get_navigation(), ActionEvent.ACTION_PERFORMED, "act_maploaded"));
					//PAS.get_pas().get_navigation().gotoMap(nav);
					resetPanDrag();
					if(get_mappane().m_overlays!=null)
						updateOverlay();
				}
				break;
			case MouseEvent.BUTTON2:
			case MouseEvent.BUTTON3:
				break;	
		}
		//m_b_isdragging = false;
		set_isdragging(false);

	}
	
	public synchronized void mouseExited(java.awt.event.MouseEvent e)
	{
	
	}

	/*we need to focus the mappane so that shortcut-keys will work*/
	public synchronized void mouseEntered(java.awt.event.MouseEvent e)
	{
		get_mappane().requestFocusInWindow();
	}
	
	public synchronized void mouseClicked(java.awt.event.MouseEvent e)
	{
		switch(e.getButton())
		{
		}
	}
}