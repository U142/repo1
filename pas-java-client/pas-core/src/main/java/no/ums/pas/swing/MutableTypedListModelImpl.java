package no.ums.pas.swing;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class MutableTypedListModelImpl<T> extends AbstractMutableTypedListModel<T, List<T>> {

    public MutableTypedListModelImpl() {
        super(new ArrayList<T>());
    }

    @Override
    protected int indexOf(List<T> container, T source) {
        return container.indexOf(source);
    }

    @Override
    protected boolean contains(List<T> container, T value) {
        return container.contains(value);
    }

    @Override
    protected T elementAt(List<T> container, int index) {
        return container.get(index);
    }

    @Override
    protected int sizeOf(List<T> container) {
        return container.size();
    }

    @Override
    protected void add(List<T> container, int index, T value) {
        container.add(index, value);
    }

    @Override
    protected T remove(List<T> container, int index) {
        return container.remove(index);
    }

    @Override
    protected T set(List<T> container, int index, T value) {
        return container.set(index, value);
    }

    @Override
    protected void clear(List<T> container) {
        container.clear();
    }
}
