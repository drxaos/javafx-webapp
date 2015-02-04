package test.fx;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebHistory.Entry;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;


abstract public class WebApp extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Web Application");
        stage.setScene(new Scene(new Browser(this), getWidth(), getHeight(), Color.web("#FFFFFF")));
        stage.show();
    }

    abstract protected int getWidth();

    abstract protected int getHeight();

    abstract protected String getStartUrl();

    abstract protected void onRelocate(String url);

    protected void onStateChanged(WebEngine webEngine, ObservableValue<? extends State> ov,
                                  State oldState, State newState) {
        if (newState == State.SUCCEEDED) {
            JSObject windowObject = (JSObject) webEngine.executeScript("window");
            windowObject.setMember("app", getAppBridge());
        }
    }

    abstract protected Object getAppBridge();

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
                        webApp.onStateChanged(webEngine, ov, oldState, newState);
                    }
                }
        );

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