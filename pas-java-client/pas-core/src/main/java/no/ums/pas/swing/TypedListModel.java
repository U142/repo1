package no.ums.pas.swing;

import javax.swing.ComboBoxModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This is a specialization of the Swing ListModel that also takes a type.
 *
 * This is usefull for creating generic and safely typed ListModels. The
 * model also implements {@link Iterable} for easier iteration.
 *
 * @author Ståle Undheim <su@ums.no>
 */
public interface TypedListModel<T> extends ListModel, ComboBoxModel, Iterable<T> {

    class Factory {
        public static <T> TypedListModel<T> of(final T ... values) {
            return new ImmutableTypedListModel<T>(Arrays.asList(values));
        }
        public static <T> TypedListModel<T> of(final List<T> values) {
            return new ImmutableTypedListModel<T>(values);
        }
    }

    @Override
    T getElementAt(int index);

    @Override
    T getSelectedItem();

    int indexOf(T value);

    boolean contains(T value);
}