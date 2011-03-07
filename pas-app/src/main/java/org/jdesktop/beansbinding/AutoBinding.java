package org.jdesktop.beansbinding;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class AutoBinding {

    public static enum UpdateStrategy {
        READ_ONCE {
            @Override
            public <SB, TB, V> void bind(SB src, BeanProperty<SB, V> srcProp, TB target, BeanProperty<TB, V> targetProp) {
                targetProp.write(target, srcProp.read(src));
            }
        },
        READ {
            @Override
            public <SB, TB, V> void bind(final SB src, final BeanProperty<SB, V> srcProp, final TB target, final BeanProperty<TB, V> targetProp) {
                READ_ONCE.bind(src, srcProp, target, targetProp);
                srcProp.addPropertyChangeListener(src, new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        targetProp.write(target, srcProp.read(src));
                    }
                });
            }
        },
        READ_WRITE {
            @Override
            public <SB, TB, V> void bind(final SB src, final BeanProperty<SB, V> srcProp, final TB target, final BeanProperty<TB, V> targetProp) {
                READ.bind(src, srcProp, target, targetProp);
                targetProp.addPropertyChangeListener(target, new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        srcProp.write(src, targetProp.read(target));
                    }
                });
            }
        };

        public abstract <SB, TB, V> void bind(SB src, BeanProperty<SB, V> srcProp, TB target, BeanProperty<TB, V> targetProp);
    }
}
