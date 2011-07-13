package no.ums.pas.swing;

import javax.swing.Action;
import javax.swing.JOptionPane;
import java.awt.Color;

/**
 * This is an interface that components can send to their controller.
 *
 * The intent off this interface is to factor out swing operations to an interface,
 * so this interface can easily be mocked for unit tests. 
 *
 * @author Ståle Undheim <su@ums.no>
 */
public interface ComponentHandle {

    enum ConfirmOptionType {
        YES_NO(JOptionPane.YES_NO_OPTION), YES_NO_CANCEL(JOptionPane.YES_NO_CANCEL_OPTION), OK_CANCEL(JOptionPane.OK_CANCEL_OPTION);

        private final int joptionPaneOptionType;

        ConfirmOptionType(int joptionPaneOptionType) {
            this.joptionPaneOptionType = joptionPaneOptionType;
        }

        public int getOptionType() {
            return joptionPaneOptionType;
        }
    }

    enum ConfirmResponse {
        YES, NO, OK, CANCEL;

        public static ConfirmResponse valueOf(int joptionPaneResponse, boolean yesNo) {
            switch (joptionPaneResponse) {
                case JOptionPane.OK_OPTION:
                    return (yesNo) ? YES : OK;
                case JOptionPane.CANCEL_OPTION:
                    return CANCEL;
                case JOptionPane.NO_OPTION:
                    return NO;
                default:
                    throw new IllegalArgumentException("Unrecognized response: " + joptionPaneResponse);
            }
        }
    }

    void close();

    void showPopup(String title, Action ... actions);
    void showPopup(String title, Iterable<Action> actions);

    String getInput(String message, String defaultValue);
    Color getColor(String message, Color current);

    ConfirmResponse getConfirmation(String message, String title, ConfirmOptionType type);

}
