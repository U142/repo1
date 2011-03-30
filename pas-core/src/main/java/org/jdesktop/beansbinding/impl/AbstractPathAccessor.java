package org.jdesktop.beansbinding.impl;

import org.jdesktop.beansbinding.PathAccessor;

/**
* @author Ståle Undheim <su@ums.no>
*/
public abstract class AbstractPathAccessor<T, V> implements PathAccessor<T, V> {

    private final Class<T> type;
    private final Class<V> valueType;
    private final BeanPropertyName propertyName;

    public AbstractPathAccessor(String name, Class<T> type, Class<V> valueType) {
        this(BeanPropertyName.of(name), type, valueType);
    }

    public AbstractPathAccessor(BeanPropertyName propertyName, Class<T> type, Class<V> valueType) {
        this.propertyName = propertyName;
        this.type = type;
        this.valueType = valueType;
    }

    @Override
    public Class<T> getTargetType() {
        return type;
    }

    @Override
    public Class<V> getValueType() {
        return valueType;
    }

    @Override
    public BeanPropertyName getPropertyName() {
        return propertyName;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"{"+valueType.getSimpleName()+" "+ getPropertyName().getFullName()+" on "+type.getSimpleName()+"}";
    }
}
