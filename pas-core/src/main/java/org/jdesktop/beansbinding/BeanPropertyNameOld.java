package org.jdesktop.beansbinding;

import com.google.common.base.CaseFormat;
import com.google.common.base.Preconditions;

import java.lang.reflect.Method;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class BeanPropertyNameOld {

    protected static final Class[] INDEXED_ARGS = new Class[] {int.class};
    protected static final Class[] NON_INDEXED_ARGS = new Class[0];

    public static BeanPropertyNameOld valueOf(String propertyName) {
        int dot = propertyName.lastIndexOf('.');
        String leafName = propertyName.substring(dot+1);
        int at = leafName.lastIndexOf('@');
        int index = (at == -1) ? -1 : Integer.valueOf(leafName.substring(at + 1));
        BeanPropertyNameOld parent = (dot == -1) ? null : valueOf(propertyName.substring(0, dot));

        return new BeanPropertyNameOld(parent, (at == -1) ? leafName : leafName.substring(0, at), index);
    }

    private final BeanPropertyNameOld parent;
    private final String name;
    private final String namePostfix;
    private final int index;
    private final Class[] getterArgs;

    private BeanPropertyNameOld(BeanPropertyNameOld parent, String name, int index) {
        this.parent = parent;
        this.name = Preconditions.checkNotNull(name, "name cannot be null");
        this.index = index;
        namePostfix = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, getName());
        getterArgs =  (isIndexed()) ? INDEXED_ARGS : NON_INDEXED_ARGS;
    }

    public Method getGetter(Class<?> type) {
        try {
            return type.getMethod("get"+ namePostfix, getterArgs);
        } catch (NoSuchMethodException e) {
            try {
                return type.getMethod("is"+ namePostfix, getterArgs);
            } catch (NoSuchMethodException e1) {
                return null;
            }
        }
    }

    public Method getSetter(Class<?> type) {
        for (Method method : type.getMethods()) {
            if (method.getName().endsWith(namePostfix) && method.getParameterTypes().length == getterArgs.length+1) {
                return method;
            }
        }
        return null;
    }

    
    public BeanPropertyNameOld getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public boolean isIndexed() {
        return index >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeanPropertyNameOld that = (BeanPropertyNameOld) o;

        return (parent == null) ? that.parent == null : parent.equals(that.parent) && index == that.index && name.equals(that.name);

    }

    @Override
    public int hashCode() {
        int result = parent != null ? parent.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + index;
        return result;
    }

    public String getFullName() {
        return (parent == null) ? name : parent.toString()+"."+name;
    }
}
