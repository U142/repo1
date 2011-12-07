package no.ums.map.tiled;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class TileInfo {

    private final ImmutableList<TileData> tileData;

    public TileInfo(final Iterable<TileData> tileData) {

        final TileData[] sorted = Iterables.toArray(tileData, TileData.class);
        Arrays.sort(sorted, new Comparator<TileData>() {
            public int compare(final TileData o1, final TileData o2) {
                return ComparisonChain.start()
                        .compare(o1.getY(), o2.getY())
                        .compare(o1.getX(), o2.getX())
                        .result();
            }
        });
        this.tileData = ImmutableList.copyOf(sorted);
    }

    public final ImmutableList<TileData> getTileData() {
        return tileData;
    }

}
