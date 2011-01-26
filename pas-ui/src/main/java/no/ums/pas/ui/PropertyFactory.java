package no.ums.pas.ui;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class PropertyFactory {

    public static <T> T get(Class<T> type) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, new InvocationHandler() {

private final Map<String, Object> values = new HashMap<String, Object>();

@Override
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (method.getName().startsWith("get")) {
        return values.get(method.getName().substring(4));
    }
    else if (method.getName().startsWith("set")) {
        values.put(method.getName().substring(4), args[0]);
    }
    return null;  //To change body of implemented methods use File | Settings | File Templates.
}
        }));
    }
}
