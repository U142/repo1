package no.ums.map.tiled;

import org.junit.Test;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class ZoomTileLookupTest {

    
    @Test
    public void testGetTiles() {
        ZoomLookup zoomLookup = new ZoomLookup(1, 256);
        List<Point> tilePoints = zoomLookup.getTiles(new LonLat(-90, 45), new LonLat(90, -45));
        assertThat(tilePoints, equalTo(Arrays.asList(new Point(0, 0), new Point(1, 0), new Point(0, 1), new Point(1, 1))));
    }

    /* All these tests assumed linear latitude against x, which is not correct. So they are all broken.
    @Test
    public void testGetTilesNorth() {
        ZoomLookup zoomLookup = new ZoomLookup(1, 18);
        List<Point> tilePoints = zoomLookup.getTiles(new MapBounds(-180, 90, 180, 0));
        assertThat(tilePoints, equalTo(Arrays.asList(new Point(0, 0), new Point(1, 0))));
    }

    @Test
    public void testGetTilesZoom2() {
        ZoomLookup zoomLookup = new ZoomLookup(2, 18);
        List<Point> tilePoints = zoomLookup.getTiles(new MapBounds(-90, 45, 90, -45));
        assertThat(tilePoints, equalTo(Arrays.asList(new Point(1, 1), new Point(2, 1), new Point(1, 2), new Point(2, 2))));
    }
*/

    //    These test are wrong as they assume pixels are linear against latitude
//    @Test
//    public void testGetPoint() {
//        ZoomLookup zoomLookup = new ZoomLookup(1, 18);
//        assertThat(zoomLookup.getPoint(new LonLat(-180, 90)), equalTo(new Point(0, 0)));
//        assertThat(zoomLookup.getPoint(new LonLat(-90, 45)), equalTo(new Point(128, 128)));
//        assertThat(zoomLookup.getPoint(new LonLat(0, 0)), equalTo(new Point(256, 256)));
//        assertThat(zoomLookup.getPoint(new LonLat(90, -45)), equalTo(new Point(384, 384)));
//        assertThat(zoomLookup.getPoint(new LonLat(180, -90)), equalTo(new Point(512, 512)));
//    }

    @Test
    public void testGetLon() {
        ZoomLookup zoomLookup = new ZoomLookup(1, 256);
        assertThat(zoomLookup.getLonLat(0,0).getLon(), closeTo(-180, .01));
        assertThat(zoomLookup.getLonLat(128,0).getLon(), closeTo(-90, .01));
        assertThat(zoomLookup.getLonLat(256,0).getLon(), closeTo(0, .01));
        assertThat(zoomLookup.getLonLat(384,0).getLon(), closeTo(90, .01));
        assertThat(zoomLookup.getLonLat(512,0).getLon(), closeTo(180, .01));
    }

//    These test are wrong as they assume pixels are linear against latitude
//    @Test
//    public void testGetLat() {
//        ZoomLookup zoomLookup = new ZoomLookup(1, 18);
//        assertThat(zoomLookup.getLat(new MapBounds(-180, 90, 180, -90), 0), closeTo(89, .2));
//        assertThat(zoomLookup.getLat(new MapBounds(-180, 90, 180, -90), 128), closeTo(45, .01));
//        assertThat(zoomLookup.getLat(new MapBounds(-180, 90, 180, -90), 256), closeTo(0, .01));
//        assertThat(zoomLookup.getLat(new MapBounds(-180, 90, 180, -90), 384), closeTo(-45, .01));
//        assertThat(zoomLookup.getLat(new MapBounds(-180, 90, 180, -90), 512), closeTo(-90, .1));
//        assertThat(zoomLookup.getLat(new MapBounds(0, 0, 180, -90), 128), closeTo(-45, .01));
//    }
}
