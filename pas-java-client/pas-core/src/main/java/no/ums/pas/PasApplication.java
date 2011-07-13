package no.ums.pas;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.ws.pas.Pasws;
import no.ums.ws.pas.PaswsSoap;
import no.ums.ws.pas.status.PasStatus;
import no.ums.ws.pas.status.PasStatusSoap;
import org.jdesktop.beansbinding.AbstractBean;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class PasApplication extends AbstractBean implements Executor {

    private static AtomicReference<PasApplication> instance = new AtomicReference<PasApplication>();
    private static final Log log = UmsLog.getLogger(PasApplication.class);

    public static PasApplication getInstance() {
        if (instance.get() == null) {
            throw new IllegalStateException("PasApplication has not been initialized yet, please call init");
        }
        return instance.get();
    }

    public static PasApplication newInstance(String wsdlBase) {
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(Math.max(16, Runtime.getRuntime().availableProcessors()));
        final WebServiceLoader loader = new WebServiceLoader(executor, wsdlBase);
        Future<PaswsSoap> paswsSoap = loader.getEndpoint(Pasws.class, PaswsSoap.class);
        Future<PasStatusSoap> pasStatus = loader.getEndpoint(PasStatus.class, PasStatusSoap.class);
        try {
            return new PasApplication(executor, paswsSoap.get(), pasStatus.get());
        } catch (InterruptedException e) {
            log.warn("Interrupted while starting PasApplication", e);
            throw new IllegalStateException(e);
        } catch (ExecutionException e) {
            log.error("Failed to start services", e);
            throw new IllegalStateException(e);
        }
    }


    public static PasApplication init(String wsdlBase) {
        final PasApplication pasApplication = newInstance(wsdlBase);
        if (!instance.compareAndSet(null, pasApplication)) {
            pasApplication.shutdown();
            throw new IllegalStateException("PasApplication has already been initialized, please check for multiple calls to init");
        }
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                pasApplication.shutdown();
            }
        }));
        return pasApplication;
    }

    private final ScheduledExecutorService executor;
    private final PaswsSoap paswsSoap;
    private final PasStatusSoap pasStatus;

    public PasApplication(ScheduledExecutorService executor, PaswsSoap paswsSoap, PasStatusSoap pasStatus) {
        this.executor = executor;
        this.paswsSoap = paswsSoap;
        this.pasStatus = pasStatus;
    }

    public PaswsSoap getPaswsSoap() {
        return paswsSoap;
    }

    public PasStatusSoap getPasStatus() {
        return pasStatus;
    }

    public void shutdown() {
        executor.shutdown();
        instance.compareAndSet(this, null);
        Runtime.getRuntime().halt(0);
    }

    @Override
    public void execute(Runnable command) {
        executor.execute(command);
    }

    public ScheduledExecutorService getExecutor() {
        return executor;
    }
}
