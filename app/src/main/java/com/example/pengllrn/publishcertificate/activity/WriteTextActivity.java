package com.example.pengllrn.publishcertificate.activity;

import android.content.Intent;
import android.content.SharedPreferences;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;

import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.pengllrn.publishcertificate.R;
import com.example.pengllrn.publishcertificate.base.BaseNfcActivity;
import com.example.pengllrn.publishcertificate.constant.Constant;
import com.example.pengllrn.publishcertificate.gson.ParseJson;
import com.example.pengllrn.publishcertificate.internet.OkHttp;
import com.example.pengllrn.publishcertificate.utils.UriPrefix;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import okhttp3.FormBody;
import okhttp3.RequestBody;


/**
 * Created by pengllrn on 2019/1/4.
 */

public class WriteTextActivity extends BaseNfcActivity {
    private String mText = "";  //保存证书
    private Intent intent;
    private String temp = "";
    private String mlogo;   //保存品牌
    private String mtype;   //保存随机产生的四位随机数
    private String num_zouyun = "";  //保存发送给服务器的证书
    private String uid_zouyun = ""; //保存发送给服务器的uid；
    private String groupNum = "";//保存双证组号
    private String applyUrl = Constant.URL_ADD_TAG;
    private ParseJson mParseJson = new ParseJson();
    private boolean isSuccessfullyWritted = false;
    private int publishNum = 0;//发证的次数
    private int maxPublishNum = 0;//最大发证次数
    private String mUri = "d156000139://\033\001\000\001\000\001\000\001\000\001\000";//aid的uri

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 0x2017:
                    try {
                        String reponsedata = (msg.obj).toString();
                        int status = mParseJson.Json2TaggServer(reponsedata).getStatus();
                        String message = mParseJson.Json2TaggServer(reponsedata).getMsg();
                        String uid = mParseJson.Json2TaggServer(reponsedata).getTagg().getUid();
                        String certificate = mParseJson.Json2TaggServer(reponsedata).getTagg().getCertificate();
                        String obflag = mParseJson.Json2TaggServer(reponsedata).getTagg().getObflag();
                        String brand = mParseJson.Json2TaggServer(reponsedata).getTagg().getBrand();
                        String group_number = mParseJson.Json2TaggServer(reponsedata).getTagg().getGroup_number();
                        if (status == 0) {
                            publishNum += 1;
                            Toast.makeText(WriteTextActivity.this, "发证成功", Toast.LENGTH_SHORT).show();
                            System.out.println("brand is ："
                                    + brand
                                    + "   "
                                    + "Group Number is ："
                                    + group_number
                                    + "    "
                                    + "certificate is："
                                    + certificate
                                    + " "
                                    + "uid is ："
                                    + uid + "    "
                                    + "  "
                                    + "obflag is ："
                                    + obflag
                                    +"\n");
                        }
                    } catch (Exception e) {
                        Toast.makeText(WriteTextActivity.this,"NFC标签未探测成功，请将标签靠近手机NFC检测区域再次探测",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 0x22:
                    Toast.makeText(WriteTextActivity.this,"网络请求延迟，发证失败",Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_text);
        SharedPreferences preferences = getSharedPreferences("info",MODE_PRIVATE);
        maxPublishNum = preferences.getInt("publishNum",0);
        getGroupNum();
    }
    @Override
    public void onNewIntent(Intent intent) {
        if (publishNum >= maxPublishNum) {
            Toast.makeText(this,"超过最大发证次数限制",Toast.LENGTH_SHORT).show();
            return;
        }
        getNum();
        if (mText == null)
            return;
        //获取Tag对象
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (detectedTag == null) {
            Toast.makeText(this,"NFC标签未探测成功，请将标签靠近手机NFC检测区域再次探测",Toast.LENGTH_SHORT).show();
            return;
        }
        analysisTag(detectedTag);

        //写入NDEF数据(aid)
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{createUriRecord(mUri)});
        boolean result = writetag(ndefMessage, detectedTag);
        if (result) {
            Toast.makeText(this, "aid写入成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "aid写入失败", Toast.LENGTH_SHORT).show();
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
        writeTag(detectedTag, mText);


        SharedPreferences pref = getSharedPreferences("info", MODE_PRIVATE);
        mlogo = pref.getString("Name", "");
        mtype = pref.getString("Type", "");

        /** 得到发给服务器的证书号*/
        try {
            for(int i = 0; i < 4; i++){
                char a = mText.charAt(i);
                String mstr = "3" + a;
                num_zouyun = num_zouyun + mstr;
            }
        } catch (Exception e) {

        }

        System.out.println("拼接过后的证书为：" + num_zouyun);
        System.out.println("发给服务器的uid为：" + uid_zouyun);
        System.out.println("品牌为：" + mlogo + "   " + "发证类型为：" + mtype + "    " + "证书为：" + num_zouyun
                + " " + "uid为：" + uid_zouyun + "    " + "  " + "状态位为：" + temp);

        if((mlogo != "") && (mtype != "") && (maxPublishNum != 0) && (num_zouyun.length() == 8) && (uid_zouyun.length() == 14) && isSuccessfullyWritted)
        {
            OkHttp okHttp = new OkHttp(getApplicationContext(),mHandler);
            /**
             * 双证网络数据发送
             */
            if (mtype.equals("双证")) {
                System.out.println("Dou-certi group number is " + groupNum);
                /**
                 * 双证有状态位网络数据发送
                 */
                if (temp != "") {
                    RequestBody requestBody = new FormBody.Builder()
                            .add("uid",uid_zouyun)
                            .add("certificate",num_zouyun)
                            .add("obflag",temp)
                            .add("brand",mlogo)
                            .add("group_number",groupNum)
                            .build();
                    okHttp.postFromInternet(applyUrl,requestBody);
                    /**
                     * 双证无状态位网络数据发送
                     */
                } else {
                    RequestBody requestBody = new FormBody.Builder()
                            .add("uid",uid_zouyun)
                            .add("certificate",num_zouyun)
                            .add("brand",mlogo)
                            .add("group_number",groupNum)
                            .build();
                    okHttp.postFromInternet(applyUrl,requestBody);
                }
                /**
                 * 单证网络数据发送
                 */
            } else {
                groupNum = "-1";
                /**
                 * 单证有状态位网络数据发送
                 */
                if (temp != "") {
                    RequestBody requestBody = new FormBody.Builder()
                            .add("uid",uid_zouyun)
                            .add("certificate",num_zouyun)
                            .add("obflag",temp)
                            .add("brand",mlogo)
                            .add("group_number",groupNum)
                            .build();
                    okHttp.postFromInternet(applyUrl,requestBody);
                    /**
                     * 单证无状态位网络数据发送
                     */
                } else {
                    RequestBody requestBody = new FormBody.Builder()
                            .add("uid",uid_zouyun)
                            .add("certificate",num_zouyun)
                            .add("brand",mlogo)
                            .add("group_number",groupNum)
                            .build();
                    okHttp.postFromInternet(applyUrl,requestBody);
                }
            }
        }else{
            Toast.makeText(WriteTextActivity.this, "请选择品牌,单双证类型", Toast.LENGTH_SHORT).show();
        }

        num_zouyun = "";
        uid_zouyun = "";
    }

    /**
     * 将Uri转成NdefRecord
     *
     * @param uriStr
     * @return
     */
    public static NdefRecord createUriRecord(String uriStr) {
        byte prefix = 0;
        for (Byte b : UriPrefix.URI_PREFIX_MAP.keySet()) {
            String prefixStr = UriPrefix.URI_PREFIX_MAP.get(b).toLowerCase();
            if ("".equals(prefixStr))
                continue;
            if (uriStr.toLowerCase().startsWith(prefixStr)) {
                prefix = b;
                uriStr = uriStr.substring(prefixStr.length());
                break;
            }
        }
        byte[] data = new byte[1 + uriStr.length()];
        data[0] = prefix;
        System.arraycopy(uriStr.getBytes(), 0, data, 1, uriStr.length());
        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], data);
        return record;
    }

