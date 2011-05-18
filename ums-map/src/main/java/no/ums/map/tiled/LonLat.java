package no.ums.map.tiled;

import javax.print.Doc;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class LonLat {

    public static final LonLat NONE = new LonLat(Double.NaN, Double.NaN);

    private static final double DEG_TO_RAD = Math.PI/180;

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

    public double distanceToInM(LonLat other) {
        double y1r = 0;
		if(lat>0 && other.lat>0) {
            y1r = Math.min(lat, other.lat) * DEG_TO_RAD;
        }
		else if(lat<0 && other.lat<0) {
            y1r = Math.max(lat, other.lat) * DEG_TO_RAD;
        }
		double w = Math.abs(lon - other.lon) * 3600 * 30.92 * Math.cos(y1r);
		double h = Math.abs(lat - other.lat) * 3600 * 30.92;
        return Math.sqrt(w*w+h*h);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LonLat lonLat = (LonLat) o;

        return Double.compare(lonLat.lat, lat) == 0 && Double.compare(lonLat.lon, lon) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = lon != +0.0d ? Double.doubleToLongBits(lon) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        temp = lat != +0.0d ? Double.doubleToLongBits(lat) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
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
