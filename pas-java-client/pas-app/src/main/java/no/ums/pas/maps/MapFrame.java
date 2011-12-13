package no.ums.pas.maps;

//import no.ums.log.Log;
//import no.ums.log.UmsLog;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.map.tiled.AbstractTileCacheWms;
import no.ums.map.tiled.LonLat;
import no.ums.map.tiled.TileCacheFleximap;
import no.ums.map.tiled.TileData;
import no.ums.map.tiled.TileInfo;
import no.ums.map.tiled.TileLookup;
import no.ums.map.tiled.TileLookupImpl;
import no.ums.map.tiled.ZoomLookup;
import no.ums.map.tiled.component.MapModel;
import no.ums.pas.Draw;
import no.ums.pas.PAS;
import no.ums.pas.PasApplication;
import no.ums.pas.core.Variables;
import no.ums.pas.core.dataexchange.HTTPReq;
import no.ums.pas.core.logon.Settings.MAPSERVER;
import no.ums.pas.core.menus.ViewOptions;
import no.ums.pas.icons.ImageFetcher;
import no.ums.pas.maps.defines.EllipseStruct;
import no.ums.pas.maps.defines.HouseItem;
import no.ums.pas.maps.defines.Inhabitant;
import no.ums.pas.maps.defines.MapPoint;
import no.ums.pas.maps.defines.MapPointLL;
import no.ums.pas.maps.defines.MapPointPix;
import no.ums.pas.maps.defines.MapSite;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.PLMNShape;
import no.ums.pas.maps.defines.PolySnapStruct;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.maps.defines.TasStruct;
import no.ums.pas.maps.defines.UMSMapObject;
import no.ums.pas.ums.tools.PrintCtrl;
import org.geotools.data.wms.WebMapServer;
import org.geotools.ows.ServiceException;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class MapFrame extends JPanel implements ActionListener {
    private static final Log log = UmsLog.getLogger(MapFrame.class);

    public MapModel getMapModel() {
        return mapModel;
    }

    public void save_map(Image i) {
        PrintCtrl.printComponentToFile(i, null);
    }

    public void print_map() {
        PrintCtrl.printComponent(this, null);
    }

    private static final long serialVersionUID = 1L;

    public enum MapMode {
        PAN(Cursor.HAND_CURSOR),
        ZOOM(Cursor.CROSSHAIR_CURSOR),
        HOUSESELECT(Cursor.DEFAULT_CURSOR),
        SENDING_POLY(-1) {
            @Override
            public Cursor getCursor() {
                return ImageFetcher.createCursor("brush_16_paint.png", 32, 32, new Point(7, 24));
            }
        },
        OBJECT_MOVE(Cursor.MOVE_CURSOR),
        HOUSEEDITOR(Cursor.CROSSHAIR_CURSOR),
        SENDING_ELLIPSE(Cursor.SE_RESIZE_CURSOR),
        ASSIGN_EPICENTRE(-1) {
            @Override
            public Cursor getCursor() {
                return ImageFetcher.createCursor("epicentre_pinpoint.png", 32, 32, new Point(0, 0));
            }
        },
        PAN_BY_DRAG(Cursor.MOVE_CURSOR),
        SENDING_ELLIPSE_POLYGON(Cursor.SE_RESIZE_CURSOR),
        PAINT_RESTRICTIONAREA(-1) {
            @Override
            public Cursor getCursor() {
                return ImageFetcher.createCursor("brush_16_paint.png", 32, 32, new Point(7, 24));
            }
        };

        private final int cursorKey;

        MapMode(int cursorKey) {
            this.cursorKey = cursorKey;
        }

        public Cursor getCursor() {
            return new Cursor(cursorKey);
        }
    }

    public final static int MAP_HOUSEEDITOR_SET_NEWPOS = 1;
    public final static int MAP_HOUSEEDITOR_SET_PRIVATE_COOR = 2;
    public final static int MAP_HOUSEEDITOR_SET_COMPANY_COOR = 3;
    public final static int MAP_HOUSEEDITOR_SET_COOR_NONE = 4;

    Dimension m_dimension;
    public MapLoader m_maploader;
    int m_n_mapsite;
    private Cursor m_cursor_draw = ImageFetcher.createCursor("brush_16_paint.png", 32, 32, new Point(7, 24));
    private Cursor m_cursor_illegal_draw = ImageFetcher.createCursor("brush_16_paint_illegal.gif", new Point(7, 24));
    private Cursor m_cursor_draw_merge = ImageFetcher.createCursor("brush_16_merge.gif", new Point(7, 24));
    private Cursor m_cursor_draw_pin_to_border = ImageFetcher.createCursor("brush_16_pin.gif", new Point(7, 24));
    private ShapeStruct m_active_shape = null;

    private Image img_loader_snake = null;
    private String m_sz_what_is_loading = "";

    private ImageIcon m_icon_pinpoint = ImageFetcher.getIcon("pinpoint_blue.png");
    private ImageIcon m_icon_adredit = ImageFetcher.getIcon("pinpoint.png");

    private final MapModel mapModel = new MapModel();
    final no.ums.map.tiled.component.MapController controller = new no.ums.map.tiled.component.MapController();

    private final Map<String, TileLookup> tileOverlays = new HashMap<String, TileLookup>();

    private void navigationChanged()
    {
        final TileLookup tileLookup = getTileLookup();
        final TileInfo tileInfo = tileLookup.getTileInfo(mapModel.getZoom(), mapModel.getTopLeft(), getSize());
        final LonLat bottomRight = tileLookup.getZoomLookup(mapModel.getZoom()).getLonLat(mapModel.getTopLeft(), getSize().width, getSize().height);
        get_navigation().setHeaderBounds(mapModel.getTopLeft().getLon(), bottomRight.getLon(), mapModel.getTopLeft().getLat(), bottomRight.getLat());
        Variables.getNavigation().setHeaderBounds(mapModel.getTopLeft().getLon(), bottomRight.getLon(), mapModel.getTopLeft().getLat(), bottomRight.getLat());
    }


    public MapFrame(int n_width, int n_height, Draw drawthread, Navigation nav, HTTPReq http, boolean b_enable_snap) {
        super();
        m_actionhandler = new MapFrameActionHandler(this, b_enable_snap);

        mapModel.addPropertyChangeListener("zoom", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
    			navigationChanged();
            	//if(mapModel.getZoom()>=17)
            	{
        			PAS.get_pas().download_houses();
                    PAS.pasplugin.onAfterLoadMap(PAS.get_pas().get_settings(), m_navigation, MapFrame.this);
            	}
            	//else
            	{

            	}
            	PAS.get_pas().kickRepaint();
            }
        });
        mapModel.addPropertyChangeListener("topLeft", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
    			navigationChanged();
            	PAS.get_pas().kickRepaint();
            }
        });
        get_actionhandler().addPropertyChangeListener("dragging", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
		        //download houses when user stops dragging
				log.debug("dragging set to " + get_actionhandler().get_isdragging());
				switch(get_mode())
				{
				case PAN:
				case PAN_BY_DRAG:
					if(!get_actionhandler().get_isdragging())
					{
            			PAS.get_pas().download_houses();
                        PAS.pasplugin.onAfterLoadMap(PAS.get_pas().get_settings(), m_navigation, MapFrame.this);
					}
					break;
				}
			}
		});


        setPreferredSize(new Dimension(100, 100));
        setLayout(new BorderLayout());

        img_loader_snake = ImageFetcher.getImage("convert_32.png");

        m_drawthread = drawthread;
        m_navigation = nav;
        m_n_mapsite = 0;
        //this.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        this.setBackground(Color.WHITE);
        m_dimension = new Dimension(n_width, n_height);
        setSize(m_dimension.width, m_dimension.height);
        m_maploader = new MapLoader(this);
        m_current_cursor = new Cursor(Cursor.DEFAULT_CURSOR);
        this.setPreferredSize(m_dimension);
        m_polypoint_popup = new PUPolyPoint("Menu", this);
        m_current_mousepos = new Point();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                if (w <= 0 || h <= 0) {
                    return;
                }

                setPreferredSize(new Dimension(w, h));
            }
        });




        MouseAdapter mouseAdapter = new MouseAdapter() {

            private Point mouseDownPoint;
            private Cursor oldCursor;

            @Override
            public void mouseEntered(MouseEvent e) {
                setMouseInsideCanvas(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setMouseInsideCanvas(false);
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getSource().equals(MapFrame.this)) {
                    if (e.getWheelRotation() < 0) {
                    	onZoomGesture(true, e.getPoint());
                    } else if (e.getWheelRotation() > 0) {
                    	onZoomGesture(false, e.getPoint());
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (get_mode() == MapMode.PAN_BY_DRAG) {
                    mouseDownPoint = e.getPoint();
                    oldCursor = getCursor();
                    setCursor(new Cursor(Cursor.MOVE_CURSOR));
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (mouseDownPoint != null && get_mode() == MapMode.PAN_BY_DRAG) {
                    controller.mapDragged(mapModel, getTileLookup(), getSize(), e.getX() - mouseDownPoint.x, e.getY() - mouseDownPoint.y);
                    mouseDownPoint = e.getPoint();
                    kickRepaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (get_mode() == MapMode.PAN_BY_DRAG) {
                    setCursor(oldCursor);
                    mouseDownPoint = null;
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
//                if (get_mode() == MapMode.PAN) {
//                    controller.mapDragged(mapModel, getTileLookup(), getSize(), e.getX() - getSize().width/2, e.getY() - getSize().height/2);
//                }
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        addMouseWheelListener(mouseAdapter);
    }

    protected void onZoomGesture(boolean bZoomIn, Point p)
    {
    	if(bZoomIn)
    	{
            controller.onZoomIn(mapModel, getTileLookup(), getSize(), p);
    	}
    	else
    	{
            controller.onZoomOut(mapModel, getTileLookup(), getSize(), p);
    	}
    }

    public ShapeStruct get_active_shape() {
        return m_active_shape;
    }

    public MapLoader getMapLoader() {
        return m_maploader;
    }

    public boolean isInPaintMode() {
        switch (get_mode()) {
            case PAINT_RESTRICTIONAREA:
            case SENDING_ELLIPSE:
            case SENDING_ELLIPSE_POLYGON:
            case SENDING_POLY:
                return true;
        }
        return false;
    }

    public boolean setPaintModeBasedOnActiveShape(boolean bForcePolyIsElliptical) {
        if (get_active_shape() == null)
            return false;

        Class cl = get_active_shape().getClass();
        if (cl.equals(PolygonStruct.class)) {
            PolygonStruct poly = (PolygonStruct) get_active_shape();
            if (poly.isElliptical() || bForcePolyIsElliptical)
                set_mode(MapMode.SENDING_ELLIPSE_POLYGON);
            else
                set_mode(MapMode.SENDING_POLY);
        } else if (cl.equals(EllipseStruct.class))
            set_mode(MapMode.SENDING_ELLIPSE);
        else if (cl.equals(PLMNShape.class))
            set_mode(MapMode.PAN);
        else if (cl.equals(TasStruct.class))
            set_mode(MapMode.PAN);

        return true;
    }

    public void set_active_shape(ShapeStruct s) {
        m_active_shape = s;
        kickRepaint();
    }

    private PUPolyPoint m_polypoint_popup = null;
    private PolySnapStruct m_polysnapstruct = null;

    public PolySnapStruct get_current_snappoint() {
        return m_polysnapstruct;
    }

    protected boolean b_mouse_inside_canvas = true;

    protected void setMouseInsideCanvas(boolean b) {
        b_mouse_inside_canvas = b;
        kickRepaint();
        //if(m_b_needrepaint==0)
        //m_b_needrepaint ++;
    }

    public boolean getMouseInsideCanvas() {
        return b_mouse_inside_canvas;
    }

    public Cursor get_cursor_draw() {
        return m_cursor_draw;
    }

    public Cursor get_cursor_illegal_draw() {
        return m_cursor_illegal_draw;
    }

    public Cursor get_cursor_draw_merge() {
        return m_cursor_draw_merge;
    }

    public Cursor get_cursor_draw_pin_to_border() {
        return m_cursor_draw_pin_to_border;
    }

    public Image m_img_onscreen = null;
    public Image m_img_loading = null;
    //Image m_img_overlay = null;
    //Image m_img_overlay_onscreen = null;

    public void putTileOverlay(String id, TileLookup tileLookup) {
        tileOverlays.put(id, tileLookup);
        repaint();
    }

    public void removeTileOverlay(String gsm) {
        tileOverlays.remove(gsm);
        repaint();
    }

    //close status should run this
    public void resetAllOverlays() {
        tileOverlays.clear();
    }

    MapFrameActionHandler m_actionhandler;
    int m_n_current_cursor = Cursor.DEFAULT_CURSOR;
    protected MapMode m_n_current_mode = MapMode.PAN;
    protected MapMode m_n_prev_mode = MapMode.PAN;
    protected MapMode m_n_prev_paint_mode = MapMode.SENDING_POLY;
    int m_n_current_submode = 0;
    private Inhabitant m_current_inhab_move = null;
    public Cursor m_current_cursor;
    String m_sz_mapportrayal = "By";
    UMSMapObject m_current_object = null;

    protected UMSMapObject get_current_object() {
        return m_current_object;
    }

    void set_current_object(UMSMapObject obj) {
        m_current_object = obj;
    }

    private Draw m_drawthread;
    private Navigation m_navigation;

    protected Draw get_drawthread() {
        return m_drawthread;
    }

    protected Navigation get_navigation() {
        return m_navigation;
    }

    public void addActionListener(ActionListener callback) {
        m_actionhandler.addActionListener(callback);
    }

    public ImageIcon get_icon_pinpoint() {
        return m_icon_pinpoint;
    }

    public ImageIcon get_icon_adredit() {
        return m_icon_adredit;
    }

    public boolean get_draw_adredit() {
        return get_mode() == MapMode.HOUSEEDITOR;

    }

    public void setCurrentInhabitant(Inhabitant i) {
        m_current_inhab_move = i;
    }

    public Inhabitant getCurrentInhabitant() {
        return m_current_inhab_move;
    }

    private MapPointLL m_pinpointll = null;

    public MapPointLL get_pinpointll() {
        return m_pinpointll;
    }

    private MapPointLL m_adreditll = null;

    public MapPointLL get_adreditll() {
        return m_adreditll;
    }

    private ArrayList<HouseItem> m_mouseoverhouse = null;

    public ArrayList<HouseItem> get_mouseoverhouse() {
        return m_mouseoverhouse;
    }

    public void set_mouseoverhouse(ArrayList<HouseItem> i) {
        m_mouseoverhouse = i;
    }

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
        return super.getToolTipLocation(event);
    }

    public JToolTip createToolTip() {
        JToolTip tip = super.createToolTip();

        Color _c1 = SystemColor.controlShadow;
        Color c1 = new Color(_c1.getRed(), _c1.getGreen(), _c1.getBlue(), 220);

        tip.setBackground(c1);

        tip.setForeground(SystemColor.textText);

        m_tooltip = tip;
        return tip;
    }

    public void setToolTipText(final String s, final boolean b_show) {
        b_show_tooltip = b_show;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setToolTipText((b_show ? s : null));
            }
        });
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
        if (ViewOptions.TOGGLE_SEARCHPOINTS.isSelected() && get_pinpointll() != null) {
            try {
            	Point pointPin = getZoomLookup().getPoint(new LonLat(get_pinpointll().get_lon(), get_pinpointll().get_lat()));
            	Point topLeft = getZoomLookup().getPoint(getMapModel().getTopLeft());
            	Point p = new Point(pointPin.x - topLeft.x, pointPin.y - topLeft.y);
                g.drawImage(get_icon_pinpoint().getImage(), p.x - get_icon_pinpoint().getIconWidth() / 2, p.y - get_icon_pinpoint().getIconHeight() / 2, get_icon_pinpoint().getIconWidth(), get_icon_pinpoint().getIconHeight(), this);
            } catch (Exception e) {
                log.warn("Failed to draw pinpoint", e);
            }
        }
    }

    public void draw_adredit(Graphics g) {
        try {
            if (get_draw_adredit() && this.get_adreditll() != null) {
                MapPoint p = new MapPoint(get_navigation(), this.get_adreditll());
                g.drawImage(get_icon_adredit().getImage(), p.get_x() - get_icon_pinpoint().getIconWidth() / 2, p.get_y() - get_icon_pinpoint().getIconHeight() / 2, get_icon_pinpoint().getIconWidth(), get_icon_pinpoint().getIconHeight(), this);
            }
        } catch (Exception e) {
            log.error("Exception in draw_adredit", e);
        }
    }

    public void draw_moveinhab_text(Graphics g) {
        g.drawString(getCurrentInhabitant().get_adrname(), this.get_current_mousepos().x + 15, this.get_current_mousepos().y + 15);
    }

    public Dimension get_dimension() {
        return m_dimension;
    }

    /*public void robot_movecursor(Point p) {
         m_robot.mouseMove(p.x, p.y);
     }*/

    public void set_mode(MapMode mode) {
        //let settings decide
        if (mode == MapMode.PAN || mode == MapMode.PAN_BY_DRAG) {
            if (PAS.get_pas() != null && PAS.get_pas().get_settings().getPanByDrag())
                mode = MapMode.PAN_BY_DRAG;
            else
                mode = MapMode.PAN;
        }

        set_cursor(mode.getCursor());
        m_n_prev_mode = m_n_current_mode;
        m_n_current_mode = mode;

        if (get_current_object() != null) {
            get_current_object().setMoving(mode == MapMode.OBJECT_MOVE);
        }
        kickRepaint();
    }


    public void set_submode(int n_mode) {
        m_n_current_submode = n_mode;
        if (m_n_current_mode == MapMode.HOUSEEDITOR) {
            set_cursor((m_n_current_submode == MAP_HOUSEEDITOR_SET_COOR_NONE) ? new Cursor(Cursor.WAIT_CURSOR) : new Cursor(Cursor.CROSSHAIR_CURSOR));
        }
    }

    public void set_prev_mode() {
        set_mode(m_n_prev_mode);
    }

    public void set_prev_paintmode() {
        set_mode(m_n_prev_paint_mode);
    }

    public MapMode get_mode() {
        return m_n_current_mode;
    }

    public int get_submode() {
        return m_n_current_submode;
    }

    public void set_mode_objectmove(UMSMapObject obj) {
        set_current_object(obj);
        set_mode(MapMode.OBJECT_MOVE);
    }

    public void initialize() {
        setVisible(true);
        setFocusable(true);
        addMouseMotionListener(m_actionhandler);
        addMouseListener(m_actionhandler);
        try {
            this.addKeyListener(m_actionhandler);
        } catch (Exception e) {
            log.error("Exception in initialize", e);
        }
    }

    public void drawOnEvents(Graphics gfx) {
        if (get_actionhandler().get_isdragging() && get_mode() == MapMode.ZOOM) {
            draw_dragging_square(gfx);
        }
        if (IsLoading()) {
            draw_loading_image(gfx);
        }
    }


    void draw_loading_image(Graphics gfx) {
        if (img_loader_snake == null)
            return;
        int scale = 1;
        int imgw = img_loader_snake.getWidth(null);
        int imgh = img_loader_snake.getHeight(null);
        Color c1 = SystemColor.controlDkShadow;
        Color c2 = SystemColor.controlLtHighlight;

        gfx.setColor(new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 160));

        gfx.drawRoundRect(getWidth() / 2 - 100, getHeight() / 2 - 70, 200, 100, 25, 25);

        gfx.setColor(new Color(c2.getRed(), c2.getGreen(), c2.getBlue(), 160));

        gfx.fillRoundRect(getWidth() / 2 - 100, getHeight() / 2 - 70, 200, 100, 25, 25);

        gfx.setColor(new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 225));
        gfx.setFont(new Font(null, Font.BOLD, 18));
        gfx.drawString(m_sz_what_is_loading, getWidth() / 2 - 60, getHeight() / 2 - 40);


        gfx.drawImage(img_loader_snake, getWidth() / 2 - imgw / 2 * scale, getHeight() / 2 - imgh / 2 * scale, imgw * scale, imgh * scale, null);
    }

    void draw_dragging_square(Graphics gfx) {
        gfx.setColor(new Color((float) 0.0, (float) 0.0, (float) 0.0, (float) 0.2));
        int x1, x2, y1, y2;
        if (Variables.getSettings().getZoomFromCenter()) {
            x1 = get_actionhandler().getMousedownPos().width - Math.abs(get_actionhandler().getMousedownPos().width - get_actionhandler().getCursorPos().width);
            x2 = get_actionhandler().getMousedownPos().width + Math.abs(get_actionhandler().getMousedownPos().width - get_actionhandler().getCursorPos().width);
            y1 = get_actionhandler().getMousedownPos().height - Math.abs(get_actionhandler().getMousedownPos().height - get_actionhandler().getCursorPos().height);
            y2 = get_actionhandler().getMousedownPos().height + Math.abs(get_actionhandler().getMousedownPos().height - get_actionhandler().getCursorPos().height);
        } else {
            int tmpx1, tmpx2, tmpy1, tmpy2;
            tmpx1 = get_actionhandler().getMousedownPos().width;
            tmpx2 = get_actionhandler().getCursorPos().width;
            tmpy1 = get_actionhandler().getMousedownPos().height;
            tmpy2 = get_actionhandler().getCursorPos().height;
            x1 = Math.min(tmpx1, tmpx2);
            x2 = Math.max(tmpx1, tmpx2);
            y1 = Math.min(tmpy1, tmpy2);
            y2 = Math.max(tmpy1, tmpy2);
        }
        gfx.fillRect(x1, y1, x2 - x1, y2 - y1);
        gfx.setColor(Color.black);
        gfx.drawRect(x1, y1, x2 - x1, y2 - y1);
    }

    public int get_mapsite() {
        return m_n_mapsite;
    }

    public String get_mapportrayal() {
        return m_sz_mapportrayal;
    }

    public void set_mapsite(MapSite site) {
        m_n_mapsite = site.get_mapsite();
        m_sz_mapportrayal = site.get_portrayal();
    }

    public MapFrameActionHandler get_actionhandler() {
        return m_actionhandler;
    }

    public void load_map() {
        //m_img_loading.flush();

        log.debug("Loading map " + PAS.get_pas().get_settings().getMapServer().name());
        PAS.pasplugin.onBeforeLoadMap(PAS.get_pas().get_settings());
        PAS.get_pas().get_mainmenu().enableUglandPortrayal((PAS.get_pas().get_settings().getMapServer() == MAPSERVER.DEFAULT ? true : false));
        if (m_maploader.IsLoadingMapImage())
            return;

        set_cursor(new Cursor(Cursor.WAIT_CURSOR));
        get_drawthread().set_need_imageupdate();
        //m_img_onscreen = null;
        try {
            switch (PAS.get_pas().get_settings().getMapServer()) {
                case DEFAULT:
//                    m_img_loading = m_maploader.load_map(get_navigation().getNavLBO(), get_navigation().getNavRBO(), get_navigation().getNavUBO(), get_navigation().getNavBBO(), this.getSize(), get_mapsite(), get_mapportrayal());
                    break;
                case WMS:
                    m_img_loading = m_maploader.load_map_wms(get_navigation().getNavLBO(), get_navigation().getNavRBO(), get_navigation().getNavUBO(), get_navigation().getNavBBO(), this.getSize(), Variables.getSettings().getWmsSite());
                    break;
            }
        } catch (Exception e) {
            //log.debug("Error loading map " + e.getMessage());
            //Error.getError().addError("MapFrame","Exception in load_map",e,1);
            log.warn(e.getMessage(), e);
            PAS.pasplugin.onMapLoadFailed(this.get_maploader());
        }
        try {
            m_img_onscreen.flush();
        } catch (Exception e) {

        }
        m_img_onscreen = m_img_loading;
        /*m_img_onscreen = new BufferedImage(m_img_loading.getWidth(null), m_img_loading.getHeight(null), BufferedImage.TYPE_INT_ARGB);
          Graphics tempg = m_img_onscreen.createGraphics();
          tempg.drawImage(m_img_loading, 0, 0, null);
          tempg.dispose();*/

        //PAS.get_pas().prepareImage(m_img_onscreen, PAS.get_pas().get_drawthread());
        try {
            prepareImage(m_img_onscreen, null); //get_drawthread());
        } catch (Exception e) {
            //log.debug("prepareImage " + e.getMessage());
            //Error.getError().addError("MapFrame","Exception in load_map",e,1);
        }
        try {
            set_cursor(m_current_cursor);
        } catch (Exception e) {
            //Error.getError().addError("MapFrame","Exception in load_map",e,1);
        }
        //log.debug("KICKREPAINT");
        PAS.pasplugin.onAfterLoadMap(PAS.get_pas().get_settings(), Variables.getNavigation(), this);
        PAS.get_pas().kickRepaint();

    }

    int b_loading_in_progress = 0;

    public boolean IsLoading() {
        return b_loading_in_progress > 0;
    }

    public void SetIsLoading(boolean b, String what_is_loading) {

        //b_loading_in_progress += (b ? 1 : (b_loading_in_progress>=1 ? -1 : 0));
        if (b)
            b_loading_in_progress++;
        else
            b_loading_in_progress--;
        if (b_loading_in_progress < 0)
            b_loading_in_progress = 0;
        if (what_is_loading.length() > 0)
            m_sz_what_is_loading = what_is_loading;
        kickRepaint();
    }

    public synchronized void load_map(boolean b_threaded) {
        //ensures that nothing is drawn until this new map is ready
            PAS.pasplugin.onBeforeLoadMap(PAS.get_pas().get_settings());
        PAS.pasplugin.onAfterLoadMap(PAS.get_pas().get_settings(), Variables.getNavigation(), MapFrame.this);
    }

    public void set_cursor(Cursor cur) {
        if (cur.getType() != Cursor.WAIT_CURSOR) {
            setCursor(cur);
            m_n_current_cursor = cur.getType();
            m_current_cursor = cur;
        } else {
            //wait cursor is current. must be overridden by reset_cursor
            //m_n_current_cursor = getCursor().getType();
            setCursor(cur);
        }
    }

    private final List<Future<?>> pendingDownloads = new ArrayList<Future<?>>();

    public void paintToImage(BufferedImage b)
    {
    	Graphics g = b.getGraphics();
		g.setClip(0, 0, getWidth(), getHeight());
		paintComponent(g);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            for (Future<?> pendingDownload : pendingDownloads) {
                if (!pendingDownload.isDone()) {
                    pendingDownload.cancel(false);
                }
            }
            pendingDownloads.clear();

            final Iterable<TileLookup> layers = Iterables.concat(Collections.singleton(getTileLookup()), tileOverlays.values());
            for (final TileLookup tileLookup : layers) {
                final TileInfo tileInfo = tileLookup.getTileInfo(mapModel.getZoom(), mapModel.getTopLeft(), getSize());
                final LonLat bottomRight = tileLookup.getZoomLookup(mapModel.getZoom()).getLonLat(mapModel.getTopLeft(), getSize().width, getSize().height);
                get_navigation().setHeaderBounds(mapModel.getTopLeft().getLon(), bottomRight.getLon(), mapModel.getTopLeft().getLat(), bottomRight.getLat());

                for (final TileData tileData : tileInfo.getTileData()) {
                    g.drawImage(tileLookup.getImageFast(tileData), tileData.getX(), tileData.getY(), tileData.getWidth(), tileData.getHeight(), null);
                    if (!tileLookup.exists(tileData)) {
                        pendingDownloads.add(PasApplication.getInstance().getExecutor().submit(new Runnable() {
                            @Override
                            public void run() {
                                tileLookup.getImage(tileData);
                                repaint();
                            }
                        }));
                    }
                }
            }

            PAS.get_pas().get_drawthread().draw_layers(g);
            drawOnEvents(g);
        } catch (Exception e) {
            log.error("Failed to draw map", e);
        }
    }

    private final TileLookup defaultLookup = new TileLookupImpl(new TileCacheFleximap());
    private final TileLookup wmsLookup = new TileLookupImpl(new AbstractTileCacheWms() {
        private String lastLookup = null;
        private String scheme;
        private String host;
        private String path;
        private String version;


        private void update() {
            final String wmsSite = Variables.getSettings().getWmsSite();
            if (lastLookup == null || !lastLookup.equals(wmsSite)) {
                try {
                    final URI base = URI.create(wmsSite);
                    scheme = base.getScheme();
                    host = base.getHost();
                    path = base.getPath();
                    
        			m_maploader.setWmsAuthenticator(Variables.getSettings().getWmsUsername(), Variables.getSettings().getWmsPassword().toCharArray());
                    WebMapServer wms = new WebMapServer(new URL(wmsSite));
                    version = wms.getCapabilities().getVersion();
                    lastLookup = wmsSite;
                } catch (IOException e) {
                    version = "1.1.1";
                    log.warn("Failed to fetch WMS version", e);
                } catch (ServiceException e) {
                    log.warn("Failed to fetch WMS version", e);
                }
            }
        }

        @Override
        public String getScheme() {
            update();
            return scheme;
        }

        @Override
        public String getHost() {
            update();
            return host;
        }

        @Override
        public String getPath() {
            update();
            return path;
        }

        @Override
        public String getVersion() {
            update();
            return version;
        }

        @Override
        public String getFormat() {
            return Variables.getSettings().getSelectedWmsFormat();
        }

        @Override
        public String getLayers() {
            return Joiner.on(",").join(Variables.getSettings().getSelectedWmsLayers());
        }
    });

    public TileLookup getTileLookup() {
        switch (PAS.get_pas().get_settings().getMapServer()) {
            case DEFAULT:
                return defaultLookup;
            case WMS:
                return wmsLookup;
            default:
                throw new IllegalStateException("Unsupported map server" + PAS.get_pas().get_settings().getMapServer());
        }
    }

    public ZoomLookup getZoomLookup() {
        return getTileLookup().getZoomLookup(getMapModel().getZoom());
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
        try {
            ret = null;
        } catch (Exception e) {
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

    public void clear_screen() {

    }

    @Override
    public void update(Graphics g) {
        super.update(g);
        log.debug("!!!!!!!!!!UPDATE!!!!!!!!!!!!!");
        paint(g);
    }

    public void update() {
        clear_screen();
        draw_background();
    }

    private Point m_current_mousepos = null;

    public Point get_current_mousepos() {
        return m_current_mousepos;
    }

    public void set_current_mousepoint(Point p) {
        m_current_mousepos = p;
    }

    public void kickRepaint() {
//        new Exception("Repaint event").printStackTrace();
        repaint();
        validate();
    }

    public void actionPerformed(ActionEvent e) {
        if ("act_set_active_shape".equals(e.getActionCommand())) {
            set_active_shape((ShapeStruct) e.getSource());
        } else if ("act_add_polypoint".equals(e.getActionCommand())) {
        	
            LonLat ll = getZoomLookup().getLonLat(mapModel.getTopLeft(), get_current_mousepos().x, get_current_mousepos().y);
            MapPoint p = new MapPoint(Variables.getNavigation(), new MapPointLL(ll.getLon(), ll.getLat()));
            //MapPoint p = new MapPoint(Variables.getNavigation(), new MapPointPix(get_current_mousepos().x, get_current_mousepos().y));
            try {
                get_active_shape().typecast_polygon().add_coor(p.get_lon(), p.get_lat());
            } catch (Exception err) {
                log.warn("Failed to add coordinates", err);
            }
            kickRepaint();
        } else if ("act_rem_polypoint".equals(e.getActionCommand())) {
            get_active_shape().typecast_polygon().rem_last_coor();
            if (PAS.get_pas() != null)
                PAS.get_pas().kickRepaint();
            else
                kickRepaint();
        } else if ("act_insat_polypoint".equals(e.getActionCommand())) {
            get_active_shape().typecast_polygon().set_activepoint(get_current_snappoint());
            if (PAS.get_pas() != null)
                PAS.get_pas().kickRepaint();
            else
                kickRepaint();
        } else if ("act_delat_polypoint".equals(e.getActionCommand())) {
            get_active_shape().typecast_polygon().remove_at(get_current_snappoint().get_polyindex());
            if (PAS.get_pas() != null)
                PAS.get_pas().kickRepaint();
            else
                kickRepaint();
        } else if ("act_moveat_polypoint".equals(e.getActionCommand())) {
            get_active_shape().typecast_polygon().move_at();
            set_mode_objectmove(get_current_snappoint());
        } else if ("act_reverse_polypoints".equals(e.getActionCommand())) {
            get_active_shape().typecast_polygon().reverse_coor_order();
            if (PAS.get_pas() != null)
                PAS.get_pas().kickRepaint();
            else
                kickRepaint();
        } else if ("act_set_ellipse_center".equals(e.getActionCommand())) {
            MapPoint p = (MapPoint) e.getSource();
            try {
                get_active_shape().typecast_ellipse().set_ellipse_center(Variables.getNavigation(), p);
            } catch (Exception err) {
                log.warn("Failed to set elipse center", err);
            }
        } else if ("act_set_ellipse_corner".equals(e.getActionCommand())) {
            MapPoint p = (MapPoint) e.getSource();
            try {
                get_active_shape().typecast_ellipse().set_ellipse_corner(Variables.getNavigation(), p);
            } catch (Exception err) {
                log.warn("Failed to handle action %s", e.getActionCommand(), err);
            }
        } else if ("act_set_polygon_ellipse_center".equals(e.getActionCommand())) {
            MapPoint p = (MapPoint) e.getSource();
            try {
                get_active_shape().typecast_polygon().set_ellipse_center(Variables.getNavigation(), p);
            } catch (Exception err) {
                log.warn("Failed to handle action %s", e.getActionCommand(), err);
            }
        } else if ("act_set_polygon_ellipse_corner".equals(e.getActionCommand())) {
            MapPoint p = (MapPoint) e.getSource();
            try {
                get_active_shape().typecast_polygon().set_ellipse_corner(Variables.getNavigation(), p);
            } catch (Exception err) {
                log.warn("Failed to handle action %s", e.getActionCommand(), err);
            }
        } else if ("act_onmouseover_houses".equals(e.getActionCommand())) {
            ArrayList<HouseItem> arr = (ArrayList<HouseItem>) e.getSource();
            if (arr.size() > 0) {
                set_mouseoverhouse(arr);
                kickRepaint();
            } else
                set_mouseoverhouse(null);
        } else if ("act_mousemove".equals(e.getActionCommand())) {
            int n_x, n_y;
            n_x = ((MapPoint) e.getSource()).get_x();
            n_y = ((MapPoint) e.getSource()).get_y();
            try {
                set_current_mousepoint(new Point(n_x, n_y));
            } catch (Exception err) {
                log.warn("Failed to handle action %s", e.getActionCommand(), err);
            }
        } else if ("act_mouse_rightclick".equals(e.getActionCommand())) {
            if (get_current_snappoint() != null) {
                if (get_current_snappoint().isActive()) {//only activate menu for active sending
                    m_polypoint_popup.enable_insert(!get_current_snappoint().isLastPoint());
                    m_polypoint_popup.show(this, get_current_snappoint().p());
                }
            }
        }
        //this is executed by MapFrameActionHandler.mouseMoved if act_mousemove has detected a snap
        else if ("act_mousesnap".equals(e.getActionCommand())) {
            int n_x, n_y;
            n_x = ((MapPoint) e.getSource()).get_x();
            n_y = ((MapPoint) e.getSource()).get_y();
            set_current_mousepoint(new Point(n_x, n_y));
            if (PAS.get_pas() != null)
                PAS.get_pas().kickRepaint();
            else
                kickRepaint();
        }
    }

}
