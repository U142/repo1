package no.ums.pas.swing.dynamictree;

import javax.annotation.Nonnull;
import java.util.concurrent.Future;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public interface TreeNodeFactory<T> {

    /**
     * Returns the identity for this value.
     *
     * The identity must correctly implement equals and hashcode, and it must be unique
     * for all types of values returned by this TreeNodeFactory.
     *
     * @param value an instance returned from this TreeNodeFactory.
     * @return
     */
    @Nonnull
    Object identity(@Nonnull T value);

    /**
     * Checks wether or not a given value is a leaf value or not.
     * @param value
     * @return
     */
    boolean isLeaf(@Nonnull T value);

    /**
     * Returns all the children, in the desired display order, for a value.
     *
     * The parent will be null if the root is null.
     *
     * @param parent to get children for.
     * @return iteration of children.
     */
    @Nonnull
    Iterable<? extends T> getChildren(@Nonnull T parent);
}
