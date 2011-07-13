package no.ums.pas.swing;

import com.google.common.collect.Lists;

import java.util.Iterator;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public abstract class AbstractMutableTypedListModel<T, C extends Iterable<T>> extends AbstractTypedListModel<T, C> implements MutableTypedListModel<T> {

    public AbstractMutableTypedListModel(C container) {
        super(container);
    }

    protected abstract void add(C container, int index, T value);
    protected abstract T remove(C container, int index);

    protected T set(C container, int index, T value) {
        T old = remove(container, index);
        add(Math.max(0, index-1), value);
        return old;
    }

    protected void clear(C container) {
        for (int i=sizeOf(container)-1; i>=0; i--) {
            remove(container, i);
        }
    }

    @Override
    public void clear() {
        final int oldSize = getSize();
        for (T t : this) {
            removeListener(t);
        }
        clear(container);
        fireIntervalRemoved(0, oldSize - 1);
    }

    @Override
    public void add(T value) {
        add(getSize(), value);
    }

    @Override
    public void add(int index, T value) {
        internalAdd(index, value);
        fireIntervalAdded(index, index);
    }

    private void internalAdd(int index, T value) {
        add(container, index, value);
        addListener(value);
    }

    @Override
    public void addAll(int index, Iterable<? extends T> values) {
        int endIndex = index;
        for (T value : values) {
            internalAdd(endIndex++, value);
        }
        fireIntervalAdded(index, endIndex);
    }

    @Override
    public void remove(int index) {
        final T removed = remove(container, index);
        removeListener(removed);
        fireIntervalRemoved(index, index);
    }

    @Override
    public void set(int index, T value) {
        final T oldValue = set(container, index, value);
        removeListener(oldValue);
        addListener(value);
        fireContentsChanged(index, index);
    }

    @Override
    public void setAll(Iterable<? extends T> values) {
        final int oldSize = getSize();
        clear();
        addAll(0, Lists.newArrayList(values));
        if (oldSize > getSize()) {
            fireContentsChanged(0, getSize() - 1);
            fireIntervalRemoved(getSize(), oldSize - 1);
        } else if (getSize() > oldSize) {
            fireContentsChanged(0, oldSize - 1);
            fireIntervalAdded(oldSize, getSize() - 1);
        } else {
            // Size matches.
            fireContentsChanged(0, getSize() - 1);
        }
    }

    @Override
    public Iterator<T> iterator() {
        final Iterator<T> delegate = container.iterator();
        return new Iterator<T>() {

            private T current;

            @Override
            public boolean hasNext() {
                return delegate.hasNext();
            }

            @Override
            public T next() {
                current = delegate.next();
                return current;
            }

            @Override
            public void remove() {
                delegate.remove();
                removeListener(current);
            }
        };
    }
}
