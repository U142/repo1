package no.ums.map.tiled.cache;

import java.awt.Image;
import java.net.URI;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public interface ImageStore {

    Image getImage(String hostBase, URI uri);

    boolean exists(String hostBase, URI uri);

}
