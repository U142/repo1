package no.ums.pas.swing;

import javax.swing.Action;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.util.Arrays;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class ComponentHandleImpl implements ComponentHandle {

    private final Component component;

    public ComponentHandleImpl(Component component) {
        this.component = component;
    }

    @Override
    public void close() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                component.setVisible(false);
            }
        });
    }

    @Override
    public String getInput(String message, String defaultValue) {
        return JOptionPane.showInputDialog(component, message, defaultValue);
    }

    @Override
    public Color getColor(String message, Color current) {
        return JColorChooser.showDialog(component, message, current);
    }

    @Override
    public ConfirmResponse getConfirmation(String message, String title, ConfirmOptionType type) {
        final int optionType = type.getOptionType();
        final int result = JOptionPane.showConfirmDialog(component, message, title, optionType);
        return ConfirmResponse.valueOf(result, type != ConfirmOptionType.OK_CANCEL);
    }

    @Override
    public void showPopup(String title, Action... actions) {
        showPopup(title, Arrays.asList(actions));
    }

    @Override
    public void showPopup(String title, Iterable<Action> actions) {
        final JPopupMenu popupMenu = new JPopupMenu(title);
        for (Action action : actions) {
            if (action == null) {
                popupMenu.addSeparator();
            } else {
                popupMenu.add(action);
            }
        }
        popupMenu.setInvoker(component);
        popupMenu.show(component, 0, component.getHeight());

    }
}
