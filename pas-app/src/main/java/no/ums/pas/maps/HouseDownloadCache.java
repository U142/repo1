package no.ums.pas.maps;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.map.tiled.LonLat;
import no.ums.map.tiled.TileCell;
import no.ums.map.tiled.ZoomLookup;
import no.ums.pas.PAS;
import no.ums.pas.PasApplication;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.pas.UAddress;
import no.ums.ws.pas.UAddressList;
import no.ums.ws.pas.UMapAddressParams;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class HouseDownloadCache {

    private static final List<UAddress> EMPTY = Collections.emptyList();
    private static final Log log = UmsLog.getLogger(HouseDownloadCache.class);

    private static final int MIN_ZOOM = 16;
    private static final int MAX_ZOOM = 18;

    private static final ZoomLookup[] zoomLookups = new ZoomLookup[MAX_ZOOM+1];

    static {
        for (int z=MIN_ZOOM; z<=MAX_ZOOM; z++) {
            zoomLookups[z] = new ZoomLookup(z, 256);
        }
    }

    private final ConcurrentMap<TileCell, List<UAddress>> cache = new MapMaker().softValues().makeComputingMap(new Function<TileCell, List<UAddress>>() {
        @Override
        public List<UAddress> apply(TileCell tileCell) {
            if (tileCell.getZoom() < MIN_ZOOM || tileCell.getZoom() > MAX_ZOOM) {
                return EMPTY;
            }
            final LonLat topLeft = zoomLookups[tileCell.getZoom()].getTopLeft(tileCell.getRow(), tileCell.getColumn());
            final LonLat bottomRight = zoomLookups[tileCell.getZoom()].getBottomRight(tileCell.getRow(), tileCell.getColumn());

            if (tileCell.getZoom() > MIN_ZOOM) {
                final List<UAddress> houseInfos = cache.get(new TileCell(tileCell.getZoom() - 1, tileCell.getRow() / 2, tileCell.getColumn() / 2));
                return Lists.newArrayList(Iterators.filter(houseInfos.iterator(), new Predicate<UAddress>() {
                    @Override
                    public boolean apply(@Nullable UAddress input) {
                        return input != null
                                && input.getLon() < topLeft.getLat()
                                && input.getLon() > bottomRight.getLat()
                                && input.getLat() > topLeft.getLon()
                                && input.getLat() < bottomRight.getLon();
                    }
                }));
            }

            UMapAddressParams mapAddressParams = new UMapAddressParams();
            ULOGONINFO logonInfo = new ULOGONINFO();
            mapAddressParams.setLBo(topLeft.getLon());
            mapAddressParams.setRBo(bottomRight.getLon());
            mapAddressParams.setUBo(topLeft.getLat());
            mapAddressParams.setBBo(bottomRight.getLat());
            logonInfo.setSzStdcc(PAS.get_pas().get_userinfo().get_current_department().get_stdcc());
            logonInfo.setLDeptpk(PAS.get_pas().get_userinfo().get_current_department().get_deptpk());
            log.trace("Downloading Cell, zoom: %d, row: %d, column: %d", tileCell.getZoom(), tileCell.getRow(), tileCell.getColumn());
            final UAddressList addressList = PasApplication.getInstance().getPaswsSoap().getAddressList(mapAddressParams, logonInfo);
            return addressList.getList().getUAddress();
        }
    });


    public List<UAddress> getHouseInfos(int zoom, int row, int column) {
        return (zoom < MIN_ZOOM) ? EMPTY : cache.get(new TileCell(zoom, row, column));
    }

    public boolean isAvailable(int zoom, int row, int column) {
        return zoom < MIN_ZOOM || cache.containsKey(new TileCell(zoom, row, column));
    }
}
