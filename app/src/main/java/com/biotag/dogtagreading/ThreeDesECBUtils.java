package com.biotag.dogtagreading;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

public class ThreeDesECBUtils {
    //
    public final static String base64secretKey = "28AAAAEFGGIIIIIIIJLLNNNNOPPQTUUY";
    // 向量  必须8位
    public final static String iv = "12345678";
    // 加解密统一使用的编码方式
    public final static String encoding = "utf-8";
    // 3des加密
    public static final String algorithm = "desede";

    /**
     * @param str 需要加密的文字
     * @return 加密后的文字
     * @throws Exception 加密失败
     */
    public static String get3DES(final String str) throws Exception {
        byte[] realKey = android.util.Base64.decode(base64secretKey, android.util.Base64.DEFAULT);
        String enText = Create3destext(realKey,str.getBytes());
//        Key deskey = null;
//        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
//        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(algorithm);
//        deskey = keyfactory.generateSecret(spec);
//
//        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
//        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
//        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
//        byte[] encryptData = cipher.doFinal(str.getBytes(encoding));
//        return Base64.encode(encryptData);
        return enText;
    }

    private static String Create3destext(byte[] realKey, byte[] plain) {
        try {
            DESedeKeySpec dks = new DESedeKeySpec(realKey);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
            SecretKey secretKey = keyfactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DESede");
            cipher.init(Cipher.DECRYPT_MODE,secretKey);
            byte[] plainbyte = cipher.doFinal(plain);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 3DES解密
     *
     * @param encryptText 加密文本
     * @return
     * @throws Exception
     */
    public static String decode3DES(String encryptText) throws Exception {
//        Key deskey = null;
//        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
//        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(algorithm);
//        deskey = keyfactory.generateSecret(spec);
//        Cipher cipher = Cipher.getInstance("desede/ECB/PKCS5Padding");
//        //        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
//        cipher.init(Cipher.DECRYPT_MODE, deskey);
//
//
//        byte[] decryptData = cipher.doFinal(Base64.decode(encryptText));
//
//        return new String(decryptData, encoding);
        return "";
    }
}
