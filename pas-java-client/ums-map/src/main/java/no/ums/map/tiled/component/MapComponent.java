package no.ums.map.tiled.component;

import no.ums.map.tiled.TileData;
import no.ums.map.tiled.TileInfo;
import no.ums.map.tiled.TileLookup;

import javax.swing.JComponent;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
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
public class MapComponent extends JComponent {

    private TileLookup tileLookup;
    private final MapModel model = new MapModel();
    private final Controller controller = new MapController();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final List<Future<?>> tasks = new ArrayList<Future<?>>();

    public MapComponent() {
        setPreferredSize(new Dimension(256, 256));
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repaint();
            }
        });
        final MapMouseAdapter mouseAdapter = new MapMouseAdapter();
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        addMouseWheelListener(mouseAdapter);

        model.addPropertyChangeListener("zoom", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                repaint();
            }
        });
        model.addPropertyChangeListener("topLeft", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
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

    public void setTileLookup(TileLookup value) {
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
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (model.isNavigationEnabled()) {
                if (e.getWheelRotation() < 0) {
                    controller.onZoomIn(model, tileLookup, getSize(), new Point(e.getX(), e.getY()));
                } else {
                    controller.onZoomOut(model, tileLookup, getSize(), new Point(e.getX(), e.getY()));
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (model.isNavigationEnabled()) {
                mouseDownPoint = e.getPoint();
                oldCursor = getCursor();
                setCursor(new Cursor(Cursor.MOVE_CURSOR));
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (mouseDownPoint != null && model.isNavigationEnabled()) {
                controller.mapDragged(model, tileLookup, getSize(), e.getPoint().x - mouseDownPoint.x, e.getPoint().y - mouseDownPoint.y);
                mouseDownPoint = e.getPoint();
                repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (model.isNavigationEnabled()) {
                setCursor(oldCursor);
                mouseDownPoint = null;
            }
        }
    }
}
