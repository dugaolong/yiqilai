package www.dugaolong.com.yiqilai;


import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import www.dugaolong.com.yiqilai.base.BaseActivity;

public class TencentX5 extends BaseActivity {

    com.tencent.smtt.sdk.WebView webView;//腾讯X5WebView
//    private String url = "http://192.168.1.5:8080/attendance/Mobilelogin.html";
    private String url = "https://ditu.amap.com";
    private PopupWindow popupWindow=null; // 活动窗口
//    Handler handler=new Handler();
    Runnable runnableClose=null;
    LinearLayout ll_tencent;
    Dialog dialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tencent_x5);
        mContext = this;
        super.hideTitle(0);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);//（这个对宿主没什么影响，建议声明）
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initView();
//        showSexDialog();
//        reHandler();
//        handler.postDelayed(runnableClose, 4000);//4秒后执行runnable.
    }

//    private void reHandler() {
//        runnableClose=new Runnable() {
//            @Override
//            public void run() {
//                //要做的事情
//                if(dialog!=null){
//                    dialog.dismiss();
//                }
//            }
//        };
//    }

    private void initView() {
        ll_tencent = (LinearLayout) findViewById(R.id.ll_tencent);
        webView = (com.tencent.smtt.sdk.WebView)findViewById(R.id.WebView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
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
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        //配置权限
        webView.setWebChromeClient(new com.tencent.smtt.sdk.WebChromeClient(){
            @Override
            public void onGeolocationPermissionsShowPrompt(String s, GeolocationPermissionsCallback geolocationPermissionsCallback) {
                super.onGeolocationPermissionsShowPrompt(s, geolocationPermissionsCallback);
                geolocationPermissionsCallback.invoke(s, true, false);
            }
        });

        webView.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finishAll();
    }


    @Override
    protected void onResume() {
        super.onResume();
//        reHandler();
//        handler.postDelayed(runnableOpen, 200);//每3秒执行一次runnable.
//        handler.postDelayed(runnableClose, 3000);//每3秒执行一次runnable.
    }

    protected void showSexDialog() {
        dialog = new Dialog(this, R.style.Dialog_Fullscreen);
        dialog.setContentView(R.layout.dialog);
        dialog.show();
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
}