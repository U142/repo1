package org.jdesktop.beansbinding.impl;

import org.jdesktop.beansbinding.PathAccessor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Ståle Undheim <su@ums.no>
 */
abstract class AbstractCustomListenerPath<SRC, LST, VAL> extends AbstractPathAccessor<SRC, VAL> {

    interface UpdateHandle {
        void update();
    }

    AbstractCustomListenerPath(String name, Class<SRC> type, Class<VAL> valueType) {
        super(name, type, valueType);
    }

    @Override
    public ListenerHandle<SRC> addPropertyChangeListener(SRC instance, PropertyChangeListener listener) {
        return new AbstractListenerHandle<SRC, LST, VAL>(this, listener) {
            @Override
            protected LST createListener() {
                return AbstractCustomListenerPath.this.createListener(this);
            }

            @Override
            protected void removeListener(SRC current, LST listener) {
                AbstractCustomListenerPath.this.removeListener(current, listener);
            }

            @Override
            protected void addListener(SRC current, LST listener) {
                AbstractCustomListenerPath.this.addListener(current, listener);
            }
        }.changeListenTarget(instance);
    }

    protected abstract LST createListener(UpdateHandle updateHandle);

    protected abstract void addListener(SRC current, LST listener);

    protected abstract void removeListener(SRC current, LST listener);

    static abstract class AbstractListenerHandle<SRC, LST, VAL> implements ListenerHandle<SRC>, UpdateHandle {

        private SRC current;
        private final LST listener;
        private VAL currentValue;
        private final PropertyChangeListener propertyChangeListener;
        private final PathAccessor<SRC, VAL> accessor;

        protected AbstractListenerHandle(PathAccessor<SRC, VAL> accessor, PropertyChangeListener propertyChangeListener) {
            this.accessor = accessor;
            this.propertyChangeListener = propertyChangeListener;
            listener = createListener();
        }

        protected abstract LST createListener();

        protected abstract void addListener(SRC current, LST listener);

        protected abstract void removeListener(SRC current, LST listener);

        @Override
        public ListenerHandle<SRC> changeListenTarget(SRC value) {
            if (current != null) {
                removeListener(current, listener);
            }
            current = value;
            currentValue = (current == null) ? null : accessor.getValue(current);
            if (current != null) {
                addListener(current, listener);
            }
            return this;
        }

        @Override
        public void update() {
            VAL oldValue = currentValue;
            currentValue = accessor.getValue(current);
            THREAD_STACK_VISITED.get().put(current, null);
            try {
                propertyChangeListener.propertyChange(new PropertyChangeEvent(current, accessor.getPropertyName().getName(), oldValue, currentValue));
            } finally {
                THREAD_STACK_VISITED.get().remove(current);
            }
        }

    }
}
