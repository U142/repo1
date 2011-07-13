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

    public TileCacheWms(String scheme, String host, String path, String version, String format, Iterable<String> layers) {
        this.scheme = scheme;
        this.host = host;
        this.path = path;
        this.version = version;
        this.format = format;
        this.layers = Joiner.on(",").join(layers);
    }

    public TileCacheWms(String scheme, String host, String path, String version, String format, String ... layers) {
        this.scheme = scheme;
        this.host = host;
        this.path = path;
        this.version = version;
        this.format = format;
        this.layers = Joiner.on(",").join(layers);
    }

    @Override
    public String getScheme() {
        return scheme;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public String getLayers() {
        return layers;
    }
}
