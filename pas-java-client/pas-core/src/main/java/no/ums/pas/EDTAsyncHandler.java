package no.ums.pas;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.awt.EventQueue;
import java.util.concurrent.ExecutionException;

/**
 * Implementation of AsyncHandler that will execute {@link #handle(Object)} on the EDT.
 *
 * Use this when the result of a webservice call needs to result in events
 * to be executed on the EDT (Event Dispatch Thread). This class will handle
 * exception handling as a result off the call, and invoke the {@link #handle(Object)}
 * method if all goes well.
 *
 * @author Ståle Undheim <su@ums.no>
 */
public abstract class EDTAsyncHandler<T> implements AsyncHandler<T> {

    @Override
    public void handleResponse(Response<T> res) {
        try {
            final T response = res.get();
            prepare(response);
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    handle(response);
                }
            });
        } catch (final InterruptedException e) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    handleInterrupt(e);
                }
            });
        } catch (final ExecutionException e) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    handleServiceException(e);
                }
            });
        }
    }

    protected void handleServiceException(ExecutionException e) {
        handleException(e);
    }

    protected void handleInterrupt(InterruptedException e) {
        handleException(e);
    }

    protected void handleException(Exception e) {
        throw new IllegalStateException("Failed to execute webservice call", e);
    }

    protected void prepare(T response) {
        // Noop
    }

    protected void handle(T response) {
        // Noop
    }
}
