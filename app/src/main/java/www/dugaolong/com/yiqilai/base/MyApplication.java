package www.dugaolong.com.yiqilai.base;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

/**
 * 系统组件
 */
public class MyApplication extends Application {
    private static Context appContext;
    public static MyApplication instance;
    private Bitmap screenShot;
    String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        instance = this;

    }

    public static MyApplication getInstance() {
        if (instance == null) {
            instance = new MyApplication();
        }
        return instance;
    }

    public static Context getAppContext() {
        return appContext;
    }

}
