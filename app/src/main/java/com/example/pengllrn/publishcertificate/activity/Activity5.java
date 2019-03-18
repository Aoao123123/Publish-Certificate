package com.example.pengllrn.publishcertificate.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pengllrn.publishcertificate.Adapter.CheckAllTagStorageTimeAdapter;
import com.example.pengllrn.publishcertificate.Adapter.TaggAdapter;
import com.example.pengllrn.publishcertificate.R;
import com.example.pengllrn.publishcertificate.bean.Tagg;
import com.example.pengllrn.publishcertificate.constant.Constant;
import com.example.pengllrn.publishcertificate.gson.ParseJson;
import com.example.pengllrn.publishcertificate.internet.OkHttp;

import java.util.List;

public class Activity5 extends AppCompatActivity {

    private String applyUrl = Constant.URL_ALL_TAG;
    private ParseJson mParseJson = new ParseJson();
    private ListView allTagStorageTimeLv;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x2020:
                    try {
                        String reponsedata = (msg.obj).toString();
                        List<Tagg> listTagHaveStorageTime = mParseJson.TaggCheckStorageTimePoint(reponsedata);
                        if (listTagHaveStorageTime != null) {
                            allTagStorageTimeLv.setAdapter(new CheckAllTagStorageTimeAdapter(Activity5.this,listTagHaveStorageTime,R.layout.base_list_item2));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 0x22:
                    Toast.makeText(Activity5.this,"网络延迟",Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_5);
        allTagStorageTimeLv = (ListView) findViewById(R.id.lv_all_tag_storage_time);
        sendingtoServer();
    }

    public void sendingtoServer() {
        OkHttp okHttp = new OkHttp(getApplicationContext(),mHandler);
        okHttp.getFromInternet(applyUrl);
    }
}
