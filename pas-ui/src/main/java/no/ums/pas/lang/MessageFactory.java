package no.ums.pas.lang;

import no.ums.pas.beans.BindingFactory;
import org.jdesktop.beansbinding.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class MessageFactory {

    public static <T extends Messages> T getMessages(final Class<T> messageClass) {
        return getMessages(messageClass, Locale.ENGLISH);
    }

    public static <T extends Messages> T getMessages(final Class<T> messageClass, final Locale initalLocale) {
        return messageClass.cast(Proxy.newProxyInstance(messageClass.getClassLoader(), new Class[]{messageClass}, new MessageHandler<T>(messageClass, initalLocale)));
    }


    private static class MessageHandler<T extends Messages> implements InvocationHandler {

        private final AtomicReference<PropertyChangeSupport> propertySupport = new AtomicReference<PropertyChangeSupport>(null);
        private final Class<T> messageType;
        private Locale locale;
        private final Map<Method, String> messages = new HashMap<Method, String>();
        private final Map<String, String> properties = new HashMap<String, String>();

        public MessageHandler(final Class<T> messageType, Locale initalLocale) {
            this.messageType = messageType;
            setLocale(initalLocale);
        }

        private void setLocale(Locale locale) {
            this.locale = locale;
            Locale[] variants = new Locale[]{
                    locale,
                    new Locale(locale.getLanguage(), locale.getCountry(), locale.getVariant()),
                    new Locale(locale.getLanguage(), locale.getCountry()),
                    new Locale(locale.getLanguage())
            };
            // Load properties in least specific to most specific order.
            String[] propNames = new String[]{
                    messageType.getSimpleName() + ".properties",
                    messageType.getSimpleName() + "_" + variants[3] + ".properties",
                    messageType.getSimpleName() + "_" + variants[2] + ".properties",
                    messageType.getSimpleName() + "_" + variants[1] + ".properties",
                    messageType.getSimpleName() + "_" + variants[0] + ".properties"
            };
            final Properties props = new Properties();
            for (String propName : propNames) {
                URL resource = messageType.getResource(propName);
                if (resource != null) {
                    try {
                        InputStream stream = resource.openStream();
                        props.load(stream);
                        stream.close();
                    } catch (IOException e) {
                        throw new IllegalStateException("Failed to load properties from " + resource);
                    }
                }
            }
            for (Method method : messageType.getMethods()) {
                String name = method.getName();
                if (name.startsWith("get") && method.getDeclaringClass() != Messages.class && method.getDeclaringClass() != Object.class) {
                    final String propName = Character.toLowerCase(name.charAt(3)) + name.substring(4);
                    if (props.containsKey(propName)) {
                        update(propName, method, props.getProperty(propName));
                    } else if (method.isAnnotationPresent(Messages.Default.class)) {
                        update(propName, method, method.getAnnotation(Messages.Default.class).value());
                    } else {
                        throw new IllegalStateException("No value or default value for " + method);
                    }
                }
            }
        }

        private void update(String propName, Method method, String value) {
            final String oldValue = messages.get(method);
            messages.put(method, value);
            properties.put(propName, value);
            if (propertySupport.get() != null) {
                propertySupport.get().firePropertyChange(propName, oldValue, value);
            }
        }


        @Override
        public Object invoke(final Object proxy, Method method, final Object[] args) throws Throwable {
            if (propertySupport.get() == null) {
                propertySupport.compareAndSet(null, new PropertyChangeSupport(proxy));
            }
            if (messages.containsKey(method)) {
                return String.format(messages.get(method), args);
            } else if (method.getDeclaringClass().equals(Messages.class)) {
                if (method.getName().equals("getLocale")) {
                    return locale;
                } else if (method.getName().equals("setLocale")) {
                    setLocale((Locale) args[0]);
                    return null;
                } else if (method.getName().equals("addPropertyChangeListener")) {
                    propertySupport.get().addPropertyChangeListener((PropertyChangeListener) args[0]);
                    return null;
                } else if (method.getName().equals("removePropertyChangeListener")) {
                    propertySupport.get().removePropertyChangeListener((PropertyChangeListener) args[0]);
                    return null;
                } else if (method.getName().equals("property")) {
                    return new MessageProperty(args);
                } else if (method.getName().equals("autoBind")) {
                    final String propName = (String) args[0];
                    if (!properties.containsKey(propName)) {
                        throw new IllegalArgumentException("No such property: " + propName);
                    }
                    return new BindingFactory<Messages, String>() {
                        @Override
                        public <T, V> AutoBinding<Messages, String, T, V> to(T target, Property<T, V> prop) {
                            return Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, (Messages) proxy, new MessageProperty(args), target, prop);
                        }

                        @Override
                        public <T, V> AutoBinding<Messages, String, T, V> to(T target, String targetProp) {
                            return Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, (Messages) proxy, new MessageProperty(args), target, BeanProperty.<T, V>create(targetProp));
                        }
                    };
                }
            } else if (method.getDeclaringClass().equals(Object.class)) {
                if (method.getName().equals("equals")) {
                    return proxy == args[0];
                }
                return method.invoke(this, args);
            }
            throw new UnsupportedOperationException("Not implemented: " + method);
        }

        public class MessageProperty extends Property<Messages, String> {

            private final Map<Messages, List<PropertyStateListener>> listeners = new HashMap<Messages, List<PropertyStateListener>>();
            private final String propertyName;
            private final Object[] args;

            public MessageProperty(Object[] args) {
                this.propertyName = (String) args[0];
                this.args = new Object[args.length - 1];
                System.arraycopy(args, 1, this.args, 0, args.length - 1);
            }

            @Override
            public Class<? extends String> getWriteType(Messages source) {
                return String.class;
            }

            @Override
            public String getValue(Messages source) {
                return String.format(properties.get(propertyName), args);
            }

            @Override
            public void setValue(Messages source, String value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isReadable(Messages source) {
                return true;
            }

            @Override
            public boolean isWriteable(Messages source) {
                return false;
            }

            @Override
            public void addPropertyStateListener(final Messages source, PropertyStateListener listener) {
                if (!listeners.containsKey(source)) {
                    source.addPropertyChangeListener(new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                            if (evt.getPropertyName().equals(propertyName)) {
                                for (PropertyStateListener stateListener : listeners.get(source)) {
                                    stateListener.propertyStateChanged(new PropertyStateEvent(MessageProperty.this, source, true, evt.getOldValue(), evt.getNewValue(), false, false));
                                }
                            }
                        }
                    });
                    listeners.put(source, new ArrayList<PropertyStateListener>());
                }
                listeners.get(source).add(listener);
            }

            @Override
            public void removePropertyStateListener(Messages source, PropertyStateListener listener) {
                if (listeners.containsKey(source)) {
                    listeners.get(source).remove(listener);
                }
            }

            @Override
            public PropertyStateListener[] getPropertyStateListeners(Messages source) {
                if (listeners.containsKey(source)) {
                    List<PropertyStateListener> list = listeners.get(source);
                    return list.toArray(new PropertyStateListener[list.size()]);
                }
                return new PropertyStateListener[0];
            }
        }
    }


}
