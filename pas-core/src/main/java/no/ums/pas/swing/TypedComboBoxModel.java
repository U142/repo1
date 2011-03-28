package no.ums.pas.swing;

import org.jdesktop.beansbinding.Converter;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListDataListener;
import java.util.Iterator;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public abstract class TypedComboBoxModel<K, V> extends Converter<K, V> implements ComboBoxModel, TypedListModel<V> {

    private final DefaultComboBoxModel delegate = new DefaultComboBoxModel();

    public void setValues(Iterable<V> values) {
        int index = 0;
        for (V value : values) {
            if (index >= getSize()) {
                delegate.addElement(value);
            } else {
                delegate.removeElementAt(index);
                delegate.insertElementAt(value, index);
            }
            index += 1;
        }
        while (index < getSize()) {
            delegate.removeElementAt(index++);
        }
    }

    @Override
    public V convertForward(K in) {
        for (int i = 0; i < getSize(); i++) {
            if (getKey(getElementAt(i)).equals(in)) {
                return getElementAt(i);
            }
        }
        return null;
    }

    @Override
    public K convertReverse(V out) {
        return getKey(out);
    }

    protected abstract K getKey(V value);

    @Override
    public int getSize() {
        return delegate.getSize();
    }

    @Override
    public V getElementAt(int index) {
        return (V) delegate.getElementAt(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        delegate.addListDataListener(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        delegate.removeListDataListener(l);
    }

    public void setSelected(V value) {
        delegate.setSelectedItem(getKey(value));
    }

    @Override
    public void setSelectedItem(Object anItem) {
        setSelected((V) anItem);
    }

    @Override
    public Object getSelectedItem() {
        return convertForward((K) delegate.getSelectedItem());
    }

    @Override
    public Iterator<V> iterator() {
        return new Iterator<V>() {

            int index = 0;

            @Override
            public boolean hasNext() {
                return index < getSize();
            }

            @Override
            public V next() {
                return getElementAt(index++);
            }

            @Override
            public void remove() {
                delegate.removeElementAt(index--);
            }
        };
    }
}
