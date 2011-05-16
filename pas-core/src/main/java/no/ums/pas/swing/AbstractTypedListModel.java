package no.ums.pas.swing;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import org.jdesktop.beansbinding.AbstractBean;

import javax.annotation.Nullable;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public abstract class AbstractTypedListModel<T, C extends Iterable<T>> implements TypedListModel<T> {

    protected final C container;
    private final Set<ListDataListener> listenerSet = new HashSet<ListDataListener>();
    private T selectedItem;
    private final PropertyChangeListener changeListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            // Ignore that we are sending object to a method expecting T.
            @SuppressWarnings({"SuspiciousMethodCalls"})
            int index = indexOf(container, (T) evt.getSource());
            if (index != -1) {
                fireContentsChanged(index, index);
            }
        }
    };

    /**
     * Method to get the index of a variable.
     *
     * This method should be overridden for optimal performance.
     *
     * @param container backing this list
     * @param source value to get index of
     * @return the index off the source value in the container, or -1 if not found.
     */
    protected int indexOf(C container, final T source) {
        return Iterables.indexOf(container, new Predicate<T>() {
            @Override
            public boolean apply(@Nullable T input) {
                return input == source;
            }
        });
    }

    protected boolean contains(C container, T value) {
        return Iterables.contains(container, value);
    }



    /**
     * Method to get the value at a specific index.
     *
     * This method should be overridden for optimal performance.
     * @param container to get the element from
     * @param index of the element to get.
     * @return the element at the given index.
     */
    protected T elementAt(C container, int index) {
        return Iterables.get(container, index);
    }

    /**
     * Returns the size of the container.
     *
     * This method should be overridden for optimal perfomance.
     *
     * @param container to get the size off.
     * @return the size of the container.
     */
    protected int sizeOf(C container) {
        return Iterables.size(container);
    }

    @Override
    public int indexOf(T value) {
        return indexOf(container, value);
    }

    @Override
    public boolean contains(T value) {
        return contains(container, value);
    }

    public AbstractTypedListModel(C container) {
        this.container = container;
        for (T t : container) {
            addListener(t);
        }
    }

    @Override
    public T getElementAt(int index) {
        return elementAt(container, index);
    }


    @Override
    public T getSelectedItem() {
        return selectedItem;
    }

    @Override
    public void setSelectedItem(Object anItem) {
        if (anItem != selectedItem) {
            selectedItem = (T) anItem;
            fireContentsChanged(-1, -1);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return container.iterator();
    }

    @Override
    public int getSize() {
        return sizeOf(container);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listenerSet.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listenerSet.remove(l);
    }

    protected void fireContentsChanged(int start, int end) {
        if (start <= end) {
            // Copy array to avoid concurrent modification exceptions
            for (ListDataListener listener : listenerSet.toArray(new ListDataListener[listenerSet.size()])) {
                listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, start, end));
            }
        }
    }

    protected void fireIntervalAdded(int start, int end) {
        if (start <= end) {
            // Copy array to avoid concurrent modification exceptions
            for (ListDataListener listener : listenerSet.toArray(new ListDataListener[listenerSet.size()])) {
                listener.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, start, end));
            }
        }
    }

    protected void fireIntervalRemoved(int start, int end) {
        if (start <= end) {
            // Copy array to avoid concurrent modification exceptions
            for (ListDataListener listener : listenerSet.toArray(new ListDataListener[listenerSet.size()])) {
                listener.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, start, end));
            }
        }
    }

    protected void addListener(T value) {
        if (value instanceof AbstractBean) {
            ((AbstractBean) value).addPropertyChangeListener(changeListener);
        }
    }

    protected void removeListener(T removed) {
        if (removed instanceof AbstractBean) {
            ((AbstractBean) removed).removePropertyChangeListener(changeListener);
        }
    }
}
