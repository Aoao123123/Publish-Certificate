package com.example.pengllrn.publishcertificate.activity;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.pengllrn.publishcertificate.R;
import com.example.pengllrn.publishcertificate.base.BaseNfcActivity;

import java.nio.charset.Charset;

public class InitializationActivity extends BaseNfcActivity {

    private String mText;//保存证书
    private boolean isSuccessfullyWritted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialization);
    }

    @Override
    public void onNewIntent(Intent intent) {
        mText = "\0\0\0\0";
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (detectedTag == null) {
            Toast.makeText(this,"NFC标签未探测成功，请将标签靠近手机NFC检测区域再次探测",Toast.LENGTH_SHORT).show();
            return;
        }
        //写非NDEF格式的数据
        String[] techList = detectedTag.getTechList();
        boolean haveMifareUltralight = false;
        for (String tech : techList) {
            if (tech.indexOf("MifareUltralight") >= 0) {
                haveMifareUltralight = true;
                break;
            }
        }

        if (!haveMifareUltralight) {
            Toast.makeText(this, "不支持MifareUltralight数据格式", Toast.LENGTH_SHORT).show();
            return;
        }

        writeTag(detectedTag,mText);
    }

    /** 写非ndef格式数据
     *
     */
    public void writeTag(Tag tag, String num) {
        MifareUltralight ultralight = MifareUltralight.get(tag);
        try {
            ultralight.connect();
            ultralight.writePage(22, num.getBytes(Charset.forName("US-ASCII")));

            System.out.println("非ndef数据写入成功");
            Toast.makeText(this,"标签初始化成功",Toast.LENGTH_SHORT).show();
            isSuccessfullyWritted = true;
        } catch (Exception e) {
            System.out.println("非ndef写入异常");
            Toast.makeText(this,"标签数据未初始化成功，请将标签靠近手机NFC检测区域再次初始化",Toast.LENGTH_SHORT).show();
            isSuccessfullyWritted = false;
        } finally {
            try {
                ultralight.close();
            } catch (Exception e) {
            }
        }
    }
}
