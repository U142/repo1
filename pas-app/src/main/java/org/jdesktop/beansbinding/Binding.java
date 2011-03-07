package org.jdesktop.beansbinding;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class Binding<SB, TB, V> {

    protected final AutoBinding.UpdateStrategy strategy;
    protected final SB src;
    protected final BeanProperty<SB, V> srcProp;
    protected final TB target;
    protected final BeanProperty<TB, V> targetProp;
    protected Converter<SB, TB> converter;
    private PropertyChangeSupport changeSupport;
    
    private final TB convertForward(SB value) {
    	if (converter == null) {
    		Class<?> targetType = noPrimitiveType(targetProp.getWriteType(
    				target));

    		return (TB) targetType.cast(Converter.defaultConvert(value, targetType));
    	}
    	return converter.convertForward(value);
    }
    
    private final SB convertReverse(TB value) {
    	if (converter == null) {
			Class<?> sourceType = noPrimitiveType(srcProp.getWriteType(
						src));

    		return (SB) sourceType.cast(Converter.defaultConvert(value,
    			sourceType));
    	}

    	return converter.convertReverse(value);
    }
    
    private final Class<?> noPrimitiveType(Class<?> klass) {
    		if (!klass.isPrimitive()) {
    			return klass;
    		}

    		if (klass == Byte.TYPE) {
    			return Byte.class;
    		} else if (klass == Short.TYPE) {
    			return Short.class;
    		} else if (klass == Integer.TYPE) {
    			return Integer.class;
    		} else if (klass == Long.TYPE) {
    			return Long.class;
    		} else if (klass == Boolean.TYPE) {
    			return Boolean.class;
    		} else if (klass == Character.TYPE) {
    			return Character.class;
    		} else if (klass == Float.TYPE) {
    			return Float.class;
    		} else if (klass == Double.TYPE) {
    		return Double.class;
    	}

		throw new AssertionError();
	}
    
    public Binding(AutoBinding.UpdateStrategy strategy, SB src, BeanProperty<SB, V> srcProp, TB target, BeanProperty<TB, V> targetProp) {
        this.strategy = strategy;
        this.src = src;
        this.srcProp = srcProp;
        this.target = target;
        this.targetProp = targetProp;
    }
    
    public void setConverter(Converter<SB, TB> converter)
    {
    	//changeSupport = new PropertyChangeSupport(this);
    	Converter<SB, TB> old = this.converter;
    	this.converter = converter;
    	firePropertyChange("converter", old, converter);
    }
    public Converter<SB,TB> getConverter() { 
    	return converter;
    }

    
    protected final void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    	if(changeSupport != null) {
    		changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    	}
    }
    
    public final void addPropertyChangeListener(PropertyChangeListener listener) {
    	if(changeSupport == null) {
    		changeSupport = new PropertyChangeSupport(this);
    	}
    	changeSupport.addPropertyChangeListener(listener);
    }

    public void bind() {
        strategy.bind(src, srcProp, target, targetProp, converter);
    }
}
