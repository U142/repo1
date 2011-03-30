package org.jdesktop.beansbinding.impl;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public final class BeanPropertyName {

    public static BeanPropertyName of(final String propertyName) {
        int dot = propertyName.lastIndexOf('.');
        final String leafName = propertyName.substring(dot + 1);
        final int at = leafName.lastIndexOf('@');
        final int index = (at == -1) ? -1 : Integer.valueOf(leafName.substring(at + 1));
        final BeanPropertyName parent = (dot == -1) ? null : of(propertyName.substring(0, dot));

        if (index > 0) {
            return new BeanPropertyName(parent, leafName.substring(0, at), index);
        } else {
            return new BeanPropertyName(parent, leafName, index);
        }
    }

    private final BeanPropertyName parent;
    private final String name;
    private final int index;

    private BeanPropertyName(BeanPropertyName parent, String name, int index) {
        this.parent = parent;
        this.name = name;
        this.index = index;
    }

    public BeanPropertyName getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return (getParent() == null) ? getName() : getParent().getFullName() + "." + getName();
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "BeanPropertyName{" +
                "parent=" + parent +
                ", name='" + name + '\'' +
                '}';
    }

}
