package com.example.pengllrn.publishcertificate.activity;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pengllrn.publishcertificate.Adapter.TaggAdapter;
import com.example.pengllrn.publishcertificate.R;
import com.example.pengllrn.publishcertificate.base.BaseNfcActivity;
import com.example.pengllrn.publishcertificate.bean.Tagg;
import com.example.pengllrn.publishcertificate.constant.Constant;
import com.example.pengllrn.publishcertificate.gson.ParseJson;
import com.example.pengllrn.publishcertificate.internet.OkHttp;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class Activity3 extends AppCompatActivity {

    private String applyUrl = Constant.URL_CHECK_IN_STORAGE;
    private ParseJson mParseJson = new ParseJson();
    private ListView tagInStorageLv;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x2020:
                    try {
                        String reponsedata = (msg.obj).toString();
                        List<Tagg> listtaginstorage = mParseJson.TaggPoint(reponsedata);
                        if (listtaginstorage != null) {
                            tagInStorageLv.setAdapter(new TaggAdapter(Activity3.this,listtaginstorage,R.layout.base_list_item));
                        }
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                    break;
                case 0x22:
                    Toast.makeText(Activity3.this,"NFC标签未探测成功，请将标签靠近手机NFC检测区域再次探测",Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);
        tagInStorageLv = (ListView) findViewById(R.id.lv_tag_in_storage);
        sendtoServer();
    }
    public void sendtoServer() {
        OkHttp okHttp = new OkHttp(getApplicationContext(),mHandler);
        okHttp.getFromInternet(applyUrl);
    }
}