    /**
     * 写入ndef数据(aid)
     *
     * @param message
     * @param tag
     * @return
     */
    public static boolean writetag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;
        Ndef ndef = Ndef.get(tag);
        try {
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    return false;
                }
                ndef.writeNdefMessage(message);
                return true;
            }
        } catch (Exception e) {
        } finally {
            try {
                ndef.close();
            } catch (Exception e) {

            }
        }
        return false;
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
            isSuccessfullyWritted = true;
        } catch (Exception e) {
            System.out.println("非ndef写入异常");
            Toast.makeText(this,"NFC标签未探测成功，请将标签靠近手机NFC检测区域再次探测",Toast.LENGTH_SHORT).show();
            isSuccessfullyWritted = false;
        } finally {
            try {
                ultralight.close();
            } catch (Exception e) {
            }
        }
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

    /** 产生随机的4位数字证书*/
    public void getNum(){
        int a0, a1, a2, a3;
        a0 = (int) (Math.random() * 10);
        a1 = (int) (Math.random() * 10);
        a2 = (int) (Math.random() * 10);
        a3 = (int) (Math.random() * 10);

        mText = Integer.toString(a0) + Integer.toString(a1) + Integer.toString(a2) + Integer.toString(a3);

        SharedPreferences sharedPreferences = getSharedPreferences("certi", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("certificate",mText);
        editor.apply();

        System.out.println("随机产生的证书为：" + mText);
    }

    public void getGroupNum() {
        int a0, a1, a2, a3, a4, a5, a6, a7;
        a0 = (int) (Math.random() * 10);
        a1 = (int) (Math.random() * 10);
        a2 = (int) (Math.random() * 10);
        a3 = (int) (Math.random() * 10);
        a4 = (int) (Math.random() * 10);
        a5 = (int) (Math.random() * 10);
        a6 = (int) (Math.random() * 10);
        a7 = (int) (Math.random() * 10);

        groupNum = Integer.toString(a0) + Integer.toString(a1) + Integer.toString(a2) +
                Integer.toString(a3) + Integer.toString(a4) + Integer.toString(a5) +
                Integer.toString(a6) + Integer.toString(a7);

//        SharedPreferences sharedPreferences = getSharedPreferences("groupnumber", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("group_number",groupNum);
//        editor.apply();
    }
}
