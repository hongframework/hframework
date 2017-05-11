package com.hframework.common.util.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by zhangquanhong on 2016/4/17.
 */
public class MD5Util {


//    /**
//     * 加密
//     *
//     * @param content  待加密内容
//     * @return
//     */
//    public static String encrypt(String content) {
//        return SecurityUtils.MD5DecodeString(content);
//    }


    public static String encrypt(String strObj) {
        String resultString = null;
        try {
            resultString = new String(strObj);
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteToString(md.digest(strObj.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString;
    }

    // exchange byte array to hex string
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }

    // global array
    private final static String[] strDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    // return digits and string
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }

}
