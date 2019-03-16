package com.example.pengllrn.publishcertificate.utils;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;

/**
 * Created by pengllrn on 2019/1/4.
 */

public class NAFVerifyHelper {

    public static int nfcDetected(Context context) {
        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (mNfcAdapter == null)
            return 1;
        return mNfcAdapter.isEnabled() ? 0 : 2;
    }

    public static boolean checkNfcData(Intent intent) {
        if (intent != null && intent.getParcelableExtra(NfcAdapter.EXTRA_TAG) != null) {
            return true;
        }
        return false;
    }

    public static Tag getNfcData(Intent intent) {
        if (checkNfcData(intent)) {
            return intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
        return null;
    }

    public static byte[] makeAID(int type, int sub1, int sub2, int vendor, int brand) {
        byte[] aid = new byte[]{-47, 86, 0, 1, 57, 27, (byte) type, (byte) (sub1 >> 8 & 255), (byte) (sub1 & 255), (byte) (sub2 >> 8 & 255), (byte) (sub2 & 255), (byte) (vendor >> 8 & 255), (byte) (vendor & 255), (byte) (brand >> 8 & 255), (byte) (brand & 255), 0};
        return aid;
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src != null && src.length > 0) {
            for (int i = 0; i < src.length; ++i) {
                int v = src[i] & 255;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    builder.append(0);
                }
                builder.append(hv);
            }
            return builder.toString();
        } else {
            return null;
        }
    }


    public static byte[] parse(byte[] data) {
        byte[] aid = new byte[16];
        byte index = 0;
        int var5 = index + 1;
        aid[index] = -47;
        aid[var5++] = 86;
        aid[var5++] = 0;
        aid[var5++] = 1;
        aid[var5++] = 57;
        int count = 13;

        while (var5 < 16 && count < data.length) {
            if (ISASCII(data[count]) && ISASCII(data[count + 1])) {
                byte value = (byte) (DIGIT(data[count]) << 4 | DIGIT(data[count + 1]) & 15);
                aid[var5++] = value;
                count += 2;
            } else {
                aid[var5++] = data[count];
                ++count;
            }
        }

        return aid;
    }

    public static boolean ISASCII(byte value) {
        return value >= 48 && value <= 57 || value >= 97 && value <= 102 || value >= 65 && value <= 70;
    }

    public static byte DIGIT(byte value) {
        return value >= 48 && value <= 57 ? (byte) (value - 48) : (value >= 97 && value <= 102 ? (byte) (value - 97 + 10) : (value >= 65 && value <= 70 ? (byte) (value - 65 + 10) : 0));
    }

    public static String getTagTid(Tag tag) {
        String tid = null;
        if (tag != null) {
            byte[] b = tag.getId();
            byte[] temp = new byte[8];
            for (int i = 0; i < b.length; i++) {
                temp[i] = b[i];
            }
            temp[7] = 0;
            tid = bytesToHexString(temp);
        }
        return tid;
    }
}
