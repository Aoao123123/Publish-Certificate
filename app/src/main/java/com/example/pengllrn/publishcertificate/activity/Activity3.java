package com.example.pengllrn.publishcertificate.activity;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pengllrn.publishcertificate.R;
import com.example.pengllrn.publishcertificate.base.BaseNfcActivity;
import com.example.pengllrn.publishcertificate.constant.Constant;

import java.io.IOException;
import java.util.Arrays;

public class Activity3 extends BaseNfcActivity {

    private String certificate1 = ""; //证书
    private String certificate2 = "";
    private TextView certi1;
    private TextView certi2;
    private TextView compareResult;
    private Button dataClear;
    //    private boolean flag = false;
    private String temp = "";   //保存状态位
    private String uid_zouyun = ""; //保存uid

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);

    }

    @Override
    public void onNewIntent(Intent intent) {
        uid_zouyun = "";
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
        certificate1 = readTag(detectedTag);
        analysisTag(detectedTag);
        System.out.println("证书：" + certificate1 + "     " + "uid: " + uid_zouyun);
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
                Toast.makeText(Activity3.this,"NFC标签数据未读取成功，请将标签靠近手机NFC检测区域再次读取",Toast.LENGTH_SHORT).show();
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
}
