package no.ums.pas.core.map;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class MapPos {

    private final int pixelX;
    private final int pixelY;
    private final double lon;
    private final double lat;
    private final double utmNorthing;
    private final double utmEasting;
    private final String utmZone;

    private MapPos(int pixelX, int pixelY, double lon, double lat, double utmNorthing, double utmEasting, String utmZone) {
        this.pixelX = pixelX;
        this.pixelY = pixelY;
        this.lon = lon;
        this.lat = lat;
        this.utmNorthing = utmNorthing;
        this.utmEasting = utmEasting;
        this.utmZone = utmZone;
    }

    public int getPixelX() {
        return pixelX;
    }

    public int getPixelY() {
        return pixelY;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public double getUtmNorthing() {
        return utmNorthing;
    }

    public double getUtmEasting() {
        return utmEasting;
    }

    public String getUtmZone() {
        return utmZone;
    }
}
package no.ums.pas.core.map;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class MapPos {

    private final int pixelX;
    private final int pixelY;
    private final double lon;
    private final double lat;
    private final double utmNorthing;
    private final double utmEasting;
    private final String utmZone;

    private MapPos(int pixelX, int pixelY, double lon, double lat, double utmNorthing, double utmEasting, String utmZone) {
        this.pixelX = pixelX;
        this.pixelY = pixelY;
        this.lon = lon;
        this.lat = lat;
        this.utmNorthing = utmNorthing;
        this.utmEasting = utmEasting;
        this.utmZone = utmZone;
    }

    public int getPixelX() {
        return pixelX;
    }

    public int getPixelY() {
        return pixelY;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public double getUtmNorthing() {
        return utmNorthing;
    }

    public double getUtmEasting() {
        return utmEasting;
    }

    public String getUtmZone() {
        return utmZone;
    }
}
