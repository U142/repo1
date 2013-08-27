package no.ums.map.tiled;

import no.ums.map.tiled.component.MapComponent;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Locale;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public final class MapTileMain {
    private static final File TILES = new File("/storage/Geodata/GeocacheBasis/tiles");

    private MapTileMain() {
    }

    public static void main(final String[] args) {
//        PasApplication.init("https://secure.ums2.no/PAS/experimental/parm2/ws");

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                final  TestFrame mainFrame = new TestFrame();
                mainFrame.getMapComponent1().getModel().setTopLeft(new LonLat(5.7098, 58.8188));
                mainFrame.getMapComponent1().getModel().setZoom(4);
//                final TileCacheGoogle googleTileCache = new TileCacheGoogle();
//                final TileCacheLocal localTileCache = new TileCacheLocal(TILES);
//                final TileCachePas pas = new TileCachePas();
//                final TileCacheFleximap fleximap = new TileCacheFleximap();
//                final TileCacheWms wms = new TileCacheWms("http", "www.webatlas.no", "/wms-UMs", "1.1.1",
//                        "image/png", "ortofoto");
                /*
                // Open Street Map
                final TileCacheOsm osmTileCache = new TileCacheOsm(TileCacheOsm.Layer.MAPNIK);
                mainFrame.getMapComponent1().setTileLookup(new TileLookupImpl(osmTileCache));
                */

                /*
                // Default Geodata
                final TileCacheGeodataDefault tileCacheGeodataDefault = new TileCacheGeodataDefault(TileCacheGeodataDefault.Layer.GeocacheBasis);
                mainFrame.getMapComponent1().setTileLookup(new TileLookupImpl(tileCacheGeodataDefault));
                */

                // UMS Tiled Geodata
                final TileCacheGeodata tileCacheGeodata = new TileCacheGeodata(TileCacheGeodata.Layer.GeocacheBasis);
                mainFrame.getMapComponent1().setTileLookup(new TileLookupImpl(tileCacheGeodata));

                mainFrame.getMapComponent1().addMouseMotionListener(new MouseAdapter() {
                    @Override
                    public void mouseMoved(final MouseEvent e) {
                        final MapComponent map = (MapComponent) e.getComponent();
                        final ZoomLookup zoomLookup = map.getTileLookup().getZoomLookup(map.getModel().getZoom());
                        final LonLat ll = zoomLookup.getLonLat(map.getModel().getTopLeft(), e.getX(), e.getY());
                        mainFrame.getLblLon().setText(String.format(Locale.ENGLISH, "%8.2f", ll.getLon()));
                        mainFrame.getLblLat().setText(String.format(Locale.ENGLISH, "%8.2f", ll.getLat()));
                        mainFrame.getLblZoom().setText(String.valueOf(zoomLookup.getZoomLevel()));
                    }
                });
                //mainFrame.getMapComponent1().addLayer(new MapComponent.DrawingLayer(mainFrame.getMapComponent1()));
                mainFrame.setVisible(true);
            }
        });
    }
}
