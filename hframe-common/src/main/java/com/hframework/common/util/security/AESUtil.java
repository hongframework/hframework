package com.hframework.common.util.security;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Aes加密解密工具类
 *
 * Created by zqh on 15-3-25.
 */
public class AESUtil {


    public static final String VIPARA = "0102030405060708";
    public static final String encode = "GBK";

    private static Logger logger = Logger.getLogger(AESUtil.class);

    /**
     * 加密
     *
     * @param content  待加密内容
     * @param password 加密密钥
     * @return
     */
    public static String encrypt(String password, String content)
            throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
        SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
        byte[] encryptedData = cipher.doFinal(content.getBytes(encode));

        return Base64Utils.encode(encryptedData);
    }

    /**
     * 解密
     *
     * @param content  待解密内容
     * @param password 解密密钥
     * @return
     */
    public static String decrypt(String password, String content)
            throws Exception {
        byte[] byteMi = Base64Utils.decode(content);
        IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
        SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
        byte[] decryptedData = cipher.doFinal(byteMi);

        return new String(decryptedData, encode);
    }


    /**
     * 方宽aes加密
     *
     * @param content  待加密内容
     * @param password 加密密钥
     * @return
     */
    public static String fangkuanEncrypt(String password, String content)
            throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
        SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
        byte[] encryptedData = cipher.doFinal(content.getBytes(encode));
        return new String(Base64.encodeBase64(encryptedData));
    }

    /**
     * 解密
     *
     * @param content  待解密内容
     * @param password 解密密钥
     * @return
     */
    public static String fangkuanDecrypt(String password, String content)
            throws Exception {
        byte[] byteMi = Base64.decodeBase64(content);
        IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
        SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
        byte[] decryptedData = cipher.doFinal(byteMi);

        return new String(decryptedData, encode);
    }


    public static void main(String args[]){
        try {

            //String str = decrypt("ourangel88888888","si7PTEGS/0Zrms85juZwqqYXC9aiMRXsJJkDCaHL4vE61gYum6ww4/CesG6dXu2VZ55T1XVzTPftTOk/Q/eBNBvKSBa/nL4vF2R7F/IJPnoXveiv08mtECjnlRkzC9q5hgBPUcA0LUAjUCCLvo Ql0f4cQjUy3aCeHzQRZaANZI=");
            //System.out.println(str);
            /*String res = fangkuanEncrypt("ourangel88888888", "{\"account\":\"158\",\"phone\":\"13800001126\",\"identityCard\":\"\",\"expressOrg\":\"1\",\"staffNumber\":\"\",\"state\":\"1\",\"isNew\":\"1\"}");
            System.out.println(res);
            String result = fangkuanDecrypt("ourangel88888888", "lym/j5sr3lyFZa7yObaSYXxdqVY6V1zzBsQqVP4A5k/s3qDWYzw8iuU30a5TqTBeAIvQ8RZLf/mm8C+MJOCp1kcWMRwMxfL7jKnoVXAkrFdpdoAG8/SauG5Hnmlcpjg5ibC0XL0A8zXYB8i2uEPrkSEmBL2NwqODfDz0PX+Woq0=");
            System.out.println(result);
            String str = "uMFNJB7ygxPmywe8OWU2yrgA0gmHe0v9xkgQP4cOWf1lcrMJZK2zX%2FRO%2BClzv96SdsJjLkJOyZ4NLFi%2BNlT982bsbjNuGOjT6L95ueEA9mYIkHZVk3lA1WfAJOMpZPF7nyN9FDeqpI%2FWJxj%2FqesSf01grGxHzD64vsmbyMExbQY%3D";
            System.out.println(URLDecoder.decode(str, "UTF-8"));*/
            String resu = SecurityUtils.MD5DecodeString("lym/j5sr3lyFZa7yObaSYXxdqVY6V1zzBsQqVP4A5k/s3qDWYzw8iuU30a5TqTBeAIvQ8RZLf/mm8C+MJOCp1kcWMRwMxfL7jKnoVXAkrFdpdoAG8/SauG5Hnmlcpjg5ibC0XL0A8zXYB8i2uEPrkSEmBL2NwqODfDz0PX+Woq0=20150504000001");
            logger.info("resu+"+resu);
            System.out.println(resu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
