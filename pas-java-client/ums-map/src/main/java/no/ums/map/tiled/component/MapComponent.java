package no.ums.map.tiled.component;

import no.ums.map.tiled.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public final class MapComponent extends JComponent {

    private static final int TILE_SIZE = 256;
    private TileLookup tileLookup;
    private final MapModel model = new MapModel();
    private final Controller controller = new MapController();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final List<Future<?>> tasks = new ArrayList<Future<?>>();
    
    public static class DrawingLayer extends MouseAdapter implements MapLayer, KeyListener {

        private final MapComponent mapComponent;
        private boolean isDrawing;
        private Point currentPoint;
        private Path2D.Double path;
        private List<LonLat> shape;

        private boolean drawingActivated = true;
        public boolean isDrawingActivated() { return drawingActivated; }
        public void setDrawingActivated(boolean drawingActivated) { this.drawingActivated = drawingActivated; }
        
        public void setShape(List<LonLat> shape) { this.shape = shape; }
        public List<LonLat> getShape() { 
            System.out.println("getShape");
            return this.shape;
        }
        public void setPath(Path2D.Double path) { this.path = path; }

        public DrawingLayer(MapComponent mapComponent) {
            this.mapComponent = mapComponent;
            shape = new ArrayList<LonLat>();
            path = new Path2D.Double();
            mapComponent.addLayer(this);
            mapComponent.addMouseMotionListener(this);
            mapComponent.addMouseListener(this);
            mapComponent.addKeyListener(this);
        }
        
        public Image getBufferedImage(int width, int height) {
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bi.createGraphics();
            mapComponent.paint(g);
            g.dispose();
            return bi;
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            super.mouseWheelMoved(e);
            recalculate();
            System.out.println("mouseWheelMoved");
        }
        
        public boolean canDrawHere(Point p) {
            boolean canDraw;
            Polygon restricionPoly = MapTools.createPolygon(mapComponent.getRestrictionLayer().path, mapComponent.getRestrictionLayer().shape);

            canDraw = restricionPoly != null && (!restricionPoly.contains(p));

            if(mapComponent.getDrawLayer().shape.size()>2 && canDraw) {
                PathIterator pi = mapComponent.getRestrictionLayer().path.getPathIterator(new AffineTransform());
                Path2D tmp = new Path2D.Double();
                tmp.append(pi,true);

                PathIterator piDraw = mapComponent.getDrawLayer().path.getPathIterator(new AffineTransform());


                List<Double[]> draw = new ArrayList<Double[]>();
                double[] coors = new double[2];
                while(!piDraw.isDone()) {
                    piDraw.currentSegment(coors);
                    draw.add(new Double[] { coors[0], coors[1] });
                    piDraw.next();
                }

                Double[] tmpc = draw.get(draw.size()-1);

                Line2D.Double line = new Line2D.Double(tmpc[0].intValue(), tmpc[1].intValue(),
                        p.getX(), p.getY());
                
                canDraw = !lineIntersects(mapComponent.getRestrictionLayer().path, line);
            }

            canDraw = isDrawingActivated();

            return canDraw;
        }
        
        private boolean lineIntersects(Path2D restrictionPath, Line2D line) {
            PathIterator iterator = restrictionPath.getPathIterator(new AffineTransform());
            double[] coors1 = new double[2];
            double[] coors2 = new double[2];
            Line2D.Double restrictionLine;

            while (!iterator.isDone()) {
                iterator.currentSegment(coors1);
                iterator.next();
                if(!iterator.isDone()) {
                    iterator.currentSegment(coors2);
                    restrictionLine = new Line2D.Double(coors1[0], coors1[1], coors2[0], coors2[1]);
                    if(restrictionLine.intersectsLine(line)) {
                        return true;
                    }
                }
            }
            return false;
        }



        public Path2D.Double addLonLatToPath(List<LonLat> lonLatShape, Path2D.Double path) {
            ZoomLookup zoomLookup = mapComponent.tileLookup.getZoomLookup(mapComponent.getModel().getZoom());
            Point screenPoint;
            int i=0;
            for(LonLat lonLat: lonLatShape) {
                screenPoint = zoomLookup.getScreenPoint(mapComponent.getModel().getTopLeft(),lonLat);
                if(i==0) {
                    path.moveTo(screenPoint.getX(),screenPoint.getY());
                }
                else {
                    path.lineTo(screenPoint.getX(),screenPoint.getY());
                }
                path.closePath();
                ++i;
            }

            return path;
        }
        
        public LonLat closestPolygonPoint(Point2D.Double p) {
            List<LonLat> res = mapComponent.getRestrictionLayer().shape;

            ZoomLookup zoomLookup = mapComponent.tileLookup.getZoomLookup(mapComponent.getModel().getZoom());
            LonLat ll = zoomLookup.getLonLat(mapComponent.getModel().getTopLeft(), (int)p.getX(),(int)p.getY());

            double distanceM = Double.MAX_VALUE;
            LonLat closest = null;

            for(LonLat lonLat: res) {
                if(distanceM>lonLat.distanceToInM(ll)) {
                    distanceM = lonLat.distanceToInM(ll);
                    closest = lonLat;
                }
            }
            return closest;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(canDrawHere(new Point(e.getX(), e.getY()))) {
                if (!isDrawing && e.getButton() == MouseEvent.BUTTON1) {
                    isDrawing = true;
                    path.moveTo(e.getX(), e.getY());
                    //shape.clear();
                    ZoomLookup zoomLookup = mapComponent.tileLookup.getZoomLookup(mapComponent.getModel().getZoom());
                    LonLat ll = zoomLookup.getLonLat(mapComponent.getModel().getTopLeft(), e.getX(),e.getY());
                    shape.add(ll);
                    if(shape.size()>0) {
                        PathIterator iterator = path.getPathIterator(new AffineTransform());
                        double[] coords = new double[2];
                        int[] x = new int[shape.size()];
                        int[] y = new int[shape.size()];
                        int i=0;
                        while(!iterator.isDone()) {
                            iterator.currentSegment(coords);
                            try {
                                x[i] = (int)coords[0];
                            }
                            catch (Exception ex) {
                                System.out.print(ex.getMessage());
                            }
                            y[i] = (int)coords[1];
                            i++;
                            iterator.next();
                        }
                        Area a = new Area(new Polygon(x, y, shape.size()));
                        a.add(new Area(path));
                        path.append(a,false);
                    }
                }
                else if (isDrawing && e.getButton() == MouseEvent.BUTTON1) {
                    ZoomLookup zoomLookup = mapComponent.tileLookup.getZoomLookup(mapComponent.getModel().getZoom());
                    LonLat ll;
                    if(MapTools.getNumberOfPathPoints(path) == 1 && mapComponent.getRestrictionLayer().shape.size() > 3) {
                        Point2D.Double lastPoint = getLastPoint();
                        LonLat ll1 = closestPolygonPoint(new Point2D.Double(lastPoint.getX(), lastPoint.getY()));
                        Point p1 = zoomLookup.getScreenPoint(mapComponent.getModel().getTopLeft(),ll1);
                        path.lineTo(p1.getX(), p1.getY());

                        LonLat ll2 = closestPolygonPoint(new Point2D.Double(e.getX(), e.getY()));
                        Point p2 = zoomLookup.getScreenPoint(mapComponent.getModel().getTopLeft(),ll2);
                        path.lineTo(p2.getX(), p2.getY());

                        List<LonLat> lonLatPath = MapTools.getShapeShortestRoute(mapComponent.getRestrictionLayer().shape, ll1, ll2);
                        // Add the restriction border to path
                        path = addLonLatToPath(lonLatPath, path);

                        // Add the restriction border lonlat to shape
                        shape.addAll(lonLatPath);
                    }
                    path.lineTo(e.getX(), e.getY());
                    ll = zoomLookup.getLonLat(mapComponent.getModel().getTopLeft(), e.getX(),e.getY());
                    shape.add(ll);
                    recalculate();
                    mapComponent.repaint();
                } else if (isDrawing && e.getButton() == MouseEvent.BUTTON3) {
                    isDrawing = false;
                    path.closePath();
                    recalculate();
                    mapComponent.repaint();
                }
            }
        }

        public Path2D.Double getPath() {
            return path;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            super.mouseDragged(e);
            currentPoint = e.getPoint();
            mapComponent.requestFocus();
        }

        public ZoomLookup recalculate() {
            ZoomLookup zoomLookup = mapComponent.tileLookup.getZoomLookup(mapComponent.getModel().getZoom());
            if(shape.size()>0) {
                path.reset();
                path = new Path2D.Double();
                for(int i=0; i< shape.size(); i++) {
                    LonLat lonLat = shape.get(i);
                    Point p = zoomLookup.getScreenPoint(mapComponent.getModel().getTopLeft(), lonLat);
                    if(i==0) {
                        path.moveTo(p.getX(), p.getY());
                    }
                    else {
                        path.lineTo(p.getX(),p.getY());
                    }
                }
                if(!isDrawing) {
                    path.closePath();
                }
            }
            return zoomLookup;
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            currentPoint = e.getPoint();
            if (isDrawing) {
                mapComponent.repaint();
            }
        }

        public void paintShapes(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            if (isDrawing && currentPoint != null) {
                BasicStroke stroke = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[] {3f, 3f}, 0.0f);
                g2.setStroke(stroke);
                g2.setColor(Color.BLUE);
                g2.draw(new Line2D.Double(path.getCurrentPoint(), currentPoint));
            }
            if (path != null) {
                g2.setStroke(new BasicStroke(2));
                g2.setColor(Color.BLUE);
                g2.draw(path);
                fillPolygon(g2, path);
            }
        }
        
        @Override
        public void paint(Graphics g) {
            paintShapes(g);
        }

        private void fillPolygon(Graphics2D g2, Path2D.Double path) {
            AffineTransform transform = new AffineTransform();
            PathIterator pathIterator = path.getPathIterator(transform);
            double[] d = new double[2];
            List<Double[]> coords = new ArrayList<Double[]>();

            int i=0;
            while(!pathIterator.isDone()) {
                pathIterator.currentSegment(d);
                coords.add(new Double[] { d[0], d[1] });
                pathIterator.next();
                i++;
            }

            int[] xCoords = new int[i];
            int[] yCoords = new int[i];

            for(int j=0;j<coords.size();j++) {
                xCoords[j] = (coords.get(j)[0]).intValue();
                yCoords[j] = (coords.get(j)[1]).intValue();
            }

            g2.setColor(new Color(Color.BLUE.getRed(), Color.BLUE.getGreen(), Color.BLUE.getBlue(), 10));
            if(i>0) {
                g2.fillPolygon(xCoords, yCoords, i);
            }

        }

        private Point2D.Double getLastPoint() {
            PathIterator pathIterator = mapComponent.getDrawLayer().path.getPathIterator(new AffineTransform());

            double[] coors = new double[2];

            while(!pathIterator.isDone()) {
                pathIterator.currentSegment(coors);
                pathIterator.next();
            }
            return new Point2D.Double(coors[0], coors[1]);
        }

        private Path2D.Double removeLastPoint(Path2D.Double path, List<LonLat> shape) {
            AffineTransform transform = new AffineTransform();
            PathIterator pathIterator = path.getPathIterator(transform);
            double[] d = new double[2];

            Path2D.Double newPath = new Path2D.Double();

            int i=0;
            while(!pathIterator.isDone()) {
                pathIterator.currentSegment(d);
                pathIterator.next();
                if(i==0) {
                    newPath.moveTo(d[0], d[1]);
                }
                else if(!pathIterator.isDone()){
                    newPath.lineTo(d[0], d[1]);
                }
                i++;
            }
            if(shape.size()>0) {
                shape.remove(shape.size()-1);
            }
            return newPath;
        }

        @Override
        public void keyTyped(KeyEvent e) { }

        @Override
        public void keyPressed(KeyEvent e) { }

        @Override
        public void keyReleased(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_DELETE) {
                path = removeLastPoint(path, shape);
                mapComponent.repaint();
            }
        }
    }
    
    public static class RestrictionLayer implements MapLayer {

        Path2D.Double path;
        MapComponent mapComponent;
        List<LonLat> shape;

        public RestrictionLayer(MapComponent mapComponent, Path2D.Double restrictionShapePath, List<LonLat> restrictionShape) {
            this.mapComponent = mapComponent;
            path = restrictionShapePath;
            shape = restrictionShape;
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            ZoomLookup zoomLookup = mapComponent.tileLookup.getZoomLookup(mapComponent.getModel().getZoom());
            if(shape.size()>0) {
                path.reset();
                path = new Path2D.Double();
                for(int i=0; i< shape.size(); i++) {
                    LonLat lonLat = shape.get(i);
                    Point p = zoomLookup.getScreenPoint(mapComponent.getModel().getTopLeft(), lonLat);
                    if(i==0) {
                        path.moveTo(p.getX(), p.getY());
                    }
                    else {
                        path.lineTo(p.getX(),p.getY());
                    }
                }
                path.closePath();
            }

            fillPolygon(g2, path);

            if (path != null) {
                g2.setStroke(new BasicStroke(1));
                g2.setColor(Color.BLACK);
                g2.draw(path);
            }
        }

        private void fillPolygon(Graphics2D g2, Path2D.Double path) {
            AffineTransform transform = new AffineTransform();
            PathIterator pathIterator = path.getPathIterator(transform);
            double[] d = new double[2];
            List<Double[]> coords = new ArrayList<Double[]>();

            int i=0;
            while(!pathIterator.isDone()) {
                pathIterator.currentSegment(d);
                coords.add(new Double[] { d[0], d[1] });
                pathIterator.next();
                i++;
            }

            int[] xCoords = new int[i];
            int[] yCoords = new int[i];

            for(int j=0;j<coords.size();j++) {
                xCoords[j] = (coords.get(j)[0]).intValue();
                yCoords[j] = (coords.get(j)[1]).intValue();
            }

            g2.setColor(new Color(Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue(), 30));
            g2.fillPolygon(xCoords, yCoords, i);
        }
    }
    
    public static class IndividualRestriction extends RestrictionLayer {
        String shapeName;
        LonLat centerPoint;

        public IndividualRestriction(MapComponent mapComponent, Path2D.Double restrictionShapePath,
                                     List<LonLat> restrictionShape, String shapeName, LonLat center) {
            super(mapComponent, restrictionShapePath, restrictionShape);
            this.shapeName = shapeName;
            this.centerPoint = center;
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);    //To change body of overridden methods use File | Settings | File Templates.
            Graphics2D g2 = (Graphics2D) g;
            ZoomLookup zoomLookup = mapComponent.getTileLookup().getZoomLookup(mapComponent.getModel().getZoom());
            Point p = zoomLookup.getScreenPoint(mapComponent.getModel().getTopLeft(), centerPoint);

            FontMetrics fm = g2.getFontMetrics();

            int width = fm.stringWidth(this.shapeName);
            int height = fm.getHeight();
            int factor = 5;

            g2.setColor(new Color(Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue(), 40));
            g2.fillRoundRect(p.x-width/2-factor, p.y-height/2-factor, width+factor*2, height+factor, 10, 10);
            g2.setColor(Color.black);
            g2.drawRoundRect(p.x-width/2-factor, p.y-height/2-factor, width+factor*2, height+factor, 10, 10);

            g2.setColor(SystemColor.textText);
            g2.drawString(this.shapeName, p.x-width/2, p.y+2);
        }
        
    }
    
    private final List<MapLayer> layers = new ArrayList<MapLayer>();

    public Path2D.Double convertLonLatToPath2D(List<LonLat> list, ZoomLookup zoomLookup) {
        Path2D.Double path = new Path2D.Double();

        int i=0;

        for(LonLat lonLat: list) {
            if (i == 0) {
                path.moveTo(zoomLookup.getPoint(lonLat).getX(), zoomLookup.getPoint(lonLat).getY());
            } else {
                path.lineTo(zoomLookup.getPoint(lonLat).getX(), zoomLookup.getPoint(lonLat).getY());
            }
            i++;
        }
        if(list.size()>0) {
            path.closePath();
        }

        return path;
    }
    
    public void addLayer(MapLayer layer) {
        layers.add(layer);
    }
    
    public void removeLayer(MapLayer layer) {
        layers.remove(layer);
    }
    
    public RestrictionLayer getRestrictionLayer() {
        for(MapLayer layer: layers) {
            if(layer instanceof RestrictionLayer)
                return (RestrictionLayer)layer;
        }
        return null;
    }
    
    public DrawingLayer getDrawLayer() {
        System.out.println("getDrawLayer");
        for(MapLayer layer: layers) {
            if( layer instanceof DrawingLayer) {
                return (DrawingLayer)layer;
            }
        }
        return null;
    }

    public LonLat[] getBounds(List<LonLat> shape) {
        LonLat topLeft = new LonLat(180,-90), bottomRight = new LonLat(-180,90);

        for(LonLat lonLat: shape) {
            if(topLeft.getLon() > lonLat.getLon()) {
                topLeft = new LonLat(lonLat.getLon(), topLeft.getLat());
            }
            if(topLeft.getLat() < lonLat.getLat()) {
                topLeft = new LonLat(topLeft.getLon(), lonLat.getLat());
            }
            if(bottomRight.getLon() < lonLat.getLon()) {
                bottomRight = new LonLat(lonLat.getLon(), bottomRight.getLat());
            }
            if(bottomRight.getLat() > lonLat.getLat()) {
                bottomRight = new LonLat(bottomRight.getLon(), lonLat.getLat());
            }
        }

        return new LonLat[] { topLeft, bottomRight };
    }

    public MapComponent() {
        setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                repaint();
            }
        });
        final MapMouseAdapter mouseAdapter = new MapMouseAdapter();
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        addMouseWheelListener(mouseAdapter);

        model.addPropertyChangeListener("zoom", new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                repaint();
            }
        });
        model.addPropertyChangeListener("topLeft", new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                repaint();
            }
        });
    }

    public MapModel getModel() {
        return model;
    }

    public TileLookup getTileLookup() {
        return tileLookup;
    }

    public void setTileLookup(final TileLookup value) {
        final TileLookup oldValue = this.tileLookup;
        this.tileLookup = value;
        firePropertyChange("tileLookup", oldValue, value);
        repaint();
    }

    public List<LonLat> addLonLatToShape(String[] lat, String[] lon) {
        List<LonLat> lonLatList = new ArrayList<LonLat>();

        for(int i=0;i<lat.length;i++) {
            lonLatList.add(new LonLat(Double.parseDouble(lat[i].replace(',','.')),Double.parseDouble(lon[i].replace(',','.'))));
        }
        return lonLatList;
    }
    public List<LonLat> addLonLatToShape(ArrayList<Double> lat, ArrayList<Double> lon) {
        List<LonLat> lonLatList = new ArrayList<LonLat>();

        for(int i=0;i<lat.size();i++) {
            lonLatList.add(new LonLat(lat.get(i),lon.get(i)));
        }
        return lonLatList;
    }

    @Override
    public void paint(final Graphics g) {
        // Cancel all previous not-done downloads, so what we request now is what becomes important.
        for (Future<?> task : tasks) {
            task.cancel(false);
        }
        tasks.clear();
        if (tileLookup != null && model.getTopLeft() != null) {
            final TileInfo tileInfo = tileLookup.getTileInfo(model.getZoom(), model.getTopLeft(), getSize());
            for (final TileData tileData : tileInfo.getTileData()) {
                final Image image = tileLookup.getImageFast(tileData);
                g.drawImage(image, tileData.getX(), tileData.getY(), tileData.getWidth(), tileData.getHeight(), null);

                if (!tileLookup.exists(tileData)) {
                    tasks.add(executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            tileLookup.getImage(tileData);
                            repaint();
                        }
                    }));
                }
            }
        } else {
            final Image image = Toolkit.getDefaultToolkit().getImage(MapComponent.class.getResource("world.png"));
            g.drawImage(image, 0, 0, null);
        }
        for (MapLayer layer : layers) {
            layer.paint(g);
        }
    }

    public interface Controller {
        void onZoomIn(MapModel model, TileLookup tileLookup, Dimension size, Point point);

        void onZoomOut(MapModel model, TileLookup tileLookup, Dimension size, Point point);

        void mapDragged(MapModel model, TileLookup tileLookup, Dimension size, int xOffset, int yOffset);

    }

    private class MapMouseAdapter extends MouseAdapter {

        private Point mouseDownPoint;
        private Cursor oldCursor;

        @Override
        public void mouseWheelMoved(final MouseWheelEvent e) {
            if (model.isNavigationEnabled()) {
                if (e.getWheelRotation() < 0) {
                    controller.onZoomIn(model, tileLookup, getSize(), new Point(e.getX(), e.getY()));
                } else {
                    controller.onZoomOut(model, tileLookup, getSize(), new Point(e.getX(), e.getY()));
                }
                for(MapLayer layer: layers) {
                    if(layer instanceof DrawingLayer) {
                        ((DrawingLayer)layer).mouseWheelMoved(e);
                    }
                }
                repaint();
            }
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            if (model.isNavigationEnabled()) {
                mouseDownPoint = e.getPoint();
                oldCursor = getCursor();
                setCursor(new Cursor(Cursor.MOVE_CURSOR));
            }
        }

        @Override
        public void mouseDragged(final MouseEvent e) {
            if (mouseDownPoint != null && model.isNavigationEnabled()) {
                final int xDelta = e.getPoint().x - mouseDownPoint.x;
                final int yDelta = e.getPoint().y - mouseDownPoint.y;
                controller.mapDragged(model, tileLookup, getSize(), xDelta, yDelta);
                mouseDownPoint = e.getPoint();
                repaint();
            }
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            if (model.isNavigationEnabled()) {
                setCursor(oldCursor);
                mouseDownPoint = null;
            }
        }
    }
}
