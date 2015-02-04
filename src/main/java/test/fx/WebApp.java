package test.fx;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.*;
import javafx.scene.web.WebHistory.Entry;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.net.URL;


abstract public class WebApp extends Application {

    Browser browser;

    @Override
    public void start(Stage stage) {
        stage.setTitle(getTitle());
        stage.setScene(new Scene(browser = new Browser(this), getWidth(), getHeight(), Color.web("#F00")));
        stage.show();
    }

    protected int getWidth() {
        return 800;
    }

    protected int getHeight() {
        return 600;
    }

    abstract protected String getStartUrl();

    protected String getUserAgent() {
        return "TestApplication/0.1";
    }

    protected String getTitle() {
        return "Test Application 0.1";
    }

    protected void onRelocate(String url) {
    }

    protected void onAlert(String data) {
    }

    protected void onError(String message, Throwable exception) {
        System.out.println(message);
        exception.printStackTrace();
    }

    protected void onResized(Rectangle2D bounds) {
    }

    protected void onStateChanged(ObservableValue<? extends State> ov,
                                  State oldState, State newState) {
        if (newState == State.SUCCEEDED) {
            JSObject windowObject = (JSObject) browser.webEngine.executeScript("window");
            windowObject.setMember("appBridge", getAppBridge());
        }
    }

    protected Object getAppBridge() {
        return null;
    }

    public JSObject evalJs(String js) {
        if (browser.webEngine.getLoadWorker().getState() == State.SUCCEEDED) {
            return (JSObject) browser.webEngine.executeScript(js);
        }
        return null;
    }

}

class Browser extends Region {
    final WebApp webApp;
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();

    public Browser(final WebApp webApp) {
        this.webApp = webApp;
        browser.getStyleClass().add("browser");
        browser.setContextMenuEnabled(false);

        final WebHistory history = webEngine.getHistory();
        history.getEntries().addListener(new ListChangeListener<WebHistory.Entry>() {
            @Override
            public void onChanged(Change<? extends Entry> c) {
                c.next();
                ObservableList<? extends Entry> list = c.getList();
                webApp.onRelocate(list.get(list.size() - 1).getUrl());
            }
        });

        webEngine.getLoadWorker().stateProperty().addListener(
                new ChangeListener<State>() {
                    @Override
                    public void changed(ObservableValue<? extends State> ov,
                                        State oldState, State newState) {
                        webApp.onStateChanged(ov, oldState, newState);
                    }
                }
        );

        webEngine.setUserAgent(webApp.getUserAgent());

        webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> event) {
                webApp.onAlert(event.getData());
            }
        });
        webEngine.setOnError(new EventHandler<WebErrorEvent>() {
            @Override
            public void handle(WebErrorEvent event) {
                webApp.onError(event.getMessage(), event.getException());
            }
        });
        webEngine.setOnResized(new EventHandler<WebEvent<Rectangle2D>>() {
            @Override
            public void handle(WebEvent<Rectangle2D> event) {
                webApp.onResized(event.getData());
            }
        });

        URL.setURLStreamHandlerFactory(new WebAppURLStreamHandlerFactory(webApp));
        webEngine.load(webApp.getStartUrl());

        getChildren().add(browser);
    }

    @Override
    protected void layoutChildren() {
        layoutInArea(browser, 0, 0, getWidth(), getHeight(), 0, HPos.CENTER, VPos.CENTER);
    }

    @Override
    protected double computePrefWidth(double height) {
        return webApp.getWidth();
    }

    @Override
    protected double computePrefHeight(double width) {
        return webApp.getHeight();
    }
}