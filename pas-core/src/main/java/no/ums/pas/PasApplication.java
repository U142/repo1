package no.ums.pas;

import no.ums.auth.AuthInfo;
import no.ums.auth.AuthToken;
import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.ws.auth.AuthService;
import no.ums.ws.auth.AuthServiceSoap;
import no.ums.ws.parm2.ParmService;
import no.ums.ws.parm2.ParmServiceSoap;
import no.ums.ws.pas.Pasws;
import no.ums.ws.pas.PaswsSoap;
import no.ums.ws.pas.status.PasStatus;
import no.ums.ws.pas.status.PasStatusSoap;
import org.jdesktop.beansbinding.AbstractBean;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class PasApplication extends AbstractBean implements Executor, AuthTokenHolder {

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
        Future<ParmServiceSoap> parm = loader.getEndpoint(ParmService.class, ParmServiceSoap.class);
        Future<PasStatusSoap> pasStatus = loader.getEndpoint(PasStatus.class, PasStatusSoap.class);
        Future<AuthServiceSoap> authService = loader.getEndpoint(AuthService.class, AuthServiceSoap.class);
        try {
            return new PasApplication(executor, paswsSoap.get(), parm.get(), pasStatus.get(), authService.get());
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
    private final ParmServiceSoap parmService;
    private final PasStatusSoap pasStatus;
    private final AuthServiceSoap authService;

    private AuthInfo authInfo;
    private AuthToken token;

    public PasApplication(ScheduledExecutorService executor, PaswsSoap paswsSoap, ParmServiceSoap parmService, PasStatusSoap pasStatus, AuthServiceSoap authService) {
        this.executor = executor;
        this.paswsSoap = paswsSoap;
        this.parmService = parmService;
        this.pasStatus = pasStatus;
        this.authService = authService;
    }

    public PaswsSoap getPaswsSoap() {
        return paswsSoap;
    }

    public ParmServiceSoap getParmService() {
        return parmService;
    }

    public PasStatusSoap getPasStatus() {
        return pasStatus;
    }

    public AuthServiceSoap getAuthService() {
        return authService;
    }

    public void shutdown() {
        executor.shutdown();
        instance.compareAndSet(this, null);
        Runtime.getRuntime().halt(0);
    }

    public AuthInfo login(String username, String customer, String password) {
        setAuthInfo(authService.login(username, customer, password));
        return getAuthInfo();
    }

    public void logout() {
        if (getAuthInfo() != null) {
            authService.logout(getAuthInfo().getSessionId());
        }
    }

    public AuthInfo getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(AuthInfo authInfo) {
        AuthInfo oldValue = this.authInfo;
        this.authInfo = authInfo;
        update("authInfo", oldValue, authInfo);
        setToken(authInfo.getDefaultToken());
    }

    @Override
    public AuthToken getToken() {
        return token;
    }

    @Override
    public void setToken(AuthToken value) {
        final AuthToken oldValue = this.token;
        this.token = value;
        update("token", oldValue, value);
    }

    @Override
    public void execute(Runnable command) {
        executor.execute(command);
    }

    public ScheduledExecutorService getExecutor() {
        return executor;
    }
}
