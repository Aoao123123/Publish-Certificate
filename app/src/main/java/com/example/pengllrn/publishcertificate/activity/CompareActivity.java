package com.example.pengllrn.publishcertificate.activity;


import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pengllrn.publishcertificate.R;
import com.example.pengllrn.publishcertificate.base.BaseNfcActivity;


/**
 * Created by pengllrn on 2019/1/6.
 */

public class CompareActivity extends BaseNfcActivity {

    private String certificate1 = "";
    private String certificate2 = "";
    private TextView certi1;
    private TextView certi2;
    private TextView compareResult;
    private Button dataClear;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        initView();
        dataClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                certi1.setText("证书1为：");
                certi2.setText("证书2为：");
                compareResult.setText("比对结果为：");
                certificate1 = "";
                certificate2 = "";
                flag = false;
            }
        });
    }

    @Override
    public void onNewIntent(Intent intent) {
        if ((certificate1 == null) && (certificate2 == null))
            return;
        //获取Tag对象
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (detectedTag == null) {
            Toast.makeText(this,"NFC标签未探测成功，请将标签靠近手机NFC检测区域再次探测",Toast.LENGTH_SHORT).show();
            return;
        }
        //读非NDEF格式的数据
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

        if (!flag) {
            certificate1 = readTag(detectedTag);
            certi1.setText("证书1为：" + certificate1);
            flag = !flag;
        } else {
            certificate2 = readTag(detectedTag);
            certi2.setText("证书2为：" + certificate2);
            flag = !flag;
        }

        try {
            if (certificate1.equals(certificate2)) {
                compareResult.setText("比对结果为：证书一致，请按清除键清除数据");
            } else if ((certificate1.equals("") || (certificate2.equals("")))){
                compareResult.setText("请扫描第二张标签");
            } else {
                compareResult.setText("比对结果为：证书不一致，请按清除键清除数据");
            }
        } catch (Exception e) {
            compareResult.setText("证书写入未成功，请重新扫描");
        }

    }

    private void initView() {
        certi1 = (TextView) findViewById(R.id.certificate1);
        certi2 = (TextView) findViewById(R.id.certificate2);
        compareResult = (TextView) findViewById(R.id.compare_result);
        dataClear = (Button) findViewById(R.id.data_clear);
    }

    /**
     * 读取非ndef数据
     *
     * @param tag
     */
    public String readTag(Tag tag) {
        MifareUltralight ultralight = MifareUltralight.get(tag);
        String certificate = "";
        String temp = "";
        if (tag != null) {
            try {
                ultralight.connect();
                byte[] data = ultralight.readPages(22);
                for (int i = 0;i < 4;i++) {
                    temp = Integer.toHexString(data[i] & 0xFF);
                    if (temp.length() == 1) {
                        temp = "0" + temp;
                    }
                    certificate += temp;
                }
                return certificate;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(CompareActivity.this,"NFC标签数据未读取成功，请将标签靠近手机NFC检测区域再次读取",Toast.LENGTH_SHORT).show();
            } finally {
                try {
                    ultralight.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        return null;
    }
}
