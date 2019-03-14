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
import android.widget.EditText;
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
    private String temp = "";
    private String mGoodsName;//保存物资名称
    private String mModel;
    private String mMaterial;
    private String mSn;
    private String mManufacturer;
    private String mProduceDate;
    private String num_zouyun = "";  //保存发送给服务器的证书
    private String uid_zouyun = ""; //保存发送给服务器的uid；
    private String applyUrl = Constant.URL_ADD_TAG;
    private ParseJson mParseJson = new ParseJson();


    private boolean aidisSuccessfullyWritted = false;
    private boolean certiisSuccessfullyWritted = false;
    private boolean idisSuccessfullyWritted = false;
    private String mUri = "d156000139://\033\001\000\001\000\001\000\001\000\001\000";//aid的uri

    private EditText TagTypeNumEt;
    private EditText TagIdEt;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 0x2017:
                    try {
                        String reponsedata = (msg.obj).toString();
                        int status = mParseJson.Json2TaggServer(reponsedata).getStatus();
//                        String message = mParseJson.Json2TaggServer(reponsedata).getMsg();
//                        String uid = mParseJson.Json2TaggServer(reponsedata).getTagg().getUid();
//                        String certificate = mParseJson.Json2TaggServer(reponsedata).getTagg().getCertificate();
//                        String goods_name = mParseJson.Json2TaggServer(reponsedata).getTagg().getGoods_name();
//                        String model = mParseJson.Json2TaggServer(reponsedata).getTagg().getModel();
//                        String material = mParseJson.Json2TaggServer(reponsedata).getTagg().getMaterial();
//                        String sn = mParseJson.Json2TaggServer(reponsedata).getTagg().getSn();
//                        String manufacturer = mParseJson.Json2TaggServer(reponsedata).getTagg().getManufacturer();
//                        String produce_date = mParseJson.Json2TaggServer(reponsedata).getTagg().getProduce_date();
                        if (status == 0) {
                            Toast.makeText(WriteTextActivity.this, "发证成功", Toast.LENGTH_SHORT).show();
//                            System.out.println("goods_name is ："
//                                    + goods_name
//                                    + "   "
//                                    + "material is : "
//                                    + material
//                                    + "    "
//                                    + "model is : "
//                                    + model
//                                    + "  "
//                                    + "sn is : "
//                                    + sn
//                                    + "  "
//                                    + "manufacturer is : "
//                                    + manufacturer
//                                    + " "
//                                    + "produce_date is : "
//                                    + produce_date
//                                    + " "
//                                    + "certificate is："
//                                    + certificate
//                                    + " "
//                                    + "uid is ："
//                                    + uid
//                                    +"\n");
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
        TagTypeNumEt = (EditText) findViewById(R.id.et_tagtypenum_input);
        TagIdEt = (EditText) findViewById(R.id.et_tagid_input);
    }
    @Override
    public void onNewIntent(Intent intent) {
        getNum();
        if (mText == null)
            return;
        //获取Tag对象
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (detectedTag == null) {
            Toast.makeText(this, "NFC标签未探测成功，请将标签靠近手机NFC检测区域再次探测", Toast.LENGTH_SHORT).show();
            return;
        }
        analysisTag(detectedTag);

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

        getDataFromMainActivity();
        writeTypeNumInTag(detectedTag,TagTypeNumEt.getText().toString());
        writeIdInTag(detectedTag,TagIdEt.getText().toString());
        writeTag(detectedTag, mText);

        if (certiisSuccessfullyWritted) {
            //写入NDEF数据(aid)
            NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{createUriRecord(mUri)});
            boolean result = writetag(ndefMessage, detectedTag);
            if (result) {
                Toast.makeText(this, "aid写入成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "aid写入失败", Toast.LENGTH_SHORT).show();
            }
        }


        /** 得到发给服务器的证书号*/
        try {
            for (int i = 0; i < 4; i++) {
                char a = mText.charAt(i);
                String mstr = "3" + a;
                num_zouyun = num_zouyun + mstr;
            }
        } catch (Exception e) {

        }

        System.out.println("拼接过后的证书为：" + num_zouyun);
        System.out.println("发给服务器的uid为：" + uid_zouyun);
        System.out.println("物资名称为：" + mGoodsName + "   " + "规格型号为：" + mModel + "    "
                + "材质为：" + mMaterial + " " + "位号/批号为：" + mSn + " " + "生产厂家为：" + mManufacturer + " " +
                "生产日期为：" + mProduceDate + " " + "证书为：" + num_zouyun + " " + "uid为：" + uid_zouyun + "    " + "  " + "状态位为：" + temp);

        sendingtoServer();
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
                    System.out.println("Tag is not writable");
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    System.out.println("Tag memory is out of size");
                    return false;
                }
                ndef.writeNdefMessage(message);
                System.out.println("aid successfully writted");
                return true;
            }
        } catch (Exception e) {
            return false;
        } finally {
            try {
                ndef.close();
            } catch (Exception e) {

            }
        }
        return false;
    }

    /**
     * 写非ndef格式数据
     */
    public void writeTag(Tag tag, String num) {
        MifareUltralight ultralight = MifareUltralight.get(tag);
        try {
            ultralight.connect();
            ultralight.writePage(22, num.getBytes(Charset.forName("US-ASCII")));
            System.out.println("非ndef数据写入成功");
            certiisSuccessfullyWritted = true;
        } catch (Exception e) {
            System.out.println("非ndef写入异常");
            Toast.makeText(this, "NFC标签未探测成功，请将标签靠近手机NFC检测区域再次探测", Toast.LENGTH_SHORT).show();
            certiisSuccessfullyWritted = false;
        } finally {
            try {
                ultralight.close();
            } catch (Exception e) {
            }
        }
    }

    public void writeTypeNumInTag(Tag tag, String typenum) {
        MifareUltralight ultralight = MifareUltralight.get(tag);
        try {
            ultralight.connect();
            if (typenum.length() == 1) {
                typenum = "0" + "0" + "0" + typenum;
            } else if (typenum.length() == 2) {
                typenum = "0" + "0" + typenum;
            }
            ultralight.writePage(19, typenum.getBytes(Charset.forName("US-ASCII")));
            System.out.println("非ndef数据写入成功");
            certiisSuccessfullyWritted = true;
        } catch (Exception e) {
            System.out.println("非ndef写入异常");
            Toast.makeText(this, "NFC标签未探测成功，请将标签靠近手机NFC检测区域再次探测", Toast.LENGTH_SHORT).show();
            certiisSuccessfullyWritted = false;
        } finally {
            try {
                ultralight.close();
            } catch (Exception e) {
            }
        }
    }

    public void writeIdInTag(Tag tag, String id) {
        MifareUltralight ultralight = MifareUltralight.get(tag);
        try {
            ultralight.connect();
            if (id.length() == 1) {
                id = "0" + "0" + "0" + id;
            } else if (id.length() == 2) {
                id = "0" + "0" + id;
            }
            ultralight.writePage(20, id.getBytes(Charset.forName("US-ASCII")));
            System.out.println("标签id写入成功");
            idisSuccessfullyWritted = true;
        } catch (Exception e) {
            System.out.println("标签id写入异常");
            Toast.makeText(this, "NFC标签未探测成功，请将标签靠近手机NFC检测区域再次探测", Toast.LENGTH_SHORT).show();
            idisSuccessfullyWritted = false;
        } finally {
            try {
                ultralight.close();
            } catch (Exception e) {
            }
        }
    }


    /**
     * 读uid和状态位
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

                /** 得到发给服务器的uid*/
                for (int i = 0; i < 6; i++) {
                    char a = result.charAt(i);
                    uid_zouyun = uid_zouyun + a;
                }
                for (int i = 8; i < 16; i++) {
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

            } catch (Exception e) {
                boolean isContains = Arrays.asList(Constant.UIDARRAY).contains(uid_zouyun);
                int postion = 0;
                int uidCase = 0;
                if (isContains) {
                    for (int i = 0; i < Constant.UIDARRAY.length; i++) {
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

    /**
     * 产生随机的4位数字证书
     */
    public void getNum() {
        int a0, a1, a2, a3;
        a0 = (int) (Math.random() * 10);
        a1 = (int) (Math.random() * 10);
        a2 = (int) (Math.random() * 10);
        a3 = (int) (Math.random() * 10);

        mText = Integer.toString(a0) + Integer.toString(a1) + Integer.toString(a2) + Integer.toString(a3);

        SharedPreferences sharedPreferences = getSharedPreferences("certi", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("certificate", mText);
        editor.apply();

        System.out.println("随机产生的证书为：" + mText);
    }

    public void getDataFromMainActivity() {
        SharedPreferences pref = getSharedPreferences("info", MODE_PRIVATE);
        mGoodsName = pref.getString("goods_name", "");
        mModel = pref.getString("model", "");
        mMaterial = pref.getString("material", "");
        mSn = pref.getString("sn", "");
        mManufacturer = pref.getString("manufacturer", "");
        mProduceDate = pref.getString("produce_date", "");
        if (mModel.equals("")) {
            mModel = "No model";
        }
        if (mMaterial.equals("")) {
            mMaterial = "No material";
        }
        if (mSn.equals("")) {
            mSn = "No sn";
        }
    }

    public void sendingtoServer() {
        if ((mGoodsName != "") && (mManufacturer != "") && (mProduceDate != "")
                && (num_zouyun.length() == 8) && (uid_zouyun.length() == 14)
                && certiisSuccessfullyWritted) {
            OkHttp okHttp = new OkHttp(getApplicationContext(), mHandler);
            RequestBody requestBody = new FormBody.Builder()
                    .add("uid",uid_zouyun)
                    .add("certificate",num_zouyun)
                    .add("goods_name",mGoodsName)
                    .add("model",mModel)
                    .add("material",mMaterial)
                    .add("sn",mSn)
                    .add("manufacturer",mManufacturer)
                    .add("produce_date",mProduceDate)
                    .build();
            okHttp.postFromInternet(applyUrl,requestBody);
        } else {
            Toast.makeText(WriteTextActivity.this, "发证类型缺失", Toast.LENGTH_SHORT).show();
        }
        num_zouyun = "";
        uid_zouyun = "";
    }
}



