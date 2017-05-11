//***************************************************************************
/**
 * @类名称:Security.java
 * @处理内容:安全相关（加密/解密）
 */
// ***************************************************************************
package com.hframework.common.util.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtils {

    public static final String ALGORITHM_DES = "DESede";

    public static final String ALGORITHM_SHA1 = "SHA-1";

    public static final String ALGORITHM_MD5 = "MD5";

    /**
     * 静态成员初始化,BASE64编码表
     */
    final private static String encodeTable = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    /**
     * BASE64编码器,编码算法如下: 1.将数据按3个字节一组分成数块； 2.每块将3个8位的数据转换成4个6位数据段； 11111111
     * 00000000 11111111 ---- 111111 110000 000011 111111 12345678 12345678
     * 12345678 ---- 123456 781234 567812 345678 3.根据Base64字符表得到4个6位数据段对应的字符；
     * 4.如果最后一块只有两个字节，则添加两个0位，转换成对应Base64字符表的三个字符，并在结尾添一个'='字符；
     * 如果最后一块只有一个字节，则添加四个0位，转换成对应Base64字符表的两个字符，并在结尾添两个'='字符。
     *
     * @param data 需要编码的数据
     * @return 返回编码后结果
     */
    public static String BASE64Encode(byte[] data) {
        // 判断输入数据的有效性
        if (data == null) {
            return null;
        }
        // 声明保存结构的缓冲区
        StringBuffer encoded = new StringBuffer();
        // 循环开始编码
        int i, individual, remain = 0;
        for (i = 0; i < data.length; i++) {
            // 把BYTE数据类型转换成int
            individual = data[i] & 0x000000ff;
            switch (i % 3) {
                case 0:
                    // 保存编码数据
                    encoded.append(encodeTable.charAt(individual >> 2));
                    // 保留需移动的位
                    remain = (individual << 4) & 0x30;
                    break;
                case 1:
                    // 保存编码数据
                    encoded.append(encodeTable.charAt(remain | individual >> 4));
                    // 保留需移动的位
                    remain = (individual << 2) & 0x3c;
                    break;
                case 2:
                    // 保存编码数据
                    encoded.append(encodeTable.charAt(remain | individual >> 6));
                    // 保存编码数据
                    encoded.append(encodeTable.charAt(individual & 0x3f));
                    break;
            }
            // 判断换行
            if (((i + 1) % 57) == 0)
                encoded.append("\r\n");
        }
        // 末尾补位
        switch (i % 3) {
            case 1:
                // 补四个0位，生成两个Base64字符,末尾加==
                encoded.append(encodeTable.charAt(remain));
                encoded.append("==");
                break;
            case 2:
                // 补两个0位，生成三个Base64字符,末尾加=
                encoded.append(encodeTable.charAt(remain));
                encoded.append('=');
                break;
        }
        // 返回结果
        return encoded.toString();
    }

    /**
     * BASE64解码,解码算法如下 1.将数据按4个字节一组分成数块； 2.每块将4个字符去掉最高两位并转换成3个8位的数据段；
     * 注意：数据块中的字符取值不是ASCII集的值，而是Base64字符表中相对应的索引值！ 00 111111 00 110000 00 000011
     * 00 111111 ---- 11111111 00000000 11111111 3.根据ASCII字符集得到3个8位数据段对应的字符；
     * 4.如果最后一块只有两个'='，去掉两个'='，并去掉最低两位，转换成对应ASSCII字符集的两个字符；
     * 如果最后一块只有一个'='，去掉'='，并去掉最低四位，转换成对应ASSCII字符集的一个字符。
     *
     * @param encodedData 需要解码的数据
     * @return 解码后的数据
     */
    public static byte[] BASE64Decode(String encodedData) {
        byte[] data = encodedData.getBytes();
        // 分配解码后数据缓冲区
        ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
        // 循环解码,每四个字节分为一个处理数据块
        int nBlockIndex = 0;
        byte individual = 0, remain = 0;
        for (int i = 0; i < data.length; i++) {
            // 过滤回车换行符
            if (data[i] == '\r' || data[i] == '\n')
                continue;
            // 如果到达'=',处理完成
            if (data[i] == '=')
                break;
            // 读取一个块字符
            individual = (byte) decodeBase64Char((char) data[i]);
            // 针对在块的不同位置，分别处理
            switch ((nBlockIndex++) % 4) {
                case 0:
                    remain = (byte) (individual << 2);
                    break;
                case 1:
                    output.write((char) (remain | (individual >> 4)));
                    remain = (byte) (individual << 4);
                    break;
                case 2:
                    output.write((char) (remain | (individual >> 2)));
                    remain = (byte) (individual << 6);
                    break;
                case 3:
                    output.write(remain | individual);
                    break;
            }
        }
        // 返回解码后结果
        return output.toByteArray();
    }

    /**
     * BASE64字符与其索引反解
     *
     * @param code 编码字符
     * @return 索引
     */
    private static byte decodeBase64Char(char code) {
        if (code >= 'A' && code <= 'Z')
            return (byte) (code - 'A');
        else if (code >= 'a' && code <= 'z')
            return (byte) (code - 'a' + 26);
        else if (code >= '0' && code <= '9')
            return (byte) (code - '0' + 52);
        else if (code == '+')
            return 62;
        else if (code == '/')
            return 63;
        return 64;
    }

    /**
     * <p>
     * 用BASE64对字符串进行加密
     * </P>
     *
     * @param str 需要加密的字符串
     * @return String 加密后的字符串
     */
    public static String BASE64EncodeString(String str) {
        sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
        return encoder.encodeBuffer(str.getBytes()).trim();
    }

    /**
     * <p>
     * 用BASE64对字符串进行加密
     * </P>
     *
     * @param str 需要加密的字符串
     * @return String 加密后的字符串
     */
    public static String BASE64EncodeString(byte[] str) {
        sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
        return encoder.encodeBuffer(str).trim();
    }

    /**
     * <p>
     * 对用BASE64加密的字符串进行解密
     * </P>
     *
     * @param str 需要解密的字符串
     * @return String 解密后的字符串
     */
    public static String BASE64DecodeString(String str) {
        sun.misc.BASE64Decoder dec = new sun.misc.BASE64Decoder();
        try {
            return new String(dec.decodeBuffer(str));
        } catch (IOException io) {
            throw new RuntimeException(io.getMessage(), io.getCause());
        }
    }

    /**
     * 用MD5进行加密
     */
    public static String MD5DecodeString(String str) {
        return decodeString(str, ALGORITHM_MD5);
    }

    /**
     * 用SHA进行加密
     */
    public static String SHADecodeString(String str) {
        return decodeString(str, ALGORITHM_SHA1);
    }

    /**
     * 用MD5进行加密
     */
    public static byte[] MD5DecodeByte(byte[] str) {
        return decodeByte(str, ALGORITHM_MD5);
    }

    /**
     * 用SHA进行加密
     */
    public static byte[] SHADecodeByte(byte[] str) {
        return decodeByte(str, ALGORITHM_SHA1);
    }

    private static String decodeString(String str, String method) {
        MessageDigest md = null;
        String dstr = null;
        try {
            // 生成一个MD5加密计算摘要
            md = MessageDigest.getInstance(method);
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。
            // 因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            dstr = new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return dstr;
    }

    private static byte[] decodeByte(byte[] strSrc, String method) {
        byte[] returnByte = null;
        MessageDigest md = null;
        try {
            // 生成一个MD5加密计算摘要
            md = MessageDigest.getInstance(method);
            md.update(strSrc);
            // digest()最后确定返回md5 hash值，返回值为8为字符串。
            // 因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            byte[] e = md.digest();

            // 接口定义中密钥长度为24位，补0
            returnByte = new byte[24];
            int i = 0;
            while (i < e.length && i < 24) {
                returnByte[i] = e[i];
                i++;
            }

            if (i < 24) {
                returnByte[i] = 0;
                i++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnByte;
    }

    /**
     * 用3DES算法进行加密
     * @param str 源字符串
     * @param strKey 密钥字符串
     * @return
     */
    public static byte[] encode3DES(String str, String strKey) {
        try {
            byte[] byteKey = strKey.getBytes().length == 24 ? strKey.getBytes() : build3DesKey(strKey);
            SecretKey desKey = new SecretKeySpec(byteKey, ALGORITHM_DES);    //生成密钥
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            cipher.init(Cipher.ENCRYPT_MODE, desKey);
            return cipher.doFinal(str.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用3DES算法进行解密
     * @param bytes 源二进制数组
     * @param strKey 密钥字符串
     * @return
     */
    public static byte[] decode3DES(byte[] bytes, String strKey) {
        try {
            byte[] byteKey = strKey.getBytes().length == 24 ? strKey.getBytes() : build3DesKey(strKey);
            SecretKey desKey = new SecretKeySpec(byteKey, ALGORITHM_DES);    //生成密钥
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            cipher.init(Cipher.DECRYPT_MODE, desKey);
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据字符串生成密钥字节数组
     *
     * @param keyStr 密钥字符串
     * @return
     * @throws UnsupportedEncodingException
     */
    public static byte[] build3DesKey(String keyStr) throws UnsupportedEncodingException {
        byte[] key = new byte[24];    //声明一个24位的字节数组，默认里面都是0
        byte[] temp = keyStr.getBytes();    //将字符串转成字节数组
        //执行数组拷贝 System.arraycopy(源数组，从源数组哪里开始拷贝，目标数组，拷贝多少位)
        if (key.length > temp.length) {
            //如果temp不够24位，则拷贝temp数组整个长度的内容到key数组中
            System.arraycopy(temp, 0, key, 0, temp.length);
        } else {
            //如果temp大于24位，则拷贝temp数组24个长度的内容到key数组中
            System.arraycopy(temp, 0, key, 0, key.length);
        }
        return key;
    }

    public static void main(String[] args) throws Exception {
        //vxmMRYptCNU=
        //Hello
        String key = "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4";
        String msg = "3DES加密解密案例";
        //加密
        byte[] secretArr = SecurityUtils.encode3DES(msg, key);
        String temp = SecurityUtils.BASE64Encode(secretArr);
        System.out.println("【加密后】：" + temp);
        //解密
        byte[] myMsgArr = SecurityUtils.decode3DES("vxmMRYptCNU".getBytes(), key);
        System.out.println("【解密后】：" + new String(myMsgArr));
    }

    public static String getMD5(String sourceStr, int length) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            if (length == 32){
                result = buf.toString();
            }else {
                result = buf.toString().substring(8, 24);
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

}
