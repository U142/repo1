package no.ums.pas.swing;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public interface MutableTypedListModel<T> extends TypedListModel<T> {

    void clear();

    void add(T value);

    void add(int index, T value);

    void addAll(int index, Iterable<? extends T> values);

    void remove(int index);

    void set(int index, T value);

    void setAll(Iterable<? extends T> values);

}
