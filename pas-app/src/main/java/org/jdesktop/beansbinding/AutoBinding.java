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
            public <SB, SV, TB, TV> void bind(SB src, BeanProperty<SB, SV> srcProp, TB target, BeanProperty<TB, TV> targetProp, Converter<SV, TV> converter) {
                targetProp.write(target, converter.convertForward(srcProp.read(src)));
            }
        },
        READ {
            @Override
            public <SB, SV, TB, TV> void bind(final SB src, final BeanProperty<SB, SV> srcProp, final TB target, final BeanProperty<TB, TV> targetProp, final Converter<SV, TV> converter) {
                READ_ONCE.bind(src, srcProp, target, targetProp, converter);
                srcProp.addPropertyChangeListener(src, new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        targetProp.write(target, converter.convertForward(srcProp.read(src)));
                    }
                });
            }
        },
        READ_WRITE {
            @Override
            public <SB, SV, TB, TV> void bind(final SB src, final BeanProperty<SB, SV> srcProp, final TB target, final BeanProperty<TB, TV> targetProp, final Converter<SV, TV> converter) {
                READ.bind(src, srcProp, target, targetProp, converter);
                targetProp.addPropertyChangeListener(target, new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                    	srcProp.write(src, converter.convertReverse(targetProp.read(target)));
                    }
                });
            }
        };

        public abstract <SB, SV, TB, TV> void bind(SB src, BeanProperty<SB, SV> srcProp, TB target, BeanProperty<TB, TV> targetProp, Converter<SV, TV> converter);
    }
}
