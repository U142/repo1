package org.jdesktop.beansbinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class BindingGroup {

    private final List<Binding> bindings = new ArrayList<Binding>();

    public void addBinding(Binding<Object, Object, Object> binding) {
        bindings.add(binding);
    }

    public void bind() {
        for (Binding binding : bindings) {
            binding.bind();
        }
        bindings.clear();
    }
}
