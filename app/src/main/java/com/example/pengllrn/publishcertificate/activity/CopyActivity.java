package com.example.pengllrn.publishcertificate.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pengllrn.publishcertificate.R;
import com.example.pengllrn.publishcertificate.base.BaseNfcActivity;
import com.example.pengllrn.publishcertificate.constant.Constant;
import com.example.pengllrn.publishcertificate.gson.ParseJson;
import com.example.pengllrn.publishcertificate.internet.OkHttp;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by pengllrn on 2019/1/4.
 */

public class CopyActivity extends BaseNfcActivity {

    private String cText = "";//复制的证书
    private String cGroupNum = "";//复制的组号
    private String mlogo;
    private String mtype;
    private String temp = "";//状态位
    private String certi_server = "";//发给服务器的证书
    private String uid_server = "";//发给服务器的uid
    private String applyUrl = Constant.URL_ADD_TAG;
    private ParseJson mParseJson = new ParseJson();
    private boolean isSuccessfullyWritted = false;
    private boolean isSuccessfullyRead = false;

    private ImageView iv_copy_tag;
    private ImageView iv_paste_tag;
    private TextView tv_status;

    private Tag detectedTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy);
//        SharedPreferences pref = getSharedPreferences("groupnumber",MODE_PRIVATE);
//        cGroupNum = pref.getString("group_number","");
        iv_copy_tag = (ImageView) findViewById(R.id.iv_copy_tag);
        iv_paste_tag = (ImageView) findViewById(R.id.iv_paste_tag);
        tv_status = (TextView) findViewById(R.id.tv_status);
    }

    @Override
    public void onNewIntent(Intent intent) {
//        SharedPreferences pref = getSharedPreferences("certi", MODE_PRIVATE);
//        cText = pref.getString("certificate","");
//        System.out.println("The copied certification is : " + cText);

        //获取Tag对象
        detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String isEmpty = detectedTag == null? "true":"false";
        Log.d("detectedTag is empty?",isEmpty);
        analysisTag(detectedTag);
        if (detectedTag != null) {
            tv_status.setText("已检测到NFC标签，请在点击复制或粘贴按钮之前不要将手机远离NFC标签");
        } else {
            Toast.makeText(this,"NFC标签未探测成功，请将标签靠近手机NFC检测区域再次探测",Toast.LENGTH_SHORT).show();
            return;
        }
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

        iv_copy_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String isEmpty = detectedTag == null? "true":"false";
                Log.d("detectedTag is empty?",isEmpty);
                String decodeCerti = "";
                if (detectedTag != null) {
                    cText = readTag(detectedTag);
                    System.out.println("复制的证书为：" + cText);
                    try {
                        for (int i = 1;i < 8;i += 2) {
                            char a = cText.charAt(i);
                            String ele_decodeCerti = Character.toString(a);
                            decodeCerti += ele_decodeCerti;
                        }
                        System.out.print("解码复制的证书为：" + decodeCerti + "\n");
                    } catch (Exception e) {

                    }
                    SharedPreferences sharedPreferences = getSharedPreferences("decodecerti", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("decodeCerti",decodeCerti);
                    editor.apply();
                    if (isSuccessfullyRead) {
                        tv_status.setText("证书复制成功");
                    } else {
                        tv_status.setText("证书复制失败");
                    }
                }
            }
        });


        iv_paste_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String decodeCerti = "";
                SharedPreferences preferences = getSharedPreferences("decodecerti",MODE_PRIVATE);
                decodeCerti = preferences.getString("decodeCerti","");
                writeTag(detectedTag,decodeCerti);
                if (isSuccessfullyWritted) {
                    tv_status.setText("证书粘贴成功");
                } else {
                    tv_status.setText("证书粘贴失败");
                }
            }
        });



//        SharedPreferences pf = getSharedPreferences("Info", MODE_PRIVATE);
//        mlogo = pf.getString("Name", "");
//        mtype = pf.getString("Type", "");

        System.out.println("发给服务器的uid为：" + uid_server);
