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

    public TileCacheWms(final String scheme, final String host, final String path, final String version,
                        final String format, final Iterable<String> layers) {
        this.scheme = scheme;
        this.host = host;
        this.path = path;
        this.version = version;
        this.format = format;
        this.layers = Joiner.on(",").join(layers);
    }

    public TileCacheWms(final String scheme, final String host, final String path, final String version,
                        final String format, final String ... layers) {
        this.scheme = scheme;
        this.host = host;
        this.path = path;
        this.version = version;
        this.format = format;
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

    /**
     * Return default srs for lon/lat WGS84
     */
	@Override
	public int getSrs() {
		return 4326;
	}
}
