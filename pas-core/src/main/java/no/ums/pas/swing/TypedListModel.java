package no.ums.pas.swing;

import javax.swing.ListModel;

/**
 * This is a specialization of the Swing ListModel that also takes a type.
 *
 * This is usefull for creating generic and safely typed ListModels. The
 * model also implements {@link Iterable} for easier iteration.
 *
 * @author Ståle Undheim <su@ums.no>
 */
public interface TypedListModel<T> extends ListModel, Iterable<T> {

    @Override
    T getElementAt(int index);
}