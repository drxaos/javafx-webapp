package test.fx;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class WebAppURLStreamHandlerFactory implements URLStreamHandlerFactory {
    Object target;

    public WebAppURLStreamHandlerFactory(Object target) {
        this.target = target;
    }

    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (protocol.equals("webapp")) {
            return new WebAppURLHandler(target);
        }
        return null;
    }

}