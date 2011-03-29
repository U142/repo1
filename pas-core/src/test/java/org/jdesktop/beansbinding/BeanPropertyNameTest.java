package org.jdesktop.beansbinding;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class BeanPropertyNameTest {

    @Test
    public void testSimpleProperty() throws Exception {
        BeanPropertyName simple = BeanPropertyName.Factory.of("simple");
        assertThat(simple.getName(), equalTo("simple"));
        assertThat(simple.getParent(), nullValue());
    }

    @Test
    public void testParentProperty() throws Exception {
        BeanPropertyName child = BeanPropertyName.Factory.of("parent.child");
        assertThat(child.getName(), equalTo("child"));
        assertThat(child.getParent(), not(nullValue()));

        assertThat(child.getParent().getName(), equalTo("parent"));
        assertThat(child.getParent().getParent(), nullValue());
    }

    @Test
    public void testIndexedProperty() throws Exception {
        BeanPropertyName indexed = BeanPropertyName.Factory.of("indexed@5");
        assertThat(indexed.getName(), equalTo("indexed"));
        assertThat(indexed.getParent(), nullValue());
    }

    @Test
    public void testIndexedChildProperty() throws Exception {
        BeanPropertyName indexedChild = BeanPropertyName.Factory.of("parent.indexedChild@3");
        assertThat(indexedChild.getName(), equalTo("indexedChild"));
        assertThat(indexedChild.getParent(), not(nullValue()));

        assertThat(indexedChild.getParent().getName(), equalTo("parent"));
        assertThat(indexedChild.getParent().getParent(), nullValue());
    }
    
    @Test
    public void testIndexedParentProperty() throws Exception {
        BeanPropertyName child = BeanPropertyName.Factory.of("parent@4.child");
        assertThat(child.getName(), equalTo("child"));
        assertThat(child.getParent(), not(nullValue()));

        assertThat(child.getParent().getName(), equalTo("parent"));
        assertThat(child.getParent().getParent(), nullValue());
    }

    public static class SampleClass {
        private String name;
        private String[] indexedName = new String[10];

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIndexedName(int index) {
            return indexedName[index];
        }

        public void setIndexedName(int index, String value) {
            indexedName[index] = value;
        }
    }

    @Test
    public void testGetterMethod() throws NoSuchMethodException {
        Method method = BeanPropertyName.Factory.of("name").getAccessor(SampleClass.class).getGetter();
        assertThat(method, equalTo(SampleClass.class.getMethod("getName")));
    }

    @Test
    public void testSetterMethod() throws NoSuchMethodException {
        Method method = BeanPropertyName.Factory.of("name").getAccessor(SampleClass.class).getSetter();
        assertThat(method, equalTo(SampleClass.class.getMethod("setName", String.class)));
    }

    @Test
    public void testIndexedGetterMethod() throws NoSuchMethodException {
        Method method = BeanPropertyName.Factory.of("indexedName@1").getAccessor(SampleClass.class).getGetter();
        assertThat(method, equalTo(SampleClass.class.getMethod("getIndexedName", int.class)));
    }

    @Test
    public void testIndexedSetterMethod() throws NoSuchMethodException {
        Method method = BeanPropertyName.Factory.of("indexedName@1").getAccessor(SampleClass.class).getSetter();
        assertThat(method, equalTo(SampleClass.class.getMethod("setIndexedName", int.class, String.class)));
    }

    @Test
    public void testRead() {
        SampleClass instance = new SampleClass();
        instance.setName("test1");
        String name = BeanPropertyName.Factory.of("name").getAccessor(SampleClass.class).read(instance, String.class);
        assertThat(name, equalTo("test1"));
    }

    @Test
    public void testWrite() {
        SampleClass instance = new SampleClass();
        BeanPropertyName.Factory.of("name").getAccessor(SampleClass.class).write(instance, "test1");
        assertThat(instance.getName(), equalTo("test1"));
    }

    @Test
    public void testReadIndexed() {
        SampleClass instance = new SampleClass();
        instance.setIndexedName(4, "test1");
        String name = BeanPropertyName.Factory.of("indexedName@4").getAccessor(SampleClass.class).read(instance, String.class);
        assertThat(name, equalTo("test1"));
    }

    @Test
    public void testIndexedWrite() {
        SampleClass instance = new SampleClass();
        BeanPropertyName.Factory.of("indexedName@4").getAccessor(SampleClass.class).write(instance, "test1");
        assertThat(instance.getIndexedName(4), equalTo("test1"));
    }

}
