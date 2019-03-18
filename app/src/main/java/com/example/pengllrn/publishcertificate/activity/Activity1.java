package com.example.pengllrn.publishcertificate.activity;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pengllrn.publishcertificate.R;
import com.example.pengllrn.publishcertificate.base.BaseNfcActivity;
import com.example.pengllrn.publishcertificate.bean.Tagg;
import com.example.pengllrn.publishcertificate.constant.Constant;
import com.example.pengllrn.publishcertificate.gson.ParseJson;
import com.example.pengllrn.publishcertificate.internet.OkHttp;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class Activity1 extends BaseNfcActivity {

    private String certificate1 = ""; //证书
    private String temp = "";   //保存状态位
    private String uid_zouyun = ""; //保存uid
    private String applyUrl = Constant.URL_ADD_TAG;
    private String applyUrl2 = Constant.URL_CHECK_IN_STORAGE;
    private ParseJson mParseJson = new ParseJson();
    private String outStorageTime;
    private boolean isinStorageTimeExist = false;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x2017:
                    try {
                        String reponsedata = (msg.obj).toString();
                        int status = mParseJson.Json2TaggServer(reponsedata).getStatus();
                        if (status == 0) {
                            String in_storage = mParseJson.Json2TaggServer(reponsedata).getTagg().getIn_storage();
                            if (in_storage.equals("0")) {
                                Toast.makeText(Activity1.this,"出库成功",Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(Activity1.this,"出库失败",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 0x2018:
                    try {
                        String responsedata = (msg.obj).toString();
                        int status = mParseJson.Json2TaggServer(responsedata).getStatus();
                        if (status == 0) {
                            String date_in_storage = mParseJson.Json2TaggServer(responsedata).getTagg().getDate_in_storage();
                            String in_storage = mParseJson.Json2TaggServer(responsedata).getTagg().getIn_storage();
                            if (date_in_storage.equals("")) {
                                Toast.makeText(Activity1.this,"该物品尚未入库，请先进行入库操作",Toast.LENGTH_SHORT).show();
                                uid_zouyun = "";
                                certificate1 = "";
                            } else if (in_storage.equals("0")){
                                Toast.makeText(Activity1.this,"该物品已出库",Toast.LENGTH_SHORT).show();
                                uid_zouyun = "";
                                certificate1 = "";
                            } else {
                                sendtoServer();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(Activity1.this,"出库失败",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 0x2020:
                    try {
                        String responsedata = (msg.obj).toString();
                        List<Tagg> listtaginstorage = mParseJson.TaggPoint(responsedata);
                        if (listtaginstorage.size() == 0) {
                            Toast.makeText(Activity1.this,"库中已空，请进行入库操作",Toast.LENGTH_SHORT).show();
                            uid_zouyun = "";
                            certificate1 = "";
                        } else {
                            sendtoServer2();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 0x22:
                    Toast.makeText(Activity1.this,"网络延迟",Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);
    }

    @Override
    public void onNewIntent(Intent intent) {
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
        certificate1 = readCertiInTag(detectedTag);
        analysisTag(detectedTag);
        Calendar calendar = Calendar.getInstance();
        outStorageTime = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-"
                + calendar.get(Calendar.DAY_OF_MONTH);
        System.out.println("证书：" + certificate1 + "     " + "uid: " + uid_zouyun + " " + "出库时间：" + outStorageTime);
        sendtoServer3();
    }



    /**
     * 读取非ndef数据
     *
     * @param tag
     */
    public String readCertiInTag(Tag tag) {
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
                Toast.makeText(Activity1.this,"NFC标签数据未读取成功，请将标签靠近手机NFC检测区域再次读取",Toast.LENGTH_SHORT).show();
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

    /** 读uid和状态位*/
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

                /** 得到发给服务器的uid*/
                for(int i = 0; i < 6; i++){
                    char a = result.charAt(i);
                    uid_zouyun = uid_zouyun + a;
                }
                for (int i = 8; i < 16; i++){
                    char a = result.charAt(i);
                    uid_zouyun = uid_zouyun + a;
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
                boolean isContains = Arrays.asList(Constant.UIDARRAY).contains(uid_zouyun);
                int postion = 0;
                int uidCase = 0;
                if (isContains) {
                    for (int i = 0;i < Constant.UIDARRAY.length;i++) {
                        if (Constant.UIDARRAY[i].equals(uid_zouyun)) {
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
                        default:
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

    public void sendtoServer() {
        if ((certificate1.length() == 8) && (uid_zouyun.length() == 14)) {
            OkHttp okHttp = new OkHttp(getApplicationContext(),mHandler);
            RequestBody requestBody = new FormBody.Builder()
                    .add("uid",uid_zouyun)
                    .add("certificate",certificate1)
                    .add("in_storage",Constant.OUTSTORAGE)
                    .add("date_out_storage",outStorageTime)
                    .build();
            okHttp.postFromInternet(applyUrl,requestBody);
        } else {
            Toast.makeText(this,"uid或者证书格式不正确",Toast.LENGTH_SHORT).show();
        }
        uid_zouyun = "";
        certificate1 = "";
    }
    public void sendtoServer2() {
        if ((certificate1.length() == 8) && (uid_zouyun.length() == 14)) {
            OkHttp okHttp = new OkHttp(getApplicationContext(),mHandler);
            RequestBody requestBody = new FormBody.Builder()
                    .add("uid",uid_zouyun)
                    .add("certificate",certificate1)
                    .build();
            okHttp.postFromInternet2(applyUrl,requestBody);
        }
    }
    public void sendtoServer3() {
        OkHttp okHttp = new OkHttp(getApplicationContext(),mHandler);
        okHttp.getFromInternet(applyUrl2);
    }
}