//        System.out.println("发给服务器的证书为：" + certi_server);

        certi_server = "";
        uid_server = "";
    }

    /**
     * 写非ndef格式数据
     * @param tag
     * @param num
     */
    public void writeTag(Tag tag, String num) {
        MifareUltralight ultralight = MifareUltralight.get(tag);
        try {
            ultralight.connect();
            ultralight.writePage(22, num.getBytes(Charset.forName("US-ASCII")));

            System.out.println("非ndef数据写入成功");
            isSuccessfullyWritted = true;
        } catch (Exception e) {
            System.out.println("非ndef写入异常");
            Toast.makeText(this,"NFC标签数据未成功，请将标签靠近手机NFC检测区域再次读取",Toast.LENGTH_SHORT).show();
            isSuccessfullyWritted = false;
        } finally {
            try {
                ultralight.close();
            } catch (Exception e) {
            }
        }
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
                isSuccessfullyRead = true;
                return certificate;
            } catch (Exception e) {
                e.printStackTrace();
                isSuccessfullyRead = false;
                Toast.makeText(this,"NFC标签数据未读取成功，请将标签靠近手机NFC检测区域再次读取",Toast.LENGTH_SHORT).show();
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
    /**
     * 读uid和状态位
     * @param tag
     */
    private void analysisTag(Tag tag) {
        String result = "";//uid
        if (tag != null) {
            MifareUltralight mifare = MifareUltralight.get(tag);
            try {
                mifare.connect();
                byte[] payload = mifare.readPages(0);
                for (int j = 0; j < 8; j++) {
                    temp = Integer.toHexString(payload[j] & 0xFF);
                    if (temp.length() == 1) {
                        temp = "0" + temp;
                    }
                    result += temp;
                }

                /**
                 * 得到发给服务器的uid
                 */
                for(int i = 0; i < 6; i++){
                    char a = result.charAt(i);
                    uid_server = uid_server + a;
                }
                for (int i = 8; i < 16; i++){
                    char a = result.charAt(i);
                    uid_server = uid_server + a;
                }

                temp = "";
                int move = 0x80;
                byte[] transceive = mifare.transceive(new byte[]{0x30, -128});
                for (int i = 0; i < 5; i++) {
                    if ((transceive[0] & move) == 0) temp += "0";
                    else temp += "1";
                    move = move >> 1;
                }

            }catch (Exception e){
                boolean isContains = Arrays.asList(Constant.UIDARRAY).contains(uid_server);
                int postion = 0;
                int uidCase = 0;
                if (isContains) {
                    for (int i = 0;i < Constant.UIDARRAY.length;i++) {
                        if (Constant.UIDARRAY[i].equals(uid_server)) {
                            postion = i + 1;
                        }
                    }
                    uidCase = postion % 32;
                    switch (uidCase) {
                        case 0:
                            temp = "00000";
                            break;
                        case 1:
                            temp = "00001";
                            break;
                        case 2:
                            temp = "00010";
                            break;
                        case 3:
                            temp = "00011";
                            break;
                        case 4:
                            temp = "00100";
                            break;
                        case 5:
                            temp = "00101";
                            break;
                        case 6:
                            temp = "00110";
                            break;
                        case 7:
                            temp = "00111";
                            break;
                        case 8:
                            temp = "01000";
                            break;
                        case 9:
                            temp = "01001";
                            break;
                        case 10:
                            temp = "01010";
                            break;
                        case 11:
                            temp = "01011";
                            break;
                        case 12:
                            temp = "01100";
                            break;
                        case 13:
                            temp = "01101";
                            break;
                        case 14:
                            temp = "01110";
                            break;
                        case 15:
                            temp = "01111";
                            break;
                        case 16:
                            temp = "10000";
                            break;
                        case 17:
                            temp = "10001";
                            break;
                        case 18:
                            temp = "10010";
                            break;
                        case 19:
                            temp = "10011";
                            break;
                        case 20:
                            temp = "10100";
                            break;
                        case 21:
                            temp = "10101";
                            break;
                        case 22:
                            temp = "10110";
                            break;
                        case 23:
                            temp = "10111";
                            break;
                        case 24:
                            temp = "11000";
                            break;
                        case 25:
                            temp = "11001";
                            break;
                        case 26:
                            temp = "11010";
                            break;
                        case 27:
                            temp = "11011";
                            break;
                        case 28:
                            temp = "11100";
                            break;
                        case 29:
                            temp = "11101";
                            break;
                        case 30:
                            temp = "11110";
                            break;
                        case 31:
                            temp = "11111";
                            break;
                    }
                } else {
                    temp = "";
                }
            }
            try {
                mifare.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("uid为：" + result);
            System.out.println("状态位为：" + temp);
        }
    }

}
