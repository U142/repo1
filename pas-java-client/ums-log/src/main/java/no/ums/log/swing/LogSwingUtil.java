/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package no.ums.log.swing;

import com.google.common.primitives.Ints;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.util.Comparator;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author staaleu
 */
public final class LogSwingUtil {

    private LogSwingUtil() {

    }

    public static final ListCellRenderer LOG_RECORD_RENDERER = new ListCellRenderer() {

        @Override
        public Component getListCellRendererComponent(final JList list, final Object value, final int index,
                                                      final boolean isSelected, final boolean cellHasFocus) {
            return new LogRecordLine((LogRecord) value, isSelected);
        }
    };

    public static final ComboBoxModel LEVEL_MODEL = new DefaultComboBoxModel(new Level[]{
            Level.FINE, Level.INFO, Level.WARNING, Level.SEVERE});

    private static NavigableMap<Level, Color> levelColor = new TreeMap<Level, Color>(new Comparator<Level>() {
        @Override
        public int compare(final Level o1, final Level o2) {
            return Ints.compare(o1.intValue(), o2.intValue());
        }
    });

    private static final DateTimeFormatter TIME_FORMAT = new DateTimeFormatterBuilder()
            .appendHourOfDay(2)
            .appendLiteral(':')
            .appendMinuteOfHour(2)
            .appendLiteral(':')
            .appendSecondOfMinute(2)
            .toFormatter();

    private static final DateTimeFormatter DATE_TIME_FORMAT = new DateTimeFormatterBuilder()
            .appendYear(4, 4)
            .appendMonthOfYear(2)
            .appendDayOfMonth(2)
            .appendLiteral('-')
            .appendHourOfDay(2)
            .appendMinuteOfHour(2)
            .appendSecondOfMinute(2)
            .toFormatter();


    static {
        levelColor.put(Level.ALL, Color.GREEN.darker()); // lowest possible level
        levelColor.put(Level.INFO, Color.BLUE.darker());
        levelColor.put(Level.WARNING, Color.YELLOW.darker());
        levelColor.put(Level.SEVERE, Color.RED.darker());
    }

    static Color toColor(final Level level) {
        return levelColor.headMap(level, true).lastEntry().getValue();
    }

    static String formatTime(final long millis) {
        return TIME_FORMAT.print(millis);
    }

    static String formatDateTime(final long millis) {
        return DATE_TIME_FORMAT.print(millis);
    }
}
