package org.jdesktop.beansbinding;

import com.google.common.base.CaseFormat;
import com.google.common.base.Preconditions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public interface BeanPropertyName {

    abstract class Abstract implements BeanPropertyName {

        private final BeanPropertyName parent;
        private final String name;
        private final String namePostfix;

        protected Abstract(BeanPropertyName parent, String name) {
            this.parent = parent;
            this.name = name;
            this.namePostfix = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, name);
        }

        @Override
        public BeanPropertyName getParent() {
            return parent;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getFullName() {
            return (getParent() == null) ? getName() : getParent().getFullName() + "." + getName();
        }

        @Override
        public BeanPropertyAccessor getAccessor(Class<?> fromType) {
            final Method getter = getGetter(fromType);
            final Method setter = getSetter(fromType);
            return new BeanPropertyAccessor() {
                @Override
                public Method getGetter() {
                    return getter;
                }

                @Override
                public Method getSetter() {
                    return setter;
                }

                @Override
                public <T> T read(Object instance, final Class<T> returnValue) {
                    // Reading from null results in null
                    if (instance == null) {
                        return null;
                    }
                    if (getter == null) {
                        throw new IllegalArgumentException("No getter for " + getFullName() + " on " + instance);
                    }
                    try {
                        Object value = readImpl(instance, getter);
                        try {
                            if (value == null && returnValue.isPrimitive()) {
                                return (T) IPathAccessor.DEFAULT_VALUES.get(returnValue);
                            }
                            return (T) value;
                        } catch (ClassCastException e) {
                            throw new IllegalStateException("Failed to cast from " + getter.getReturnType() + " to " + returnValue + " on " + getFullName()+" - value: "+value, e);
                        }
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException("Failed to read property " + getFullName() + " by invoking " + getter + " on " + instance, e);
                    } catch (InvocationTargetException e) {
                        throw new IllegalStateException("Failed to read property " + getFullName() + " by invoking " + getter + " on " + instance, e);
                    }

                }

                @Override
                public void write(Object instance, Object value) {
                    instance = Preconditions.checkNotNull(instance, "instance cannot be null when writing to %s on %s", getFullName(), instance);
                    if (setter == null) {
                        throw new IllegalArgumentException("No setter for " + getFullName() + " on " + instance);
                    }
                    try {
                        if (value == null && getter.getReturnType().isPrimitive()) {
                            value = IPathAccessor.DEFAULT_VALUES.get(getter.getReturnType());
                        }
                        writeImpl(instance, setter, value);
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException("Failed to write property " + value + " on property " + getFullName() + " by invoking " + setter + " on " + instance, e);
                    } catch (InvocationTargetException e) {
                        throw new IllegalStateException("Failed to write property " + value + " on property " + getFullName() + " by invoking " + setter + " on " + instance, e);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Failed to write value " + value + " on property " + getFullName() + " by invoking " + setter + " on " + instance, e);
                    }
                }
            };
        }

        Method findGetter(Class<?> fromType, Class... args) {
            try {
                return fromType.getMethod("get" + namePostfix, args);
            } catch (NoSuchMethodException e) {
                try {
                    return fromType.getMethod("is" + namePostfix, args);
                } catch (NoSuchMethodException e1) {
                    return null;
                }
            }
        }

        Method findSetter(Class<?> fromType, int argLength) {
            for (Method method : fromType.getMethods()) {
                if (method.getName().equals("set"+namePostfix) && method.getParameterTypes().length == argLength) {
                    return method;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return "BeanPropertyName{" +
                    "parent=" + parent +
                    ", name='" + name + '\'' +
                    '}';
        }

        abstract Method getGetter(Class<?> aClass);

        abstract Object readImpl(Object instance, Method getter) throws IllegalAccessException, InvocationTargetException;

        abstract Method getSetter(Class<?> aClass);

        abstract void writeImpl(Object instance, Method setter, Object value) throws InvocationTargetException, IllegalAccessException;
    }

    class Factory {

        static BeanPropertyName of(final String propertyName) {
            int dot = propertyName.lastIndexOf('.');
            final String leafName = propertyName.substring(dot + 1);
            final int at = leafName.lastIndexOf('@');
            final int index = (at == -1) ? -1 : Integer.valueOf(leafName.substring(at + 1));
            final BeanPropertyName parent = (dot == -1) ? null : of(propertyName.substring(0, dot));

            if (index > 0) {
                return new Abstract(parent, leafName.substring(0, at)) {
                    @Override
                    public Method getGetter(Class<?> fromType) {
                        return findGetter(fromType, int.class);
                    }

                    @Override
                    public Method getSetter(Class<?> fromType) {
                        return findSetter(fromType, 2);
                    }

                    Object readImpl(Object instance, Method getter) throws IllegalAccessException, InvocationTargetException {
                        return getter.invoke(instance, index);
                    }

                    void writeImpl(Object instance, Method setter, Object value) throws InvocationTargetException, IllegalAccessException {
                        setter.invoke(instance, index, value);
                    }
                };
            } else {
                return new Abstract(parent, leafName) {

                    @Override
                    public Method getGetter(Class<?> fromType) {
                        return findGetter(fromType);
                    }

                    @Override
                    public Method getSetter(Class<?> fromType) {
                        return findSetter(fromType, 1);
                    }

                    @Override
                    Object readImpl(Object instance, Method getter) throws IllegalAccessException, InvocationTargetException {
                        return getter.invoke(instance);
                    }

                    @Override
                    void writeImpl(Object instance, Method setter, Object value) throws InvocationTargetException, IllegalAccessException {
                        setter.invoke(instance, value);
                    }
                };
            }
        }

    }

    BeanPropertyName getParent();

    String getName();

    String getFullName();

    BeanPropertyAccessor getAccessor(Class<?> fromType);

}
