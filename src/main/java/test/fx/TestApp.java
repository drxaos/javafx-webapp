package test.fx;

public class TestApp extends WebApp {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected int getWidth() {
        return 600;
    }

    @Override
    protected int getHeight() {
        return 600;
    }

    @Override
    protected String getStartUrl() {
        return "http://google.com/";
    }

    @Override
    protected void onRelocate(String url) {
        System.out.println("New URL: " + url);
    }

    @Override
    protected Object getAppBridge() {
        return null;
    }
}
