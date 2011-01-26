package no.ums.pas.ui;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class HelloProxy {

    interface Hello {
        String msg();
    }

    public static void main(String[] args) {
        ClassLoader classloader = Hello.class.getClassLoader();
        Class[] interfaces = {Hello.class};
        Hello hello = (Hello) Proxy.newProxyInstance(classloader, interfaces,
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method m, Object[] args) {
                        return "You invoked: " + m.getName();
                    }
                });
        // Result: You invoked: msg
        System.out.println(hello.msg());
    }
}
