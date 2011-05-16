package org.jdesktop.beansbinding;

import no.ums.log.Log;
import no.ums.log.UmsLog;
/**
 * @author Ståle Undheim <su@ums.no>
 */
public class Binding<SB, SV, TB, TV> {

    private static final Log log = UmsLog.getLogger(Binding.class);

    protected final AutoBinding.UpdateStrategy strategy;
    protected final SB src;
    protected final BeanProperty<SB, SV> srcProp;
    protected final TB target;
    protected final BeanProperty<TB, TV> targetProp;
    protected Converter<SV, TV> converter = Converter.noop();
    protected SV nullValue;

    public static <SB, SV, TB, TV> Binding<SB, SV, TB, TV> create(AutoBinding.UpdateStrategy strategy, SB src, BeanProperty<SB, SV> srcProp, TB target, BeanProperty<TB, TV> targetProp) {
        return new Binding<SB, SV, TB, TV>(strategy, src, srcProp, target, targetProp);
    }

    public Binding(AutoBinding.UpdateStrategy strategy, SB src, BeanProperty<SB, SV> srcProp, TB target, BeanProperty<TB, TV> targetProp) {
        strategy.assertValid(src, srcProp, target, targetProp);
        this.strategy = strategy;
        this.src = src;
        this.srcProp = srcProp;
        this.target = target;
        this.targetProp = targetProp;
    }

    private String safeTypeName(Object value) {
        return (value == null) ? "Null" : value.getClass().getSimpleName();
    }

    public Binding<SB, SV, TB, TV> setConverter(final Converter<SV, TV> converter) {
        this.converter = new Converter<SV, TV>() {
            @Override
            public TV convertForward(SV in) {
                try {
                    return converter.convertForward(in);
                } catch (RuntimeException e) {
                    log.warn("Error converting value %s using %s from %s.%s -> %s.%s.", in, converter.getClass().getSimpleName(), safeTypeName(src), srcProp, safeTypeName(target), targetProp, e);
                    throw e;
                }
            }

            @Override
            public SV convertReverse(TV out) {
                try {
                    return converter.convertReverse(out);
                } catch (RuntimeException e) {
                    log.warn("Error converting value %s using %s from %s.%s -> %s.%s.", out, converter.getClass().getSimpleName(), safeTypeName(target), targetProp, safeTypeName(src), srcProp, e);
                    throw e;
                }
            }
        };
        return this;
    }

    public Converter<SV, TV> getConverter() {
        return converter;
    }

    public void bind() {
        strategy.bind(src, srcProp, null, target, targetProp, getConverter());
    }

    public Binding<SB, SV, TB, TV> setSourceNullValue(SV nullValue) {
        this.nullValue = nullValue;
        return this;
    }

    @Override
    public String toString() {
        return String.format("Binding([%s].[%s] - [%s].[%s] using %s", src.getClass().getSimpleName(), srcProp.getPropertyName(), target.getClass().getSimpleName(), targetProp.getPropertyName(), strategy);
    }
}
