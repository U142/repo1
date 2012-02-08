package no.ums.map.tiled.component;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.map.tiled.LonLat;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public final class MapModel {
    private static final Log log = UmsLog.getLogger(MapModel.class);
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

//    private MapBounds bounds;
//    private MapBounds viewBounds;
    private String layer;
    private boolean navigationEnabled = true;
    private int zoom = 1;
    private LonLat topLeft = new LonLat(0, 0);

    public LonLat getTopLeft() {
        return topLeft;
    }

    public void setTopLeft(final LonLat value) {
        final LonLat oldValue = this.topLeft;
        this.topLeft = value;
        update("topLeft", oldValue, value);
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(final int value) {
        final int oldValue = this.zoom;
        this.zoom = value;
        update("zoom", oldValue, value);
    }


    public boolean isNavigationEnabled() {
        return navigationEnabled;
    }

    /**
     * Controls wether or not navigation should be enabled for the map. This includes
     * drag to move, and scroll to zoom.
     * <p/>
     * Please note that changing this value while dragging could have undetermined results.
     *
     * @param value enabled navigation.
     */
    public void setNavigationEnabled(final boolean value) {
        final boolean oldValue = this.navigationEnabled;
        this.navigationEnabled = value;
        update("navigationEnabled", oldValue, value);
    }

//    public MapBounds getBounds() {
//        return bounds;
//    }
//
//    public void setBounds(MapBounds bounds) {
//        MapBounds oldBounds = this.bounds;
//        this.bounds = bounds;
//        update("bounds", oldBounds, bounds);
//    }
//
//    public MapBounds getViewBounds() {
//        return viewBounds;
//    }
//
//    public void setViewBounds(MapBounds viewBounds) {
//        MapBounds oldBounds = this.viewBounds;
//        this.viewBounds = viewBounds;
//        update("bounds", oldBounds, viewBounds);
//    }

    public String getLayer() {
        return layer;
    }

    public void setLayer(final String layer) {
        String oldLayer = this.layer;
        this.layer = layer;
        update("layer", oldLayer, layer);
    }


    private void update(final String property, final Object oldValue, final Object newValue) {
        support.firePropertyChange(property, oldValue, newValue);
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
        support.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
        support.removePropertyChangeListener(propertyName, listener);
    }
}
