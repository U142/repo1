package org.jdesktop.beansbinding;

import java.lang.reflect.Method;

/**
* @author Ståle Undheim <su@ums.no>
*/
public interface BeanPropertyAccessor {
    Method getGetter();

    Method getSetter();

    <T> T read(Object instance, Class<T> returnValue);

    void write(Object instance, Object value);
}
