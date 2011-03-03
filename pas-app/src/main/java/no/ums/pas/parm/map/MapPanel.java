package no.ums.pas.parm.map;

import no.ums.pas.Draw;
import no.ums.pas.core.dataexchange.HTTPReq;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.defines.EllipseStruct;
import no.ums.pas.maps.defines.MapPoint;
import no.ums.pas.maps.defines.MapitemProperties;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.ums.tools.ImageLoader;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

//import java.util.logging.ConsoleHandler;

public class MapPanel extends JPanel implements ActionListener {
	public static final long serialVersionUID = 1;

	ArrayList<Object> m_shapes = new ArrayList<Object>();
	
	MapitemProperties m_mapproperties = new MapitemProperties();

	MapFrame m_mapframe = null;

	Draw m_draw = null;

	Navigation m_nav = null;

	HTTPReq m_http = null;

	String m_sz_sitename;

	ShapeStruct m_shape;

	Point m_mousepos;

	ArrayList<Double> coordList;
	
	ActionEvent local_event;

	// ToolBar Variables
	private JToolBar jtbToolbar;

	private JButton btn1, btn2, btn3, btn4;

	private void setMousePos(Point p) {
		m_mousepos = p;
	}

	private Point getMousePos() {
		return m_mousepos;
	}

	MapPanel(String sz_site) {
		m_sz_sitename = sz_site;
		createToolbar();
		this.repaint();
	}

	private void createToolbar() {
		jtbToolbar = new JToolBar("ToolBar");
		btn1 = new JButton("zoom");
		btn2 = new JButton("draw");
		btn3 = new JButton("clear");
		btn4 = new JButton("GoTo");
		
		btn1.addActionListener(this);
		btn2.addActionListener(this);
		btn3.addActionListener(this);
		btn4.addActionListener(this);

		jtbToolbar.add(btn1);
		jtbToolbar.add(btn2);
		jtbToolbar.add(btn3);
		jtbToolbar.add(btn4);
	}

	void exec() {
		m_draw = new DrawPoly(this, Thread.NORM_PRIORITY, 1024, 768);
		m_nav = new Navigation(this, 1024, 768);
		m_http = new HTTPReq(m_sz_sitename, m_nav);

		ImageLoader.setClassLoader(getClass().getClassLoader());
		m_mapframe = new MapFrame(1024, 768, m_draw, m_nav, m_http, false);
		m_mapframe.addActionListener(this);
		m_draw.set_mappane(m_mapframe);
		m_mapframe.initialize();
		//m_draw.start();
		m_shape = new PolygonStruct(new Dimension(1024, 768));

		this.setBounds(0, 0, 1280, 1024);
		this.setLayout(new BorderLayout());
		add(m_mapframe, BorderLayout.CENTER);
		add(jtbToolbar, BorderLayout.NORTH);

		actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_loadmap"));
		// m_mapframe.set_mode(MapFrame.MAP_MODE_ZOOM);
		m_mapframe.set_mode(MapFrame.MAP_MODE_SENDING_POLY);
	}
	
	public ActionEvent get_callback() {
		return local_event;
	}	

	public void actionPerformed(ActionEvent e) {
		local_event = e;
		if ("act_loadmap".equals(e.getActionCommand())) {
			m_mapframe.load_map(true);
			m_draw.setRepaint(m_mapframe.get_mapimage());
			repaint();
		} else if ("act_download_houses".equals(e.getActionCommand())) {
		} else if ("act_setzoom".equals(e.getActionCommand())) {
			m_mapproperties.set_zoom(((Double) e.getSource()).intValue());
		} else if ("act_add_polypoint".equals(e.getActionCommand())) {
			MapPoint p = (MapPoint) e.getSource();
			m_shape.typecast_polygon()
					.add_coor(new Double(p.get_lon()), new Double(p.get_lat()));

			// writing selected coordinates
			coordList = m_shape.typecast_polygon().get_coors_lat();
			for (int i = 0; i < coordList.size(); i++) {
				System.out.println(coordList.get(i) + " coordinate " + i);
			}
			// ----------------------

			redraw();
		} else if ("act_rem_polypoint".equals(e.getActionCommand())) {
			m_shape.typecast_polygon().rem_last_coor();
			redraw();
		} else if ("act_mousemoved".equals(e.getActionCommand())) {
			setMousePos((Point) e.getSource());
			redraw();
		}

		else if (e.getSource() == btn1) { // ToolBar zoom
			m_mapframe.set_mode(MapFrame.MAP_MODE_ZOOM);
		} else if (e.getSource() == btn2) { // Toolbar draw
			m_mapframe.set_mode(MapFrame.MAP_MODE_SENDING_POLY);
		} else if (e.getSource() == btn3) { // Toolbar clear
			System.out.println(m_shape.typecast_polygon().get_size());
			clear();
		}else if(e.getSource()==btn4){ //Toolbar gotTo
			NavStruct struct = m_shape.typecast_polygon().calc_bounds();
			m_nav.gotoMap(struct);
		}
	}
	public Navigation getM_navigation(){
		return m_nav;
	}
	public void clear() {
		while (m_shape.typecast_polygon().get_size() > 0) {
			m_shape.typecast_polygon().rem_last_coor();
		}
		redraw();
		System.out.println("redraw!");
	}
	
	public void update(Graphics g) {
		System.out.println("MapPanel.update(..)");
		paint(g);
	}

	public void redraw() {
		m_draw.setRepaint(m_mapframe.get_mapimage());
		repaint();
	}

	class DrawPoly extends Draw {
		DrawPoly(Component c, int n_pri, int n_x, int n_y) {
			super(c, n_pri, n_x, n_y);
		}

		public void draw_layers() {
			super.draw_layers();
			//if(m_polygon!=null)
			m_shape.typecast_polygon().draw(get_gfxbuffer(),m_nav, false, false, true, getMousePos());
			Iterator it = m_shapes.iterator();
			while(it.hasNext()) {
				PolygonStruct poly = (PolygonStruct)it.next();
				poly.draw(get_gfxbuffer(), m_nav, true, true, false, getMousePos());
			}
		}
	}

	public void updateShape(ShapeStruct shape) {
		if(shape==null)
			return;
		if(shape.getClass().equals(PolygonStruct.class)) {
			try {
				PolygonStruct polygon = shape.typecast_polygon();
				for (int i = 0; i < polygon.get_size(); i++) {
					Double lat = (Double) polygon.get_coors_lat().get(i);
					Double lon = (Double) polygon.get_coors_lon().get(i);
					this.m_shape.typecast_polygon().add_coor(lon,lat);
				}
				Color test = polygon.get_fill_color();
				this.m_shape.set_fill_color(test);
			} catch (NullPointerException e) {
				System.out.println(e.getMessage());
			}
		} else if(shape.getClass().equals(EllipseStruct.class)) {
		}
	}

	public ShapeStruct getM_shape() {
		return m_shape;
	}
	
	public void addShapeToDrawQueue(ShapeStruct s) {
		m_shapes.add(s);
	}
	public void clearDrawQueue() {
		m_shapes.clear();
	}
	
	public void drawLayers(){
		DrawPoly dp = (DrawPoly)m_draw;
		dp.draw_layers();
	}
	public Draw getDrawThread() {
		return m_draw;
	}
	public Image getMapImage(){
		return m_mapframe.get_image();
	}
	
	public MapFrame get_mapframe() {
		return m_mapframe;
	}
}
