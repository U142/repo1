/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package no.ums.log.swing;

import com.google.common.primitives.Ints;
import java.awt.Color;
import java.awt.Component;
import java.util.Comparator;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

/**
 *
 * @author staaleu
 */
public class LogSwingUtil {
    public static ListCellRenderer LOG_RECORD_RENDERER = new ListCellRenderer() {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            return new LogRecordLine((LogRecord) value, isSelected);
        }
    };

    public static ComboBoxModel LEVEL_MODEL = new DefaultComboBoxModel(new Level[] {Level.FINE, Level.INFO, Level.WARNING, Level.SEVERE});

    private static NavigableMap<Level, Color> levelColor = new TreeMap<Level, Color>(new Comparator<Level>() {
        @Override
        public int compare(Level o1, Level o2) {
            return Ints.compare(o1.intValue(), o2.intValue());
        }
    });

    private static final DateTimeFormatter format = new DateTimeFormatterBuilder()
            .appendHourOfDay(2)
            .appendLiteral(':')
            .appendMinuteOfHour(2)
            .appendLiteral(':')
            .appendSecondOfMinute(2)
            .toFormatter();

    static {
        levelColor.put(Level.ALL, Color.GREEN.darker()); // lowest possible level
        levelColor.put(Level.INFO, Color.BLUE.darker());
        levelColor.put(Level.WARNING, Color.YELLOW.darker());
        levelColor.put(Level.SEVERE, Color.RED.darker());
    }

    static Color toColor(Level level) {
        return levelColor.headMap(level, true).lastEntry().getValue();
    }

    static String formatTime(long millis) {
        return format.print(millis);
    }
}
