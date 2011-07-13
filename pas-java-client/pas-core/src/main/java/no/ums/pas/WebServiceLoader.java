package no.ums.pas;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapMaker;
import no.ums.log.Log;
import no.ums.log.UmsLog;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.soap.SOAPFaultException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class WebServiceLoader {

    private static final Log log = UmsLog.getLogger(WebServiceLoader.class);

    private final ExecutorService executor;
    private final String wsdlBase;

    public WebServiceLoader(ExecutorService executor, String wsdlBase) {
        this.executor = executor;
        this.wsdlBase = wsdlBase;
    }

    public <S extends Service, SI> Future<SI> getEndpoint(final Class<S> serviceClass, final Class<SI> serviceInterface) {
        return executor.submit(new Callable<SI>() {
            @Override
            public SI call() throws Exception {
                WebServiceClient webServiceClient = serviceClass.getAnnotation(WebServiceClient.class);
                String namespace = webServiceClient.targetNamespace();
                String serviceName = webServiceClient.name();
                String wsdlFileName = Iterables.getLast(Splitter.on('/').split(webServiceClient.wsdlLocation()));
                String wsdlUrl = wsdlBase + (wsdlBase.endsWith("/") ? "" : "/") + wsdlFileName;
                try {
                    log.debug("Instantiating service [%s] using [%s] at [%s]", serviceName, serviceClass.getSimpleName(), wsdlUrl);
                    S service = serviceClass.getConstructor(URL.class, QName.class).newInstance(new URL(wsdlUrl), new QName(namespace, serviceName));
                    service.setExecutor(executor);
                    for (Method method : serviceClass.getMethods()) {
                        if (method.getParameterTypes().length == 0 &&
                                serviceInterface.isAssignableFrom(method.getReturnType()) &&
                                method.isAnnotationPresent(WebEndpoint.class) &&
                                method.getAnnotation(WebEndpoint.class).name().endsWith("Soap12")) {
                            final Object endpoint = method.invoke(service);
                            Object wrapped = Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceInterface}, new WsInvocationHandler(endpoint, method.getReturnType(), executor));
                            return serviceInterface.cast(wrapped);
                        }
                    }
                    throw new IllegalArgumentException("Could not find Soap12 port on " + serviceClass);
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException("Not a valid URL: " + wsdlUrl);
                } catch (NoSuchMethodException e) {
                    throw new IllegalArgumentException("Failure to lookup method", e);
                } catch (InstantiationException e) {
                    throw new IllegalArgumentException("Could not create an instance of " + serviceClass, e);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException(e);
                }
            }
        });
    }

    private static class Invocation {
        private final Method method;
        private final Object[] args;

        private Invocation(Method method, Object[] args) {
            this.method = Preconditions.checkNotNull(method, "method cannot be null");
            this.args = args;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Invocation that = (Invocation) o;

            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals(args, that.args) && method.equals(that.method);
        }

        @Override
        public int hashCode() {
            return 31 * method.hashCode() + (args != null ? Arrays.hashCode(args) : 0);
        }

        @Override
        public String toString() {
            final StringBuilder toString = new StringBuilder(method.getName());
            toString.append("(");
            if (args != null) {
                String[] strArgs = new String[args.length];
                for (int i = 0; i < args.length; i++) {
                    strArgs[i] = String.valueOf(args[i]);
                }
                Joiner.on(", ").appendTo(toString, strArgs);
            }
            toString.append(")");
            return toString.toString();
        }
    }

    private static final Pattern soapExceptionExp = Pattern.compile("---> ([^:]+): ([^\\\n]+)\\\n");


    private static class WsInvocationHandler implements InvocationHandler, Function<Invocation, Object[]> {

        //        private final Map<Invocation, Object[]> resultCache;
        private final Log log;
        private final Object delegate;
//        private final Executor executor;

        public WsInvocationHandler(final Object delegate, Class type, Executor executor) {
//            this.executor = executor;
            log = UmsLog.getLogger(type);
            this.delegate = delegate;
//            resultCache = new MapMaker()
//                    .expiration(1, TimeUnit.MINUTES)
//                    .makeComputingMap(this);
        }

        @Override
        public Object[] apply(@Nullable final Invocation input) {
            try {
                final long start = System.currentTimeMillis();
                Object[] args = input.args;
                boolean isAsync = args != null && args.length > 0 && args[args.length - 1] instanceof AsyncHandler;
                if (isAsync) {
                    @SuppressWarnings("unchecked")
                    final AsyncHandler<Object> original = (AsyncHandler<Object>) args[args.length - 1];
                    args[args.length - 1] = new AsyncHandler<Object>() {
                        @Override
                        public void handleResponse(Response<Object> res) {
                            try {
                                log.debug("AsyncInvoke %s in %dms -> %s", input, System.currentTimeMillis() - start, res.get());
                            } catch (InterruptedException e) {
                                log.error("Interrupted executing %s after %dms", input, System.currentTimeMillis() - start, e);
                            } catch (ExecutionException e) {
                                log.error("Failed to execute %s after %dms", input, System.currentTimeMillis() - start, e);
                            }
                            original.handleResponse(res);
                        }
                    };
                }
                Object result = input.method.invoke(getDelegate(), args);
                if (!isAsync) {
                    log.debug("Invoked %s in %dms -> %s", input, System.currentTimeMillis() - start, result);
                }
                // Null values are not allowed, so we always wrap the result in an
                // object array. void methods will return null.
                return new Object[]{result};
            } catch (IllegalAccessException e) {
                log.error("Failed to execute %s", input, e);
                throw new IllegalStateException(e);
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof SOAPFaultException) {
                    final SOAPFaultException se = (SOAPFaultException) e.getCause();
                    Matcher matcher = soapExceptionExp.matcher(se.getMessage());
                    if (matcher.find()) {
                        log.error("SOAP Exception for [%s]: class: [%s] msg: [%s].", input, matcher.group(1), matcher.group(2), se);
                    } else {
                        log.error("SOAP Fault for [%s] - could not parse", input, se);
                    }
                } else {
                    log.error("Failed to execute %s", input, e);
                }
                throw new IllegalStateException(e);
            }
        }

        private Object getDelegate() {
            return delegate;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return apply(new Invocation(method, args))[0];
        }
    }
}
