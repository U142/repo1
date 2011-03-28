package no.ums.pas.swing;

import com.google.common.collect.Lists;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class MutableTypedListModelImpl<T> implements MutableTypedListModel<T> {

    private final List<ListDataListener> listeners = new ArrayList<ListDataListener>();
    private final List<T> content = new ArrayList<T>();

    @Override
    public void clear() {
        final int oldSize = getSize();
        content.clear();
        fireIntervalRemoved(0, oldSize-1);
    }

    @Override
    public void add(T value) {
        add(getSize(), value);
    }

    @Override
    public void add(int index, T value) {
        content.add(index, value);
        fireIntervalAdded(index,  index);
    }

    @Override
    public void addAll(int index, Iterable<? extends T> values) {
        int endIndex = index;
        for (T value : values) {
            content.add(endIndex++, value);
        }
        fireIntervalAdded(index, endIndex);
    }

    @Override
    public void remove(int index) {
        content.remove(index);
        fireIntervalRemoved(index, index);
    }

    @Override
    public void set(int index, T value) {
        content.set(index, value);
        fireContentsChanged(index, index);
    }

    @Override
    public void setAll(Iterable<? extends T> values) {
        final int oldSize = content.size();
        content.clear();
        content.addAll(Lists.newArrayList(values));
        if (oldSize > content.size()) {
            fireContentsChanged(0, content.size() - 1);
            fireIntervalRemoved(content.size(), oldSize - 1);
        } else if (content.size() > oldSize) {
            fireContentsChanged(0, oldSize - 1);
            fireIntervalAdded(oldSize, content.size()-1);
        } else {
            // Size matches.
            fireContentsChanged(0,content.size()-1);
        }
    }

    @Override
    public int getSize() {
        return content.size();
    }

    @Override
    public T getElementAt(int index) {
        return content.get(index);
    }

    private void fireContentsChanged(int start, int end) {
        if (start <= end) {
            // Copy array to avoid concurrent modification exceptions
            for (ListDataListener listener : listeners.toArray(new ListDataListener[listeners.size()])) {
                listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, start, end));
            }
        }
    }

    private void fireIntervalAdded(int start, int end) {
        if (start <= end) {
            // Copy array to avoid concurrent modification exceptions
            for (ListDataListener listener : listeners.toArray(new ListDataListener[listeners.size()])) {
                listener.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, start, end));
            }
        }
    }

    private void fireIntervalRemoved(int start, int end) {
        if (start <= end) {
            // Copy array to avoid concurrent modification exceptions
            for (ListDataListener listener : listeners.toArray(new ListDataListener[listeners.size()])) {
                listener.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, start, end));
            }
        }
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }

    @Override
    public Iterator<T> iterator() {
        return content.iterator();
    }
}
