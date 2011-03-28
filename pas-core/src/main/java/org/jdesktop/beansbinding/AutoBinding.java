package org.jdesktop.beansbinding;

import com.google.common.base.Preconditions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class AutoBinding {

    public static enum UpdateStrategy {
        READ_ONCE {
            @Override
            public <SB, SV, TB, TV> void bind(SB src, BeanProperty<SB, SV> srcProp, SV nullValue, TB target, BeanProperty<TB, TV> targetProp, Converter<SV, TV> converter) {
                // Fail fast - make sure the target is writeable
                Preconditions.checkNotNull(converter, "Converter cannot be null");
                assertValid(src,  srcProp, target, targetProp);
                SV read = (src == null) ? nullValue : srcProp.read(src);
                targetProp.write(target, converter.convertForward(read));
            }

            @Override
            public <SB, SV, TB, TV> void assertValid(SB src, BeanProperty<SB, SV> srcProp, TB target, BeanProperty<TB, TV> targetProp) {
                Preconditions.checkArgument(targetProp.isWriteableOn(target), "Cannot write to "+targetProp+" on "+target);
            }
        },
        READ {
            @Override
            public <SB, SV, TB, TV> void bind(final SB src, final BeanProperty<SB, SV> srcProp, final SV nullValue, final TB target, final BeanProperty<TB, TV> targetProp, final Converter<SV, TV> converter) {
                READ_ONCE.bind(src, srcProp, null, target, targetProp, converter);
                srcProp.addPropertyChangeListener(src, new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        SV read = (src == null) ? nullValue : srcProp.read(src);
                        targetProp.write(target, converter.convertForward(read));
                    }

                    @Override
                    public String toString() {
                        return "Write[" + targetProp + " on " + target.getClass() + "]";
                    }
                });
            }

            @Override
            public <SB, SV, TB, TV> void assertValid(SB src, BeanProperty<SB, SV> srcProp, TB target, BeanProperty<TB, TV> targetProp) {
                READ_ONCE.assertValid(src, srcProp, target, targetProp);
            }
        },
        READ_WRITE {
            @Override
            public <SB, SV, TB, TV> void bind(final SB src, final BeanProperty<SB, SV> srcProp, SV nullValue, final TB target, final BeanProperty<TB, TV> targetProp, final Converter<SV, TV> converter) {
                // Fail fast - make sure we can write to the src
                assertValid(src, srcProp, target, targetProp);
                READ.bind(src, srcProp, null, target, targetProp, converter);
                targetProp.addPropertyChangeListener(target, new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        srcProp.write(src, converter.convertReverse(targetProp.read(target)));
                    }

                    @Override
                    public String toString() {
                        return "Write[" + srcProp + " on " + src.getClass() + "]";
                    }
                });
            }

            @Override
            public <SB, SV, TB, TV> void assertValid(SB src, BeanProperty<SB, SV> srcProp, TB target, BeanProperty<TB, TV> targetProp) {
                READ.assertValid(src, srcProp, target, targetProp);
                Preconditions.checkArgument(srcProp.isWriteableOn(src), "Cannot write to "+srcProp+" on "+src);
            }
        };

        public abstract <SB, SV, TB, TV> void bind(SB src, BeanProperty<SB, SV> srcProp, SV nullValue, TB target, BeanProperty<TB, TV> targetProp, Converter<SV, TV> converter);
        public abstract <SB, SV, TB, TV> void assertValid(SB src, BeanProperty<SB, SV> srcProp, TB target, BeanProperty<TB, TV> targetProp);
    }
}
