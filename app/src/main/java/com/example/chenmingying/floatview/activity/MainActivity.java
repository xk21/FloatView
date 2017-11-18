package com.example.chenmingying.floatview.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.chenmingying.floatview.R;
import com.example.chenmingying.floatview.bean.GeekEvent;
import com.example.chenmingying.floatview.service.FloatWindowService;
import com.example.chenmingying.floatview.utils.Constants;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "MainActivity";
    public static final int GEEK_PERMISSION_REQ_CODE = 1235;
    private Button mBt_1;
    private Button mBt_2;
    private Button mBt_3;
    private Button mBt_4;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    
    private void initView() {
        mBt_1 = findViewById(R.id.bt_1);
        mBt_2 = findViewById(R.id.bt_2);
        mBt_3 = findViewById(R.id.bt_3);
        mBt_4 = findViewById(R.id.bt_4);
    
        mBt_1.setOnClickListener(this);
        mBt_2.setOnClickListener(this);
        mBt_3.setOnClickListener(this);
        mBt_4.setOnClickListener(this);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GEEK_PERMISSION_REQ_CODE){
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText( this, "授权失败无法开启", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "FloatWindowService.isOpen ="+FloatWindowService.isOpen );
                if (!FloatWindowService.isOpen) {
                    startService(new Intent(MainActivity.this, FloatWindowService.class));
                }
            }
        }
    }
    
    public void requestPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(MainActivity.this, "当前无权限使用悬浮框,请授权", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, GEEK_PERMISSION_REQ_CODE);
        } else {
            Log.d(TAG, "FloatWindowService.isOpen 2="+ FloatWindowService.isOpen );
            if (!FloatWindowService.isOpen) {
                startService(new Intent(MainActivity.this, FloatWindowService.class));
            }
        }
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_1:
                requestPermission();
                break;
            case R.id.bt_2:
                
                break;
            case R.id.bt_3:
                if (FloatWindowService.sGeekShopType==0){
                    FloatWindowService.sGeekShopType=1;
                }else {
                    FloatWindowService.sGeekShopType=0;
                }
                EventBus.getDefault().post(new GeekEvent(Constants.MO_GEEK_RESIDENT_SHOP));
                break;
            case R.id.bt_4:
                stopService(new Intent(MainActivity.this, FloatWindowService.class));
                break;
        }
    }
}
