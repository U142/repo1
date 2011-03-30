package org.jdesktop.beansbinding.impl;

import com.google.common.base.CaseFormat;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public interface BeanPropertyAccessor {

    ImmutableMap<Class, Object> DEFAULT_VALUES = ImmutableMap.<Class, Object>builder()
            .put(boolean.class, false)
            .put(char.class, '\u0000')
            .put(byte.class, (byte) 0)
            .put(short.class, (short) 0)
            .put(int.class, 0)
            .put(long.class, 0l)
            .put(float.class, 0.0f)
            .put(double.class, 0.0d)
            .build();

    abstract class Abstract implements BeanPropertyAccessor {

        private final BeanPropertyName name;
        private final Method getter;
        private final Method setter;

        public Abstract(BeanPropertyName name, Method getter, Method setter) {
            this.getter = getter;
            this.setter = setter;
            this.name = name;
        }

        @Override
        public Method getGetter() {
            return getter;
        }

        @Override
        public Method getSetter() {
            return setter;
        }

        @Override
        public <T> T read(Object instance, Class<T> returnValue) {
            try {
                return returnValue.cast(readImpl(getter, name, instance));
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed to read property " + name.getFullName() + " by invoking " + getter + " on " + instance, e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException("Failed to read property " + name.getFullName() + " by invoking " + getter + " on " + instance, e);
            }
        }

        @Override
        public void write(Object instance, Object value) {
            Preconditions.checkNotNull(setter, "Read only property: " + name.getFullName() + " on " + instance);
            try {
                if (getter.getReturnType().isPrimitive() && value == null) {
                    writeImpl(setter, name, instance, DEFAULT_VALUES.get(getter.getReturnType()));
                } else {
                    writeImpl(setter, name, instance, value);
                }
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed to write property " + value + " on property " + name.getFullName() + " by invoking " + setter + " on " + instance, e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException("Failed to write property " + value + " on property " + name.getFullName() + " by invoking " + setter + " on " + instance, e);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Failed to write value " + value + " on property " + name.getFullName() + " by invoking " + setter + " on " + instance, e);
            }
        }

        protected abstract Object readImpl(Method getter, BeanPropertyName name, Object instance) throws IllegalAccessException, InvocationTargetException;

        protected abstract void writeImpl(Method setter, BeanPropertyName name, Object instance, Object value) throws IllegalAccessException, InvocationTargetException;
    }

    class Factory {
        public static BeanPropertyAccessor of(final BeanPropertyName name, final Class<?> fromType) {
            final Method getter = Preconditions.checkNotNull(findGetter(fromType, name), "No getter on " + fromType + " for " + name.getName());
            final Method setter = findSetter(fromType, name);

            if (name.getIndex() > 0) {
                return new Abstract(name, getter, setter) {

                    @Override
                    protected Object readImpl(Method getter, BeanPropertyName name, Object instance) throws IllegalAccessException, InvocationTargetException {
                        return getter.invoke(instance, name.getIndex());
                    }

                    @Override
                    protected void writeImpl(Method setter, BeanPropertyName name, Object instance, Object value) throws IllegalAccessException, InvocationTargetException {
                        setter.invoke(instance, name.getIndex(), value);
                    }
                };
            } else {
                return new Abstract(name, getter, setter) {

                    @Override
                    protected Object readImpl(Method getter, BeanPropertyName name, Object instance) throws IllegalAccessException, InvocationTargetException {
                        return getter.invoke(instance);
                    }

                    @Override
                    protected void writeImpl(Method setter, BeanPropertyName name, Object instance, Object value) throws IllegalAccessException, InvocationTargetException {
                        setter.invoke(instance, value);
                    }
                };
            }
        }

        private static Method findGetter(Class<?> fromType, BeanPropertyName name) {
            Class[] args = (name.getIndex() > 0) ? new Class[]{int.class} : new Class[0];
            String namePostfix = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, name.getName());
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

        private static Method findSetter(Class<?> fromType, BeanPropertyName name) {
            int argLength = (name.getIndex() > 0) ? 2 : 1;
            String namePostfix = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, name.getName());
            for (Method method : fromType.getMethods()) {
                if (method.getName().equals("set" + namePostfix) && method.getParameterTypes().length == argLength) {
                    return method;
                }
            }
            return null;
        }
    }

    Method getGetter();

    Method getSetter();

    <T> T read(Object instance, Class<T> returnValue);

    void write(Object instance, Object value);
}
