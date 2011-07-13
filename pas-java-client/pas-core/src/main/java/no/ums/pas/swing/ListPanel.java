package no.ums.pas.swing;

import com.google.common.base.Preconditions;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Label;

/**
 * Simple panel to list all components in a model using a custom renderer.
 *
 * @author Ståle Undheim <su@ums.no>
 */
public class ListPanel<T> extends JPanel {

    private static final Renderer<Object> DEFAULT_RENDERER = new Renderer<Object>() {
        @Override
        public Component render(Object value) {
            return new Label(String.valueOf(value));
        }
    };

    public interface Renderer<T> {
        Component render(T value);
    }

    private ListModel model;
    private Renderer<? super T> renderer = DEFAULT_RENDERER;

    private final ListDataListener listener = new ListDataListener() {
        @Override
        public void intervalAdded(ListDataEvent e) {
            for (int i = e.getIndex0(); i <= e.getIndex1(); i++) {
                internalAdd(i);
            }
            getParent().validate();
        }

        @Override
        public void intervalRemoved(ListDataEvent e) {
            for (int i = e.getIndex1(); i >= e.getIndex0(); i--) {
                remove(i);
            }
            getParent().validate();
        }

        @Override
        public void contentsChanged(ListDataEvent e) {
            intervalRemoved(e);
            intervalAdded(e);
        }
    };

    public ListPanel() {
        setPreferredSize(new Dimension(100, 100));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public ListModel getModel() {
        return model;
    }

    public void setModel(ListModel value) {
        final ListModel oldValue = this.model;
        if (oldValue != null) {
            oldValue.removeListDataListener(listener);
        }
        this.model = value;
        if (value != null) {
            value.addListDataListener(listener);
        }
        updateContents();
        firePropertyChange("model", oldValue, value);
    }

    private void updateContents() {
        removeAll();
        if (model != null) {
            for (int i = 0; i < model.getSize(); i++) {
                internalAdd(i);
            }
        }
    }

    private void internalAdd(int i) {
        Component comp = renderer.render((T) model.getElementAt(i));
        comp.setSize(getWidth(), comp.getMinimumSize().height);
        add(comp, i);
    }

    public Renderer<? super T> getRenderer() {
        return renderer;
    }

    public void setRenderer(Renderer<? super T> value) {
        final Renderer<? super T> oldValue = this.renderer;
        this.renderer = Preconditions.checkNotNull(value, "renderer cannot be null");
        updateContents();
        firePropertyChange("renderer", oldValue, value);
    }

    @Override
    public Dimension getPreferredSize() {
        if (getComponentCount() == 0) {
            return super.getPreferredSize();
        }
        int width = 0;
        int height = 0;
        for (int i=0; i<getComponentCount(); i++) {
            width = Math.max(width, getComponent(i).getSize().width);
            height = height + getComponent(i).getSize().height;
        }
        return new Dimension(width, height);
    }
}
