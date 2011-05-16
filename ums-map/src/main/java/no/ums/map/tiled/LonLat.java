package no.ums.map.tiled;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class LonLat {

    private final double lon;
    private final double lat;

    public LonLat(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("LonLat");
        sb.append("{lon=").append(lon);
        sb.append(", lat=").append(lat);
        sb.append('}');
        return sb.toString();
    }
}
