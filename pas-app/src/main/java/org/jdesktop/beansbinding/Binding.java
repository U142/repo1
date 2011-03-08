package org.jdesktop.beansbinding;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class Binding<SB, SV, TB, TV> {

    protected final AutoBinding.UpdateStrategy strategy;
    protected final SB src;
    protected final BeanProperty<SB, SV> srcProp;
    protected final TB target;
    protected final BeanProperty<TB, TV> targetProp;
    protected Converter<SV, TV> converter = Converter.noop();

    public Binding(AutoBinding.UpdateStrategy strategy, SB src, BeanProperty<SB, SV> srcProp, TB target, BeanProperty<TB, TV> targetProp) {
        this.strategy = strategy;
        this.src = src;
        this.srcProp = srcProp;
        this.target = target;
        this.targetProp = targetProp;
    }
    
    public void setConverter(Converter<SV, TV> converter) {
    	this.converter = converter;
    }

    public Converter<SV,TV> getConverter() {
    	return converter;
    }

    public void bind() {
        strategy.bind(src, srcProp, target, targetProp, getConverter());
    }
}
