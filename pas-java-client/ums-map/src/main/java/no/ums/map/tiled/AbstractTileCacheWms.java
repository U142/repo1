package no.ums.map.tiled;

import com.google.common.io.Resources;

import javax.imageio.ImageIO;


import no.ums.map.tiled.CoorConverter.RdCoordinate;
import no.ums.map.tiled.CoorConverter.UTMCoor;
import no.ums.map.tiled.CoorConverter.WGS84Coordinate;

import org.jfree.util.Log;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public abstract class AbstractTileCacheWms extends AbstractTileCacheUri {

    private static final int TILE_SIZE = 256;

    public AbstractTileCacheWms(int maxZoom, int tileSize) {
        super(maxZoom, tileSize);
    }

    public AbstractTileCacheWms() {
        this(18, TILE_SIZE);
    }
    

    @Override
	public Image applyImageFilter(final Image img, final TileCell input) {
    	Image returnImage = img;
    	switch(getSrs())
    	{
    	case 28992:
    		break;
    	}
    	return returnImage;
    }

	@Override
    protected URI createUri(int zoom, int row, int column) {
        try {
    // http://webatlas.no/wms-UMs?SERVICE=WMS&LAYERS=ortofoto,Navn&FORMAT=image/png&TRANSPARENT=TRUE&REQUEST=GetMap&BBOX=%f,%f,%f,%f&WIDTH=%d&HEIGHT=%d&STYLES=,&SRS=EPSG:4326&VERSION=1.1.1
            final ZoomLookup zoomLookup = getZoomLookup(zoom);
            final LonLat ll1 = zoomLookup.getLonLat(column * TILE_SIZE, row * TILE_SIZE);
            final LonLat ll2 = zoomLookup.getLonLat(column * TILE_SIZE + TILE_SIZE, row * TILE_SIZE + TILE_SIZE);
            
            CoorConverter converter = new CoorConverter();

            double n_lbo, n_rbo, n_ubo, n_bbo;
            //convert coordinates depending on srs
            switch(getSrs())
            {
			 case 28992: //Amersfoort / RD New
				 double mid_lr = (ll2.getLon() + ll1.getLon()) / 2.0;
				 double mid_ub = (ll2.getLat() + ll1.getLat()) / 2.0;
				 RdCoordinate left = converter.wgs842rd_(converter.new WGS84Coordinate(ll1.getLon(), mid_ub));
				 RdCoordinate right = converter.wgs842rd_(converter.new WGS84Coordinate(ll2.getLon(), mid_ub));
				 RdCoordinate upper = converter.wgs842rd_(converter.new WGS84Coordinate(mid_lr, ll1.getLat()));
				 RdCoordinate bottom = converter.wgs842rd_(converter.new WGS84Coordinate(mid_lr, ll2.getLat()));
				 
				 n_lbo = left.x;
				 n_rbo = right.x;
				 n_bbo = bottom.y;
				 n_ubo = upper.y;
				 break;
			 case 32632: //UTM Zone 32N
			 case 32633: //UTM zone 33N
				 UTMCoor ul = converter.LL2UTM(23, ll2.getLon(), ll1.getLon());
				 UTMCoor lr = converter.LL2UTM(23, ll2.getLat(), ll1.getLat());
				 n_lbo = Math.round(ul.f_easting);
				 n_rbo = Math.round(lr.f_easting);
				 n_ubo = Math.round(ul.f_northing);
				 n_bbo = Math.round(lr.f_northing);
				 break;
			 case 4326: //lon/lat
			 default:
				 n_lbo = ll1.getLon();
				 n_rbo = ll2.getLon();
				 n_ubo = ll1.getLat();
				 n_bbo = ll2.getLat();
				 break;

            }
            String query;

            //Hack version for danish demo
            if (getHost().contains("kortforsyningen.kms.dk")) {
                query = String.format(Locale.ENGLISH, "SERVICE=WMS&servicename=topo_skaermkort&LAYERS=%s&FORMAT=%s&TRANSPARENT=TRUE&REQUEST=GetMap&BBOX=%f,%f,%f,%f&WIDTH=%d&HEIGHT=%d&STYLES=&SRS=EPSG:%s&VERSION=%s", getLayers(), getFormat(), n_lbo, n_bbo, n_rbo, n_ubo, TILE_SIZE, TILE_SIZE, getSrs(), getVersion());
            } else if(getVersion().contains("1.3")) {
            	query = String.format(Locale.ENGLISH, "SERVICE=WMS&LAYERS=%s&FORMAT=%s&TRANSPARENT=TRUE&REQUEST=GetMap&BBOX=%f,%f,%f,%f&WIDTH=%d&HEIGHT=%d&STYLES=&CRS=EPSG:%s&VERSION=%s", getLayers(), getFormat(), n_bbo, n_lbo, n_ubo, n_rbo, TILE_SIZE, TILE_SIZE, getSrs(), getVersion());
            } else {
            	query = String.format(Locale.ENGLISH, "SERVICE=WMS&LAYERS=%s&FORMAT=%s&TRANSPARENT=TRUE&REQUEST=GetMap&BBOX=%f,%f,%f,%f&WIDTH=%d&HEIGHT=%d&STYLES=&SRS=EPSG:%s&VERSION=%s", getLayers(), getFormat(), n_lbo, n_bbo, n_rbo, n_ubo, TILE_SIZE, TILE_SIZE, getSrs(), getVersion());
            }
            return new URI(getScheme(), getHost(), getPath(), query, null);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to create URI", e);
        }
    }

    public abstract String getScheme();

    public abstract String getHost();

    public abstract String getPath();

    public abstract String getVersion();

    public abstract String getFormat();

    public abstract String getLayers();
    
    public abstract int getSrs();
}
