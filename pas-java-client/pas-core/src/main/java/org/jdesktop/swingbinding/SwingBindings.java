package org.jdesktop.swingbinding;

import com.google.common.collect.Lists;
import no.ums.pas.swing.ImmutableTypedListModel;
import no.ums.pas.swing.TypedListModel;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Converter;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListModel;
import java.util.Collections;
import java.util.List;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class SwingBindings {

    private static final BeanProperty<JComboBox,TypedListModel> JCOMBOBOX_MODEL = BeanProperty.create("model");
    private static final BeanProperty<JList,TypedListModel> JLIST_MODEL = BeanProperty.create("model");

    public static <SB> Binding<SB, ?, JList, TypedListModel> createJListBinding(AutoBinding.UpdateStrategy strategy, final SB model, final BeanProperty<SB, Object> beanProperty, JList list) {
        return createModelBinding(strategy, model, beanProperty, list, JLIST_MODEL);
    }

    public static <SB> Binding<SB, ?, JComboBox, TypedListModel> createJComboBoxBinding(AutoBinding.UpdateStrategy strategy, SB model, BeanProperty<SB, Object> beanProperty, JComboBox comboBox) {
        return createModelBinding(strategy, model, beanProperty, comboBox, JCOMBOBOX_MODEL);
    }

    private static <SB, TB> Binding<SB, ?, TB, TypedListModel> createModelBinding(AutoBinding.UpdateStrategy strategy, SB model, BeanProperty<SB, Object> beanProperty, TB target, BeanProperty<TB, TypedListModel> targetBean) {
        if (List.class.isAssignableFrom(beanProperty.getType(model))) {
            return Binding
                    .create(strategy, model, beanProperty.<List>castValue(), target, targetBean)
                    .setConverter(new TypedListModelConverter());
        }
        else if (ListModel.class.isAssignableFrom(beanProperty.getType(model))) {
            return Binding.create(strategy, model, beanProperty.<ListModel>castValue(), target, targetBean);
        }
        else {
            throw new IllegalArgumentException("Property "+beanProperty+" on "+model.getClass().getSimpleName()+" does not return a list or listmodel");
        }
    }

    private static class TypedListModelConverter extends Converter<List, TypedListModel> {

        TypedListModel old;

        @Override
        @SuppressWarnings("unchecked")
        public TypedListModel convertForward(List in) {
            final TypedListModel newList = (in == null) ? ImmutableTypedListModel.empty() : new ImmutableTypedListModel(in);
            if (old != null && in != null && in.contains(old.getSelectedItem())) {
                newList.setSelectedItem(old.getSelectedItem());
            }
            old = newList;
            return newList;
        }

        @Override
        @SuppressWarnings("unchecked")
        public List convertReverse(TypedListModel out) {
            return Lists.newArrayList(out);
        }
    }
}
