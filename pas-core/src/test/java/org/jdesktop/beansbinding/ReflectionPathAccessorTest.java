package org.jdesktop.beansbinding;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class ReflectionPathAccessorTest {

    public static class SampleClass extends AbstractBean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String value) {
            final String oldValue = this.name;
            this.name = value;
            update("name", oldValue, value);
        }

        public String getReadOnly() {
            return "ReadOnly";
        }
    }

    private final ReflectionPathAccessor<SampleClass, String> accessor =
            new ReflectionPathAccessor<SampleClass, String>(BeanPropertyName.Factory.of("name"), SampleClass.class, String.class);
    private final ReflectionPathAccessor<SampleClass, String> readOnlyAccessor =
            new ReflectionPathAccessor<SampleClass, String>(BeanPropertyName.Factory.of("readOnly"), SampleClass.class, String.class);


    @Test
    public void testPropertyName() {
        assertThat(accessor.getPropertyName(), equalTo("name"));
    }


    @Test
    public void testWriteable() {
        assertThat(accessor.isWriteable(), equalTo(true));
    }

    @Test
    public void testReadOnly() {
        assertThat(readOnlyAccessor.isWriteable(), equalTo(false));
    }

    @Test
    public void testTargetClass() {
        assertThat(accessor.getTargetType(), equalTo(SampleClass.class));
    }

    @Test
    public void testValueClass() {
        assertThat(accessor.getValueType(), equalTo(String.class));
    }

    @Test
    public void testRead() {
        SampleClass sample = new SampleClass();
        sample.setName("test");
        assertThat(accessor.getValue(sample), equalTo("test"));
    }

    @Test
    public void testWrite() {
        SampleClass sample = new SampleClass();
        accessor.setValue(sample, "test");
        assertThat(sample.getName(), equalTo("test"));
    }

    @Test
    public void testPropertyListener() {
        Mockery context = new Mockery();
        final SampleClass sample = new SampleClass();
        final PropertyChangeListener listener = context.mock(PropertyChangeListener.class);

        context.checking(new Expectations() {{
            one(listener).propertyChange(with(propertyChangeEvent("name", null, "test")));
        }});

        accessor.addPropertyChangeListenerImpl(sample, listener);

        sample.setName("test");

        context.assertIsSatisfied();
    }

    private Matcher<PropertyChangeEvent> propertyChangeEvent(final String name, final Object oldValue, final Object newValue) {
        final Matcher<String> nameMatcher = equalTo(name);
        final Matcher<Object> oldValueMatcher = equalTo(oldValue);
        final Matcher<Object> newValueMatcher = equalTo(newValue);
        return new TypeSafeMatcher<PropertyChangeEvent>() {
            @Override
            public boolean matchesSafely(PropertyChangeEvent item) {
                return nameMatcher.matches(item.getPropertyName()) && oldValueMatcher.matches(oldValue) && newValueMatcher.matches(newValue);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("PropertyChangeEvent with name [");
                nameMatcher.describeTo(description);
                description.appendText("] and old Value [");
                oldValueMatcher.describeTo(description);
                description.appendText("] and new Value [");
                newValueMatcher.describeTo(description);
                description.appendText("]");
            }
        };
    }

}
