package no.ums.map.tiled;

import com.google.common.base.Joiner;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class TileCacheWms extends AbstractTileCacheWms {

    private final String scheme;
    private final String host;
    private final String path;
    private final String version;
    private final String format;
    private final String layers;
    private final int srs;
    private final String wmsUser;
    private final String wmsPassword;

    public TileCacheWms(final String scheme, final String host, final String path, final String version,
                        final String format, final String wmsUser, final String wmsPassword, final Iterable<String> layers) {
        this.scheme = scheme;
        this.host = host;
        this.path = path;
        this.version = version;
        this.format = format;
        this.srs = 4326;
        this.wmsUser = wmsUser;
        this.wmsPassword = wmsPassword;
        this.layers = Joiner.on(",").join(layers);
    }

    public TileCacheWms(final String scheme, final String host, final String path, final String version,
                        final String format, int srs, final String wmsUser, final String wmsPassword, final Iterable<String> layers) {
        this.scheme = scheme;
        this.host = host;
        this.path = path;
        this.version = version;
        this.format = format;
        this.srs = srs;
        this.wmsUser = wmsUser;
        this.wmsPassword = wmsPassword;
        this.layers = Joiner.on(",").join(layers);
    }

    public TileCacheWms(final String scheme, final String host, final String path, final String version,
                        final String format, final String wmsUser, final String wmsPassword, final String ... layers) {
        this.scheme = scheme;
        this.host = host;
        this.path = path;
        this.version = version;
        this.format = format;
        this.srs = 4326;
        this.wmsUser = wmsUser;
        this.wmsPassword = wmsPassword;
        this.layers = Joiner.on(",").join(layers);
    }

    public TileCacheWms(final String scheme, final String host, final String path, final String version,
                        final String format, final int srs, final String wmsUser, final String wmsPassword, final String ... layers) {
        this.scheme = scheme;
        this.host = host;
        this.path = path;
        this.version = version;
        this.format = format;
        this.srs = srs;
        this.wmsUser = wmsUser;
        this.wmsPassword = wmsPassword;
        this.layers = Joiner.on(",").join(layers);
    }

    @Override
    public final String getScheme() {
        return scheme;
    }

    @Override
    public final String getHost() {
        return host;
    }

    @Override
    public final String getPath() {
        return path;
    }

    @Override
    public final String getVersion() {
        return version;
    }

    @Override
    public final String getFormat() {
        return format;
    }

    @Override
    public final String getLayers() {
        return layers;
    }

    @Override
    public String getWmsUser() {
        return wmsUser;
    }

    @Override
    public String getWmsPassword() {
        return wmsPassword;
    }

    /**
     * Return default srs for lon/lat WGS84
     */
	@Override
	public int getSrs() {
		return srs;
	}
}
