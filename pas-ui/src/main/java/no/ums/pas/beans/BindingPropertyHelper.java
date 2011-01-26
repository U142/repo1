package no.ums.pas.beans;

import org.jdesktop.beansbinding.*;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public abstract class BindingPropertyHelper<S, V> extends PropertyHelper<S, V> implements BindingProperty<S, V> {
    @Override
    public BindingFactory<S, V> autobind(final S src) {
        return new BindingFactory<S, V>() {
            @Override
            public <TT, TV> AutoBinding<S, V, TT, TV> to(TT target, Property<TT, TV> prop) {
                return Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, src, BindingPropertyHelper.this, target, prop);
            }

            @Override
            public <TT, TV> AutoBinding<S, V, TT, TV> to(TT target, String propname) {
                return Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, src, BindingPropertyHelper.this, target, BeanProperty.<TT, TV>create(propname));
            }
        };
    }
}
