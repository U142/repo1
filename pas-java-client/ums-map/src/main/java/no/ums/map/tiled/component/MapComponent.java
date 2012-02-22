package no.ums.map.tiled.component;

import no.ums.map.tiled.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
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
        private Path2D.Double path = new Path2D.Double();
        private List<LonLat> shape = new ArrayList<LonLat>();

        public DrawingLayer(MapComponent mapComponent) {
            this.mapComponent = mapComponent;
            mapComponent.addLayer(this);
            mapComponent.addMouseMotionListener(this);
            mapComponent.addMouseListener(this);
            mapComponent.addKeyListener(this);
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            super.mouseWheelMoved(e);
            recalculate();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (!isDrawing && e.getButton() == MouseEvent.BUTTON1) {
                isDrawing = true;
                path = new Path2D.Double();
                path.moveTo(e.getX(), e.getY());
                shape.clear();
                ZoomLookup zoomLookup = mapComponent.tileLookup.getZoomLookup(mapComponent.getModel().getZoom());
                LonLat ll = zoomLookup.getLonLat(mapComponent.getModel().getTopLeft(), e.getX(),e.getY());
                shape.add(ll);
            }
            else if (isDrawing && e.getButton() == MouseEvent.BUTTON1) {
                path.lineTo(e.getX(), e.getY());
                ZoomLookup zoomLookup = mapComponent.tileLookup.getZoomLookup(mapComponent.getModel().getZoom());
                LonLat ll = zoomLookup.getLonLat(mapComponent.getModel().getTopLeft(), e.getX(),e.getY());
                shape.add(ll);
                mapComponent.repaint();
            } else if (isDrawing && e.getButton() == MouseEvent.BUTTON3) {
                isDrawing = false;
                path.closePath();
                recalculate();
                mapComponent.repaint();
            }
            mapComponent.requestFocus();
        }

        public Path2D.Double getPath() {
            return path;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            super.mouseDragged(e);
            currentPoint = e.getPoint();
            ZoomLookup zoomLookup = recalculate();

            LonLat ll = zoomLookup.getLonLat(mapComponent.getModel().getTopLeft(), e.getX(),e.getY());
        }

        private ZoomLookup recalculate() {
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

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            if (isDrawing && currentPoint != null) {
                BasicStroke stroke = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[] {3f, 3f}, 0.0f);
                g2.setStroke(stroke);
                g2.setColor(Color.BLUE);
                g2.draw(new Line2D.Double(path.getCurrentPoint(), currentPoint));
            }
            if (path != null) {
                g2.setStroke(new BasicStroke(1));
                g2.setColor(Color.BLACK);
                g2.draw(path);
                fillPolygon(g2, path);
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
            
            g2.setColor(new Color(Color.BLUE.getRed(), Color.BLUE.getGreen(), Color.BLUE.getBlue(), 10));
            g2.fillPolygon(xCoords, yCoords, i);
            
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
    
    private final List<MapLayer> layers = new ArrayList<MapLayer>();
    
    
    
    public void addLayer(MapLayer layer) {
        layers.add(layer);
    }
    
    public void removeLayer(MapLayer layer) {
        layers.remove(layer);
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
