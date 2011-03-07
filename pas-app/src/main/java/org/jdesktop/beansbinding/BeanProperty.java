package org.jdesktop.beansbinding;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;

import javax.annotation.Nullable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class BeanProperty<BT, PT> {
    public static <BT, PT> BeanProperty<BT, PT> create(String text) {
        return new BeanProperty<BT, PT>(text);
    }

    private static class Identity implements Comparable<Identity> {
        private final Object v;

        public Identity(Object v) {
            this.v = v;
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof Identity) && ((Identity) o).v == v;

        }

        @Override
        public int compareTo(Identity o) {
            return System.identityHashCode(v) - System.identityHashCode(o.v);
        }

        @Override
        public int hashCode() {
            return v != null ? v.hashCode() : 0;
        }
    }

    private static class NamedMethodKey {
        private final Class type;
        private final String name;

        private NamedMethodKey(Class type, String name) {
            this.type = Preconditions.checkNotNull(type, "type cannot be null");
            this.name = Preconditions.checkNotNull(name, "setter name cannot be null");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NamedMethodKey namedMethodKey = (NamedMethodKey) o;

            return name.equals(namedMethodKey.name) && type.equals(namedMethodKey.type);
        }

        @Override
        public int hashCode() {
            return 31 * type.hashCode() + name.hashCode();
        }
    }

    private static final Set<Identity> instanceMap = new ConcurrentSkipListSet<Identity>();
    private static final Map<NamedMethodKey, Method> setterMap = new MapMaker().makeComputingMap(new Function<NamedMethodKey, Method>() {
        @Override
        public Method apply(@Nullable NamedMethodKey input) {
            for (Method method : input.type.getMethods()) {
                if (method.getParameterTypes().length == 1 && input.name.equals(method.getName())) {
                    return method;
                }
            }
            throw new IllegalArgumentException("No method "+ input.name +" on "+input.type);
        }
    });

    private final String propertyName;
    private final String getterName;
    private final String setterName;

    public BeanProperty(String propertyName) {
        this.propertyName = propertyName;
        this.getterName = "get" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, propertyName);
        this.setterName = "set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, propertyName);

    }

    public String getPropertyName() {
        return propertyName;
    }

    @SuppressWarnings("unchecked")
    public PT read(BT src) {
        try {
            return (PT) src.getClass().getMethod(getterName).invoke(src);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to invoke getter for "+propertyName+" on "+src, e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Failed to invoke getter for "+propertyName+" on "+src, e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Failed to invoke getter for "+propertyName+" on "+src, e);
        }
    }

    public void write(BT target, PT value) {
        try {
            if (!instanceMap.contains(new Identity(target)) && !Objects.equal(value, read(target))) {
                setterMap.get(new NamedMethodKey(target.getClass(), setterName)).invoke(target, value);
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to invoke setter for "+propertyName+" on "+target, e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Failed to invoke setter for "+propertyName+" on "+target, e);
        }
    }

    public void addPropertyChangeListener(final BT target, final PropertyChangeListener propertyChangeListener) {
        final Identity id = new Identity(target);
        if (propertyName.equals("text") && target instanceof JTextComponent) {
            ((JTextComponent) target).getDocument().addDocumentListener(new DocumentListener() {

                private String value = ((JTextComponent) target).getText();

                @Override
                public void insertUpdate(DocumentEvent e) {
                    changedUpdate(e);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    changedUpdate(e);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    instanceMap.add(id);
                    try {
                        final String oldValue = value;
                        value = ((JTextComponent) target).getText();
                        propertyChangeListener.propertyChange(new PropertyChangeEvent(target, propertyName, oldValue, value));
                    } finally {
                        instanceMap.remove(id);
                    }
                }
            });
        } else {
            try {
                target.getClass().getMethod("addPropertyChangeListener", String.class, PropertyChangeListener.class).invoke(target, propertyName, new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        instanceMap.add(id);
                        try {
                            propertyChangeListener.propertyChange(evt);
                        } finally {
                            instanceMap.remove(id);
                        }
                    }
                });
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed to bind property "+propertyName+" on "+target, e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException("Failed to bind property "+propertyName+" on "+target, e);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("Failed to bind property "+propertyName+" on "+target, e);
            }
        }
    }
}
