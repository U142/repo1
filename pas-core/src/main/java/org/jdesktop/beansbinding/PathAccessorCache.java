package org.jdesktop.beansbinding;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentMap;

/**
 * Cache of PathAccessors.
 * <p/>
 * Use this to get PathAccessor instances. This class uses a {@link java.util.concurrent.ConcurrentMap}
 * and is threadsafe.
 *
 * @author Ståle Undheim <su@ums.no>
 */
public class PathAccessorCache {

    private final ConcurrentMap<PropertyTypeKey, PathAccessor> accessorCache = new MapMaker().makeComputingMap(new Function<PropertyTypeKey, PathAccessor>() {
        @Override
        public PathAccessor apply(@Nonnull PropertyTypeKey input) {
            final String name = input.getName();
            final Class type = input.getType();

            // Split at the last . to get the parent property
            int dotSplit = name.lastIndexOf('.');
            // At symbol, indicating that this path has an index.
            int atIndex = name.lastIndexOf('@');
            // Index where the name ends, either end of the string, or at the last @
            int propertyNameEnd = (atIndex == -1) ? name.length() : atIndex;
            // If we have a . in our name, get the parent PathAccessor.
            PathAccessor parent = (dotSplit == -1) ? null : accessorCache.get(new PropertyTypeKey(type, name.substring(0, dotSplit)));
            // Get the actual property name, with out dots and @ symbols
            String propertyName = (dotSplit == -1) ? name.substring(0, propertyNameEnd) : name.substring(dotSplit + 1, propertyNameEnd);
            // Change the property name to all upper camel, as a postfix to get and set methods.
            String propertyPostfix = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, propertyName);

            // The target type of object for this path accessor. If we have a parent, it will be
            // the value type of the parent, otherwise it's the actual target as this is a root path accessor.
            Class targetType = (parent == null) ? type : parent.getValueType();

            Method getter;

            // If the property name contains an at, it indicates an int indexed variable
            Class[] getterArgs = (atIndex != -1) ? new Class[]{int.class} : new Class[0];
            try {
                // Look for a method starting with get followed by the propertyPostfix
                getter = targetType.getMethod("get" + propertyPostfix, getterArgs);
            } catch (NoSuchMethodException e) {
                try {
                    // Look for a method starting with is for boolean properties,
                    // followed by the propertyPostfix
                    getter = targetType.getMethod("is" + propertyPostfix, getterArgs);
                } catch (NoSuchMethodException e1) {
                    throw new IllegalArgumentException("No reader for " + propertyPostfix + " on " + targetType, e);
                }
            }
            for (Method method : targetType.getMethods()) {
                // Go through the methods of the targetType, looking for the setter.
                if (method.getName().endsWith(propertyPostfix) && method.getParameterTypes().length == getterArgs.length + 1) {
                    // return a read/write path accessor
                    return newPathAccessor(name, parent, method, getter, propertyName, atIndex);
                }
            }
            // No write method, return a read-only accessor.
            return newPathAccessor(name, parent, null, getter, name, atIndex);
        }

        private PathAccessor newPathAccessor(String name, PathAccessor parent, Method setter, Method getter, String propertyName, int atIndex) {
            if (atIndex == -1) {
                return new PathAccessor(parent, setter, getter, propertyName);
            } else {
                return new PathAccessor.Indexed(parent, setter, getter, name, Integer.parseInt(name.substring(atIndex + 1)));
            }
        }

    });

    /**
     * Return an accessor for the property "name" on the target type.
     *
     * @param type to bind this property to.
     * @param name of the property, using the syntax described in {@link PathAccessor}
     * @return the path accessor.
     */
    public PathAccessor getAccessor(Class type, String name) {
        return accessorCache.get(new PropertyTypeKey(type, name));
    }

    /**
     * This class represents a key pointing to a property on a class.
     * <p/>
     * This is used for lookup of a {@link org.jdesktop.beansbinding.PathAccessor} to
     * read and write the properties on a single object.
     * <p/>
     * It correctly implements equals and hashCode, and is immutable, so it can be safely used as a
     * map key.
     *
     * @author Ståle Undheim <su@ums.no>
     */
    private static class PropertyTypeKey {
        private final Class type;
        private final String name;

        PropertyTypeKey(Class type, String name) {
            this.type = Preconditions.checkNotNull(type, "type cannot be null");
            this.name = Preconditions.checkNotNull(name, "setter name cannot be null");
        }

        public Class getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PropertyTypeKey propertyTypeKey = (PropertyTypeKey) o;

            return name.equals(propertyTypeKey.name) && type.equals(propertyTypeKey.type);
        }

        @Override
        public int hashCode() {
            return 31 * type.hashCode() + name.hashCode();
        }
    }
}
