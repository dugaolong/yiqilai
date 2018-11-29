package www.dugaolong.com.yiqilai.base;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.smtt.sdk.CookieSyncManager;

import java.util.ArrayList;
import java.util.List;

import www.dugaolong.com.yiqilai.MainActivity;
import www.dugaolong.com.yiqilai.R;
import www.dugaolong.com.yiqilai.utils.CustomDialog;


/**
 * activity基类
 */
public abstract class BaseActivity extends AppCompatActivity {

    public static Activity instance;
    private String session;
    public static final String formatter = "%s_%s_%s".replaceAll("_", "::");

    public static final int INT_ACCESS_FINE_LOCATION = 1;
    public static final int INT_CALL_PHONE = 2;
    public Context mContext;
    protected DisplayMetrics metric;
    protected int screenWidth;
    protected int screenHeight;
    private CustomDialog progressDialog ;

    /**
     * 初始化控件
     */
    protected abstract void findWidgets();

    /**
     * 初始化控件数据
     */
    protected abstract void initComponent();

    /**
     * 初始化数据
     */
    protected abstract void getIntentData();

    /**
     * 标题栏标题
     */
    public TextView title;

    /**
     * 中间内容区域的容器
     */
    public LinearLayout base_content;
    /**
     * 中间内容区域的布局
     */
    private View contentView;
    /**
     * FrameLayout
     */
    public FrameLayout framelayout_root;
    /**
     * 标题栏根布局
     */
    public RelativeLayout rl_common_title;

    /**
     * 标题栏右边按钮
     */
    public TextView tv_right_text;

    /**
     * 返回按钮
     */
    public ImageView image_back;
    /**
     * 标题右侧图标
     */
    public ImageView image_right;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //设置activity主题
        setTheme(android.support.v7.appcompat.R.style.Theme_AppCompat_Light_NoActionBar);

        instance = this;
        //设置沉浸式标题栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }



        mContext = this;
        // 添加Activity到堆栈
        ActivityManager.getAppManager().addActivity(this);

        if (metric == null) {
            metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metric);
        }
        screenWidth = metric.widthPixels;
        screenHeight = metric.heightPixels;

        super.setContentView(R.layout.activity_base_layout);
        this.init();


        // 如果targetSDKVersion >= 23，就要申请好权限。如果您的App没有适配到Android6.0（即targetSDKVersion < 23），那么只需要在这里直接调用fetchSplashAD接口。
        if (Build.VERSION.SDK_INT >= 23) {
            checkAndRequestPermission();
        }
    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
//        outState.putSerializable("role", role);
//        outState.putString("session", APPPreferenceUtil.getInstance().getSession());
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        if (savedInstanceState != null) {
//            role = (Role) savedInstanceState.getSerializable("role");
//            session = savedInstanceState.getString("session");
//            APPPreferenceUtil.getInstance().setSession(session);
//            ((kgApplication) this.getApplication()).setRole(role);
//
//        }
//    }

    /**
     * 设置内容区域
     *
     * @param resId 资源文件id
     */
    @Override
    public void setContentView(int resId) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.contentView = inflater.inflate(resId, null);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        this.contentView.setLayoutParams(layoutParams);
        if (null != this.base_content) {
            this.base_content.addView(this.contentView);
        }
        getIntentData();
        findWidgets();
        initComponent();
        initListener();
        initHandler();
        asyncRetrive();
    }

    private void init() {
        this.rl_common_title = (RelativeLayout) findViewById(R.id.rl_common_title);
        this.image_back = (ImageView) findViewById(R.id.image_back);
        this.image_right = (ImageView) findViewById(R.id.common_right);
        this.title = (TextView) findViewById(R.id.tv_common_title);
        this.tv_right_text = (TextView) findViewById(R.id.tv_right_text);
        this.base_content = (LinearLayout) findViewById(R.id.base_content);
        this.framelayout_root = (FrameLayout) findViewById(R.id.framelayout_root);
    }

    /**
     * 隐藏标题栏
     */
    public void hideTitle(int colorRes) {
        rl_common_title.setVisibility(View.GONE);
//        setRootTopColor(colorRes);
    }

    /**
     * 设置沉浸式顶部颜色（和你当前页面顶部颜色一致）
     *
     * @param colorRes
     */
    public void setRootTopColor(int colorRes) {
        framelayout_root.setBackgroundColor(getResources().getColor(colorRes));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        OkHttpProxy.cancel(OkHttpManager.getInstance());
        // 结束Activity&从堆栈中移除
        ActivityManager.getAppManager().finishActivity(this);
    }

    /**
     * 初始化控件
     *
     * @param id
     * @param <T>
     * @return
     */
    protected <T> T findView(int id) {
        return (T) findViewById(id);
    }

    /**
     * 初始化Listener，子类根据需要自行重写
     */
    protected void initListener() {
        return;
    }

    /**
     * 初始化Handler，子类根据需要自行重写
     */
    protected void initHandler() {
        return;
    }


    /**
     * 异步查询网络数据，子类根据需要自行重写
     */
    protected void asyncRetrive() {
        return;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
        }
        return super.dispatchKeyEvent(event);
    }

    public void showDialog(String string) {
    }

    public void closeDialog() {

    }


    public void back(View v) {
        finish();
    }

    public void finishAll(){

        // 结束所有的Activity
        ActivityManager.getAppManager().finishAllActivity();
    }

    /**
     *
     * ----------非常重要----------
     *
     * Android6.0以上的权限适配简单示例：
     *
     * 如果targetSDKVersion >= 23，那么必须要申请到所需要的权限，再调用广点通SDK，否则广点通SDK不会工作。
     *
     * Demo代码里是一个基本的权限申请示例，请开发者根据自己的场景合理地编写这部分代码来实现权限申请。
     * 注意：下面的`checkSelfPermission`和`requestPermissions`方法都是在Android6.0的SDK中增加的API，如果您的App还没有适配到Android6.0以上，则不需要调用这些方法，直接调用广点通SDK即可。
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermission() {
        List<String> lackedPermission = new ArrayList<String>();
        if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        // 权限都已经有了，那么直接调用SDK
        if (lackedPermission.size() == 0) {

        } else {
            // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            requestPermissions(requestPermissions, 1024);
        }
    }

    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1024 && hasAllPermissionsGranted(grantResults)) {

        } else {
            // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
            Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            finish();
        }
    }

    //-----显示ProgressDialog
    public void showProgress(String message) {
        if (progressDialog == null) {
            progressDialog = new CustomDialog(BaseActivity.this,  R.style.CustomDialog);
            progressDialog.setCancelable(false);//设置点击不消失
        }
        if (progressDialog.isShowing()) {
            progressDialog.setMessage(message);
        } else {
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }
    //------取消ProgressDialog
    public void removeProgress(){
        if (progressDialog==null){
            return;
        }
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }

    }

}
