package no.ums.map.tiled;

import org.junit.Test;

import static org.junit.Assert.assertThat;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class OsmTileLookupTest {

    // Input ->
    //  * top left (lon, lat)
    //  * dimensions (width and height in degrees)
    //  * size (width and height in pixels)
    // Output ->
    //  * dimensions (width and height in degrees)
    //  * List of tiles

    // Tile data
    //  * String img url
    //  * position (x,y) - may be negative
    //  * size (w, h) - w == h and always the same for all values in a list

    @Test
    public void testNoop() {

    }
/* All these tests assumed linear latitude against x, which is not correct. So they are all broken.
    @Test
    public void testLookupWorld() {
        final TileLookup lookup = new TileLookupImpl(new MockTileCache());
        final LonLat topLeft = new LonLat(-180, 90);
        final LonLatSpan mapDimensions = new LonLatSpan(360, 180);
        final Dimension size = new Dimension(512, 512);

        final TileInfo tileInfo = lookup.getTileInfo(new MapBounds(topLeft, mapDimensions), size);

        final LonLatSpan tileInfoDim = tileInfo.getMapBounds().getDimension();

        assertThat(tileInfoDim.getLonDegrees(), closeTo(360d, 0.1d));
//        assertThat(tileInfoDim.getLatDegrees(), closeTo(180d, 0.1d));

        final List<TileData> tileDataList = tileInfo.getTileData();

        assertThat(tileDataList.size(), equalTo(4));

        final List<TileData> expected = Arrays.asList(
                new TileData(1, 0, 0, 0, 0, 256, 256),
                new TileData(1, 0, 1, 256, 0, 256, 256),
                new TileData(1, 1, 0, 0, 256, 256, 256),
                new TileData(1, 1, 1, 256, 256, 256, 256));

        assertThat(tileDataList, equalTo(expected));
    }

    @Test
    public void testLookupWorldCenter() {
        final TileLookup lookup = new TileLookupImpl(new MockTileCache());
        final LonLat topLeft = new LonLat(-90, 45);
        final LonLatSpan mapDimensions = new LonLatSpan(180, 90);
        final Dimension size = new Dimension(256, 256);

        final TileInfo tileInfo = lookup.getTileInfo(new MapBounds(topLeft, mapDimensions), size);

        final LonLatSpan tileInfoDim = tileInfo.getMapBounds().getDimension();

        assertThat(tileInfoDim.getWidth(), closeTo(180d, 0.1d));
//        assertThat(tileInfoDim.getHeight(), closeTo(90d, 0.1d));

        final List<TileData> tileDataList = tileInfo.getTileData();

        assertThat(tileDataList.size(), equalTo(4));

        final List<TileData> expected = Arrays.asList(
                new TileData(1, 0, 0, -128, -128, 256, 256),
                new TileData(1, 0, 1, 128, -128, 256, 256),
                new TileData(1, 1, 0, -128, 128, 256, 256),
                new TileData(1, 1, 1, 128, 128, 256, 256));

        assertThat(tileDataList, equalTo(expected));

    }

    @Test
    public void testLookupWorldQuarter() {
        // Test to get the map tiles for the east and south 3/4 of the world
        final TileLookup lookup = new TileLookupImpl(new MockTileCache());
        final LonLat topLeft = new LonLat(-90, 45);
        final LonLatSpan mapDimensions = new LonLatSpan(270, 135);
        final Dimension size = new Dimension(384, 384);

        final TileInfo tileInfo = lookup.getTileInfo(new MapBounds(topLeft, mapDimensions), size);

        final LonLatSpan tileInfoDim = tileInfo.getMapBounds().getDimension();

        assertThat(tileInfoDim.getWidth(), closeTo(270d, 0.1d));
//        assertThat(tileInfoDim.getHeight(), closeTo(135d, 0.1d));

        final List<TileData> tileDataList = tileInfo.getTileData();

        assertThat(tileDataList.size(), equalTo(4));

        final List<TileData> expected = Arrays.asList(
                new TileData(1, 0, 0, -128, -128, 256, 256),
                new TileData(1, 0, 1, 128, -128, 256, 256),
                new TileData(1, 1, 0, -128, 128, 256, 256),
                new TileData(1, 1, 1, 128, 128, 256, 256));

        assertThat(tileDataList, equalTo(expected));

    }


    @Test
    public void testLookupNorthernHemisphere() {
        //
        final TileLookup lookup = new TileLookupImpl(new MockTileCache());
        final LonLat topLeft = new LonLat(-180, 90);
        final LonLatSpan mapDimensions = new LonLatSpan(360, 90);
        final Dimension size = new Dimension(512, 256);

        final TileInfo tileInfo = lookup.getTileInfo(new MapBounds(topLeft, mapDimensions), size);

        final LonLatSpan tileInfoDim = tileInfo.getMapBounds().getDimension();

        assertThat(tileInfoDim.getWidth(), closeTo(360d, 0.1d));
//        assertThat(tileInfoDim.getHeight(), closeTo(90d, 0.1d));

        final List<TileData> tileDataList = tileInfo.getTileData();

        final List<TileData> expected = Arrays.asList(
                new TileData(1, 0, 0, 0, 0, 256, 256),
                new TileData(1, 0, 1, 256, 0, 256, 256));

        assertThat(tileDataList, equalTo(expected));
    }

    @Test
    public void testLookupWorldLevel2() {
        final TileLookup lookup = new TileLookupImpl(new MockTileCache());
        final LonLat topLeft = new LonLat(-180, 90);
        final LonLatSpan mapDimensions = new LonLatSpan(360, 180);
        final Dimension size = new Dimension(1024, 1024);

        final TileInfo tileInfo = lookup.getTileInfo(new MapBounds(topLeft, mapDimensions), size);

        final LonLatSpan tileInfoDim = tileInfo.getMapBounds().getDimension();

        assertThat(tileInfoDim.getWidth(), closeTo(360d, 0.1d));
//        assertThat(tileInfoDim.getHeight(), closeTo(180d, 0.1d));

        final List<TileData> tileDataList = tileInfo.getTileData();

        final List<TileData> expected = Arrays.asList(
                new TileData(2, 0, 0, 0, 0, 256, 256),
                new TileData(2, 0, 1, 256, 0, 256, 256),
                new TileData(2, 0, 2, 512, 0, 256, 256),
                new TileData(2, 0, 3, 768, 0, 256, 256),
                new TileData(2, 1, 0, 0, 256, 256, 256),
                new TileData(2, 1, 1, 256, 256, 256, 256),
                new TileData(2, 1, 2, 512, 256, 256, 256),
                new TileData(2, 1, 3, 768, 256, 256, 256),
                new TileData(2, 2, 0, 0, 512, 256, 256),
                new TileData(2, 2, 1, 256, 512, 256, 256),
                new TileData(2, 2, 2, 512, 512, 256, 256),
                new TileData(2, 2, 3, 768, 512, 256, 256),
                new TileData(2, 3, 0, 0, 768, 256, 256),
                new TileData(2, 3, 1, 256, 768, 256, 256),
                new TileData(2, 3, 2, 512, 768, 256, 256),
                new TileData(2, 3, 3, 768, 768, 256, 256));

        assertThat(tileDataList, equalTo(expected));
    }


    @Test
    public void testLookupWorldCenterLevel2() {
        final TileLookup lookup = new TileLookupImpl(new MockTileCache());
        final LonLat topLeft = new LonLat(-90, 45);
        final LonLatSpan mapDimensions = new LonLatSpan(180, 90);
        final Dimension size = new Dimension(512, 512);

        final TileInfo tileInfo = lookup.getTileInfo(new MapBounds(topLeft, mapDimensions), size);

        final LonLatSpan tileInfoDim = tileInfo.getMapBounds().getDimension();

        assertThat(tileInfoDim.getWidth(), closeTo(180d, 0.1d));
//        assertThat(tileInfoDim.getHeight(), closeTo(90d, 0.1d));

        final List<TileData> tileDataList = tileInfo.getTileData();

        final List<TileData> expected = Arrays.asList(
                new TileData(2, 1, 1, 0, 0, 256, 256),
                new TileData(2, 1, 2, 256, 0, 256, 256),
                new TileData(2, 2, 1, 0, 256, 256, 256),
                new TileData(2, 2, 2, 256, 256, 256, 256));

        assertThat(tileDataList, equalTo(expected));
    }
*/
}
