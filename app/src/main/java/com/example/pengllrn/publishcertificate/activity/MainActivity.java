package com.example.pengllrn.publishcertificate.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.pengllrn.publishcertificate.R;
import com.example.pengllrn.publishcertificate.base.BaseNfcActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseNfcActivity implements View.OnClickListener {


    /**
     * ImageView
     */
    private ImageView imageView1;   //出库
    private ImageView imageView2;   //入库
    private ImageView imageView3;   //盘库


    private Intent intent;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initListener();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//        SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("Name", logo);
//        editor.putString("Type", mtype);
//        editor.putInt("publishNum",publishNum);
//        editor.apply();
//    }

    /**
     * 初始化UI
     */
    private void initUI() {

        imageView1 = (ImageView) findViewById(R.id.img1);
        imageView2 = (ImageView) findViewById(R.id.img2);
        imageView3 = (ImageView) findViewById(R.id.img3);
    }

    /**
     * 初始化监听
     */
    private void initListener() {

        imageView1.setOnClickListener(this);
        imageView2.setOnClickListener(this);
        imageView3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img1:
                intent = new Intent(MainActivity.this, Activity1.class);
                startActivity(intent);
                break;
            case R.id.img2:
                intent = new Intent(MainActivity.this, Activity2.class);
                startActivity(intent);
                break;
            case R.id.img3:
                intent = new Intent(MainActivity.this, Activity3.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


}
