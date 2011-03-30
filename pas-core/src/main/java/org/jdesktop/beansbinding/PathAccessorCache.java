package org.jdesktop.beansbinding;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;

import javax.annotation.Nonnull;
import javax.swing.JList;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    private final ConcurrentMap<PropertyTypeKey, IPathAccessor> accessorCache = new MapMaker().makeComputingMap(new Function<PropertyTypeKey, IPathAccessor>() {
        @Override
        public IPathAccessor apply(@Nonnull PropertyTypeKey input) {
            final String name = input.getName();
            @SuppressWarnings("unchecked")
            final Class<Object> type = (Class<Object>) input.getType();


            // Check if a key for a supertype exists
            List<PropertyTypeKey> supertTypeKeys = new ArrayList<PropertyTypeKey>();

            // Add all super types for the object to the list off potential keys
            Class superType = type.getSuperclass();
            while (superType != null && superType != Object.class) {
                supertTypeKeys.add(new PropertyTypeKey(superType, name));
                superType = superType.getSuperclass();
            }
            // Add all the implemented interfaces for the object to the list of
            // potential keys
            for (Class<?> superInterface : type.getInterfaces()) {
                supertTypeKeys.add(new PropertyTypeKey(superInterface, name));
            }

            // Go through the potential keys, checking if we already have a key
            // for the property and a super type of the type. If we do, we can
            // reuse that accessor.
            for (PropertyTypeKey key : supertTypeKeys) {
                if (accessorCache.containsKey(key)) {
                    return accessorCache.get(key);
                }
            }

            BeanPropertyName beanPropertyName = BeanPropertyName.Factory.of(name);
            if (beanPropertyName.getParent() == null) {
                Method getter = beanPropertyName.getAccessor(type).getGetter();
                Preconditions.checkNotNull(getter, "Could not find getter for " + beanPropertyName.getFullName() + " on " + type);
                @SuppressWarnings("unchecked")
                Class<Object> returnType = (Class<Object>) getter.getReturnType();
                return new ReflectionPathAccessor<Object, Object>(beanPropertyName, type, returnType);
            }
            else {
                // Get the parent accessor
                IPathAccessor<Object, Object> parentAccessor = getAccessor(type, beanPropertyName.getParent().getFullName());
                // Get the leaf accessor for the child property
                IPathAccessor<Object, Object> childAccessor = getAccessor(parentAccessor.getValueType(), beanPropertyName.getName());
                return new ParentPathAccessor<Object, Object, Object>(parentAccessor, childAccessor);
            }
        }

    });

    public PathAccessorCache() {
        for (IPathAccessor accessor : CustomOveridePaths.OVERRIDES) {
            accessorCache.put(new PropertyTypeKey(accessor.getTargetType(), accessor.getPropertyName().getFullName()), accessor);
        }
    }

    /**
     * Return an accessor for the property "name" on the target type.
     *
     * @param type to bind this property to.
     * @param name of the property, using the syntax described in {@link IPathAccessor}
     * @return the path accessor.
     */
    @SuppressWarnings("unchecked")
    public <T, V> IPathAccessor<T, V> getAccessor(Class<? extends T> type, String name) {
        return (IPathAccessor<T, V>) accessorCache.get(new PropertyTypeKey(type, name));
    }

    /**
     * This class represents a key pointing to a property on a class.
     * <p/>
     * This is used for lookup of a {@link org.jdesktop.beansbinding.IPathAccessor} to
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
