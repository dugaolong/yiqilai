package www.dugaolong.com.yiqilai;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.tencent.smtt.sdk.CookieSyncManager;

import www.dugaolong.com.yiqilai.base.BaseActivity;


/**
 * Created by dugaolong on 17/3/13.
 */

public class MainActivity extends BaseActivity {
    private Context mContext;
    private WebView webView;//系统自带的WebView
    //            private String url = "http://192.168.1.5:8080/attendance/Mobilelogin.html";
//    private String url = "https://web.chelaile.net.cn/ch5/index.html";
//    private String url = "http://map.baidu.com/mobile/webapp/index/index?itj=45&wtj=wi&ssid=2640bfecc0d6b5c4bdf0caafcdb70329&from=1012852s&uid=&pu=sz%40320_1004%2Cta%40iphone_2_6.0_11_9.2&bd_page_type=1";
//    private String url = "http://www.baidu.com/";
//    private String url = "https://m.amap.com";
//    private String url = "http://ditu.amap.com/";
//    private String url = "file:///android_asset/test.html";
//    private String url = "http://180.76.160.67:8080/attendance/Mobilelogin.html";
//    private String url = "http://1.85.16.226:9001/Mobilelogin.html";
    private String urlBase = "http://1.85.16.226:9002/";
    private String url = urlBase + "Mobilelogin.html";
    private String urlMain = urlBase + "Mobileindex.html";
    private String urlLoginOut = urlBase + "mobilelogin/logout.html";

//    private String url_local = "http://180.76.160.67:8080/attendance/Mobilelogin.html";

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {

        }
    };
    Dialog dialog;
    FrameLayout frameLayout;
    Button text;
    private static final String TAG = "MainActivity";

    private String number;
    private final int SPLASH_DISPLAY_LENGHT = 4000; //延迟三秒
    Runnable runnableClose = null;
    private Handler mHandler = new Handler();
    private long exitTime = 0;
    private int flag = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.webview_layout);
        super.hideTitle(0);
        initView();
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        mLocationClient.startAssistantLocation();
        webView.loadUrl(url);
        showDialog();
        reHandler();
        mHandler.postDelayed(runnableClose, SPLASH_DISPLAY_LENGHT);//4秒后执行runnable.
        flag = -1;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        text = (Button) findViewById(R.id.text);
        webView = (WebView) findViewById(R.id.webview);
//        webView = new WebView(getApplicationContext());
//        FrameLayout.LayoutParams mLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        frameLayout.addView(webView, mLayoutParams);
        webView.setLayerType(View.LAYER_TYPE_NONE, null);
        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSavePassword(true);
        //启用数据库
        webSettings.setDatabaseEnabled(true);
        //设置定位的数据库路径
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setGeolocationDatabasePath(dir);
        //启用地理定位
        webSettings.setGeolocationEnabled(true);
        //开启DomStorage缓存
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //支持屏幕缩放
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //配置权限
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
                callback.invoke(origin, true, true);
            }
//            @Override
//            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
//                return true;
//            }
        });
        webView.setWebViewClient(new WebViewClient() {
            // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//
                if (url.startsWith("http:") || url.startsWith("https:")) {
//                    if(url.contains("index.")){
//                        return true;
//                    }else {
                    view.loadUrl(url);
//                    }
                }
                if (url.startsWith("tel:")) {
                    makeTel(url);
                }
                if (url.contains("logout")) {
                    clearCookies(mContext);
                }
                flag = -1;
                return true;
            }

            //加载https时候，需要加入 下面代码
            @Override
            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler, SslError error) {

                handler.proceed();  //接受所有证书
            }

            // 旧版本，会在新版本中也可能被调用，所以加上一个判断，防止重复显示
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return;
                }
                // 在这里显示自定义错误页
                frameLayout.setVisibility(View.GONE);
                text.setVisibility(View.VISIBLE);
            }

            // 新版本，只会在Android6及以上调用
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (request.isForMainFrame()) { // 或者： if(request.getUrl().toString() .equals(getUrl()))
                    // 在这里显示自定义错误页
                    frameLayout.setVisibility(View.GONE);
                    text.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                removeProgress();//当加载结束时移除动画
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showProgress("页面加载中");//开始加载动画
            }

        });

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility(View.VISIBLE);
                text.setVisibility(View.GONE);
                webView.loadUrl(url);
            }
        });

    }

    private void makeTel(String url) {
        int len = "tel:".length();
        number = url.substring(len);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, INT_CALL_PHONE);
            }
        } else {
            //用intent启动拨打电话
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stopAssistantLocation();
        finishAll();
    }

    @Override
    protected void findWidgets() {
    }

    @Override
    protected void initComponent() {
    }

    @Override
    protected void getIntentData() {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
//            webView.goBack();// 返回前一个页面
//            return true;
//        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            webView.loadUrl(urlMain);
            flag = 1;
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void showDialog() {
        dialog = new Dialog(this, R.style.Dialog_Fullscreen);
        dialog.setContentView(R.layout.splash);
        dialog.show();
    }

    private void reHandler() {
        runnableClose = new Runnable() {
            @Override
            public void run() {
                //要做的事情
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        };
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (flag == 1) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(getApplicationContext(), "再按一次,退出应用", Toast.LENGTH_LONG).show();
                    exitTime = System.currentTimeMillis();
                    return false;
                } else {
                    finish();
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }
    public void clearCookies(Context context) {
        //清空所有Cookie
        CookieSyncManager.createInstance(context);  //Create a singleton CookieSyncManager within a context
        CookieManager cookieManager = CookieManager.getInstance(); // the singleton CookieManager instance
        cookieManager.removeAllCookie();// Removes all cookies.
        CookieSyncManager.getInstance().sync(); // forces sync manager to sync now

        webView.clearCache(true);
        WebStorage.getInstance().deleteAllData(); //清空WebView的localStorage
    }
}
