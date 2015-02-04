package test.fx;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class WebAppURLHandler extends URLStreamHandler {
    Object target;

    public WebAppURLHandler(Object target) {
        this.target = target;
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        return new WebAppURLConnection(url, target);
    }

}