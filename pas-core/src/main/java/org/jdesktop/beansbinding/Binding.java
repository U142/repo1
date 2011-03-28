package org.jdesktop.beansbinding;

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
    protected SV nullValue;

    public Binding(AutoBinding.UpdateStrategy strategy, SB src, BeanProperty<SB, SV> srcProp, TB target, BeanProperty<TB, TV> targetProp) {
        strategy.assertValid(src, srcProp, target, targetProp);
        this.strategy = strategy;
        this.src = src;
        this.srcProp = srcProp;
        this.target = target;
        this.targetProp = targetProp;
    }

    public void setConverter(Converter<SV, TV> converter) {
        this.converter = converter;
    }

    public Converter<SV, TV> getConverter() {
        return converter;
    }

    public void bind() {
        strategy.bind(src, srcProp, null, target, targetProp, getConverter());
    }

    public void setSourceNullValue(SV nullValue) {
        this.nullValue = nullValue;
    }
}
