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

    public static class TestBean extends AbstractBean{
        private String name;

        public TestBean() {
        }

        public TestBean(String name) {
            this.name = name;
        }

        public String getName() {
          return name;
        }

        public void setName(String value) {
          final String oldValue = this.name;
          this.name = value;
          update("name", oldValue, value);
        }

    }

    public static class TestNestedBean extends AbstractBean {
        private TestBean nested;

        public TestNestedBean() {
        }

        public TestNestedBean(TestBean nested) {
            this.nested = nested;
        }

        public TestBean getNested() {
          return nested;
        }

        public void setNested(TestBean value) {
          final TestBean oldValue = this.nested;
          this.nested = value;
          update("nested", oldValue, value);
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

    @Test
    public void testNestedBeanChanges() throws Exception {
        final TestNestedBean nestedBeanSrc = new TestNestedBean();
        final TestNestedBean nestedBeanDest = new TestNestedBean();

        Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, nestedBeanSrc, BeanProperty.create("nested"), nestedBeanDest, BeanProperty.create("nested")).bind();
        TestBean testBean = new TestBean();
        nestedBeanSrc.setNested(testBean);

        assertThat(nestedBeanDest.getNested(), equalTo(testBean));
    }

    @Test
    public void testNestedPropertyReadOnce() {
        final TestNestedBean nestedBeanSrc = new TestNestedBean(new TestBean("original Text"));
        final TestBean tgt = new TestBean();
        Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_ONCE, nestedBeanSrc, BeanProperty.create("nested.name"), tgt, BeanProperty.create("name")).bind();
        
        assertThat(tgt.getName(), equalTo(nestedBeanSrc.getNested().getName()));
    }

    @Test
    public void testNestedPropertyRead() {
        final TestNestedBean nestedBeanSrc = new TestNestedBean(new TestBean());
        final TestBean tgt = new TestBean();
        Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, nestedBeanSrc, BeanProperty.create("nested.name"), tgt, BeanProperty.create("name")).bind();

        nestedBeanSrc.getNested().setName("Another test");

        assertThat(tgt.getName(), equalTo(nestedBeanSrc.getNested().getName()));
    }

    @Test
    public void testNestedChanges() {
        final TestNestedBean nestedBeanSrc = new TestNestedBean(new TestBean());
        final TestBean tgt = new TestBean();
        Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, nestedBeanSrc, BeanProperty.create("nested.name"), tgt, BeanProperty.create("name")).bind();

        nestedBeanSrc.setNested(new TestBean("Another test"));

        assertThat(tgt.getName(), equalTo(nestedBeanSrc.getNested().getName()));
    }

    @Test
    public void testWriteBackToNested() {
        final TestNestedBean nestedBeanSrc = new TestNestedBean(new TestBean());
        final TestBean tgt = new TestBean();
        Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, nestedBeanSrc, BeanProperty.create("nested.name"), tgt, BeanProperty.create("name")).bind();

        tgt.setName("More testing");

        assertThat(nestedBeanSrc.getNested().getName(), equalTo(tgt.getName()));
    }

    @Test
    public void testChangesDontPropagateAfterNestedValueChanges() {
        TestBean testBean1 = new TestBean("Bean1");
        TestBean testBean2 = new TestBean("Bean2");
        final TestNestedBean nestedBeanSrc = new TestNestedBean(testBean1);
        final TestBean tgt = new TestBean();
        Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, nestedBeanSrc, BeanProperty.create("nested.name"), tgt, BeanProperty.create("name")).bind();

        nestedBeanSrc.setNested(testBean2);
        testBean1.setName("Failed");
        testBean2.setName("Updated");

        assertThat(tgt.getName(), equalTo(testBean2.getName()));
    }

}
