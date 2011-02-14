package no.ums.pas.parm2;

import com.google.common.base.Preconditions;
import no.ums.log.Log;
import no.ums.log.UmsLog;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public abstract class AsyncSoapListModel<R, T> extends AbstractListModel implements Runnable {

    private static final Log log = UmsLog.getLogger(AsyncSoapListModel.class);

    private final List<T> contents = new ArrayList<T>();
    private final ScheduledExecutorService service;
    private ScheduledFuture<?> scheduledFuture;

    protected AsyncSoapListModel(final ScheduledExecutorService service) {
        this.service = service;
    }

    @Override
    public void addListDataListener(final ListDataListener l) {
        super.addListDataListener(l);
        checkSchedule();
    }

    private synchronized void checkSchedule() {
        if (listenerList.getListenerCount() > 0 && scheduledFuture == null) {
            scheduledFuture = service.scheduleAtFixedRate(this, 0, 5, TimeUnit.SECONDS);
        } else if (scheduledFuture != null && listenerList.getListenerCount() <= 0) {
            scheduledFuture.cancel(true);
            scheduledFuture = null;
        }
    }

    @Override
    public void removeListDataListener(final ListDataListener l) {
        super.removeListDataListener(l);
        checkSchedule();
    }

    @Override
    public int getSize() {
        return contents.size();
    }

    @Override
    public Object getElementAt(final int index) {
        return contents.get(index);
    }

    @Override
    public void run() {
        invoke(new AsyncHandler<R>() {

            @Override
            public void handleResponse(final Response<R> res) {
                try {
                    if (res.isDone()) {
                        Collection<T> newValues = unpack(res.get());
                        Iterator<T> iterator = newValues.iterator();
                        int index = 0;
                        while (iterator.hasNext() && index < contents.size()) {
                            T obj = iterator.next();
                            if (!isEqual(obj, contents.get(index))) {
                                contents.set(index, obj);
                                fireContentsChanged(AsyncSoapListModel.this, index, index);
                            }
                            index++;
                        }
                        if (iterator.hasNext()) {
                            while (iterator.hasNext()) {
                                contents.add(iterator.next());
                            }
                            fireIntervalAdded(AsyncSoapListModel.this, index, contents.size());
                        } else if (index < contents.size()) {
                            int oldSize = contents.size();
                            for (int i=contents.size()-1; i>=index; i--) {
                                contents.remove(i);
                            }
                            fireIntervalRemoved(AsyncSoapListModel.this, index, oldSize);
                        }
                    }
                } catch (InterruptedException e) {
                    log.info("Update interrupted", e);
                } catch (ExecutionException e) {
                    log.warn("Failed to get update from server", e);
                }
            }
        });
    }

    @Nullable
    protected abstract Collection<T> unpack(final R r);

    protected abstract void invoke(@Nonnull final AsyncHandler<R> asyncHandler);

    protected abstract boolean isEqual(@Nonnull T a, @Nonnull T b);
}
