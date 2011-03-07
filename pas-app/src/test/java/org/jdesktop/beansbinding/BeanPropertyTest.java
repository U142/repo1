package org.jdesktop.beansbinding;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import static com.google.common.base.CharMatcher.is;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class BeanPropertyTest {

    public static class TestBean {
        private String name;
        private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

        public String getName() {
            return name;
        }

        public void setName(String name) {
            String oldValue = this.name;
            this.name = name;
            changeSupport.firePropertyChange("name", oldValue, name);
        }

        public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            changeSupport.addPropertyChangeListener(propertyName, listener);
        }
    }

    private final BeanProperty<TestBean, String> property = BeanProperty.create("name");

    @Test
    public void testGetPropertyName() throws Exception {
        assertThat(BeanProperty.create("test").getPropertyName(), equalTo("test"));
    }


    @Test
    public void testRead() throws Exception {
        TestBean bean = new TestBean();
        String value = "This is a test";
        bean.setName(value);
        assertThat(property.read(bean), equalTo(value));
    }

    @Test
    public void testWrite() throws Exception {
        TestBean bean = new TestBean();
        String value = "This is a test";
        property.write(bean, value);
        assertThat(bean.getName(), equalTo(value));
    }

    @Test
    public void testWriteNullValue() throws Exception {
        TestBean bean = new TestBean();
        property.write(bean, null);
        assertThat(bean.getName(), nullValue());
    }

    @Test
    public void testAddPropertyChangeListener() throws Exception {
        final Mockery context = new Mockery();
        final PropertyChangeListener changeListener = context.mock(PropertyChangeListener.class);
        final TestBean bean = new TestBean();
        final String value = "This is a test";

        context.checking(new Expectations() {{
            one(changeListener).propertyChange(with(Matchers.<PropertyChangeEvent>anything()));
        }});

        property.addPropertyChangeListener(bean, changeListener);
        property.write(bean, value);

        context.assertIsSatisfied();
    }
}
