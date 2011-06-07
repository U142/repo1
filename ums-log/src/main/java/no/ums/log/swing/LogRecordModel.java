package no.ums.log.swing;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class LogRecordModel extends AbstractListModel {

    private final List<LogRecord> content = new ArrayList<LogRecord>();
    private final List<LogRecord> filteredList = new ArrayList<LogRecord>();

    private Level level = Level.SEVERE;

    public List<LogRecord> getAllRecords() {
        return new ArrayList<LogRecord>(content);
    }

    @Override
    public int getSize() {
        return filteredList.size();
    }

    @Override
    public Object getElementAt(int index) {
        return filteredList.get(index);
    }

    public void add(LogRecord record) {
        content.add(record);
        internalAdd(record);
    }

    private void internalAdd(LogRecord record) {
        if (record.getLevel().intValue() >= level.intValue()) {
            filteredList.add(record);
            fireIntervalAdded(this, filteredList.size() - 1, filteredList.size() - 1);
        }
    }

    void setLevel(Level level) {
        if (!this.level.equals(level)) {
            this.level = level;
            refresh();
        }
    }

    private void refresh() {
        int size = filteredList.size();
        filteredList.clear();
        if (size > 0) {
            fireIntervalRemoved(this, 0, size - 1);
        }
        // Copy content to avoid concurrent modification exception.
        for (LogRecord record : content.toArray(new LogRecord[content.size()])) {
            internalAdd(record);
        }
    }

    public Level getLevel() {
        return level;
    }

    public boolean is(final Level level1, final int index) {
        return index < filteredList.size() && filteredList.get(index).getLevel().intValue() >= level1.intValue();
    }

    public void clear() {
        content.clear();
        refresh();
    }
}
