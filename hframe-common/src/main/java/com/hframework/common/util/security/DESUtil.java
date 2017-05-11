package com.hframework.common.util.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.security.Security;

/**
 * 3DES 模式加密*
 * 加密方法DESede表示是3des加密方式<p>
 * 运算模式CBC,ECB。在CBC模式下使用key,向量iv;在ECB模式下仅使用key。<p>
 * 填充模式NoPadding、PKCS5Padding、SSL3Padding。<p>
 * 语言之间的兼容：<br>
 * 一个是C#采用CBC Mode，PKCS7 Padding,Java采用CBC Mode，PKCS5Padding Padding,<br>
 * 另一个是C#采用ECB Mode，PKCS7 Padding,Java采用ECB Mode，PKCS5Padding Padding,
 * 此段代码使用的CBC模式PKCS7Padding填充方式、用字节零填充，目的是匹配C#语言中CBC模式，zeros填充方式。
 */
public class DESUtil {

    private static final String ALGORITHM_CBC = "DESede/CBC/PKCS7Padding";
    private static final String ALGORITHM_ECB = "DESede/ECB/PKCS7Padding";
    private static final String DESEDE = "DESede";

    static {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());//添加PKCS7Padding支持
    }

    /**
     * 运用3DES CBC PKCS7Padding加密方式进行加密
     * @param data
     * @param key
     * @param ivSpec
     * @return
     * @throws Exception
     */
    public static byte[] des3EncodeCBC(byte[] data, byte[] key, IvParameterSpec ivSpec) throws Exception {
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DESEDE);
        Key desKey = keyFactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance(ALGORITHM_CBC);
        cipher.init(Cipher.ENCRYPT_MODE, desKey, ivSpec);
        return cipher.doFinal(data);
    }

    public static byte[] desDecodeCBC(byte[] data, byte[] key, IvParameterSpec ivSpec) throws Exception {
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DESEDE);
        Key desKey = keyFactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance(ALGORITHM_CBC);
        cipher.init(Cipher.DECRYPT_MODE, desKey, ivSpec);
        return cipher.doFinal(data);
    }

}
