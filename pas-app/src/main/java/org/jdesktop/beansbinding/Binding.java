package org.jdesktop.beansbinding;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class Binding<SB, TB, V> {

    protected final AutoBinding.UpdateStrategy strategy;
    protected final SB src;
    protected final BeanProperty<SB, V> srcProp;
    protected final TB target;
    protected final BeanProperty<TB, V> targetProp;

    public Binding(AutoBinding.UpdateStrategy strategy, SB src, BeanProperty<SB, V> srcProp, TB target, BeanProperty<TB, V> targetProp) {
        this.strategy = strategy;
        this.src = src;
        this.srcProp = srcProp;
        this.target = target;
        this.targetProp = targetProp;
    }

    public void bind() {
        strategy.bind(src, srcProp, target, targetProp);
    }
}
