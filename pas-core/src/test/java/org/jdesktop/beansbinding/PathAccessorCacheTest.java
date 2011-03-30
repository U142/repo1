package org.jdesktop.beansbinding;

import org.junit.Test;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class PathAccessorCacheTest {

    @Test
    public void testJTextComponentLookup() {
        PathAccessorCache cache = new PathAccessorCache();
        assertThat(cache.<JTextComponent, String>getAccessor(JTextField.class, "text"), is(CustomOveridePaths.JTEXTCOMPONENT_TEXT));
    }

    @Test
    public void testJTextComponentPropertyLookup() {
        PathAccessorCache cache = new PathAccessorCache();
        assertThat(cache.<AbstractButton, Boolean>getAccessor(JButton.class, "selected"), is(CustomOveridePaths.ABSTRACT_BUTTON_SELECTED));
    }

    class Model extends AbstractBean {
        private String name;

        public String getName() {
          return name;
        }

        public void setName(String value) {
          final String oldValue = this.name;
          this.name = value;
          update("name", oldValue, value);
        }
    }

    @Test
    public void testDocumentChange() throws BadLocationException {
        JTextField field = new JTextField();
        Model model = new Model();
        Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, model, BeanProperty.create("name"), field, BeanProperty.create("text")).bind();
        field.getDocument().insertString(0, "Hello world", null);
        assertThat(model.getName(), equalTo("Hello world"));
    }

}
