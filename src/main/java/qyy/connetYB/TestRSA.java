package qyy.connetYB;

import java.security.PublicKey;


public class TestRSA {
    public static String publicKeyStr ;
    public static void setPublicKey(String publicKey){
        publicKeyStr = publicKey;
    }
    public static String getEncryKey(String  message) {
        String byte2Base64 = null;
        try {
            //将Base64编码后的公钥转换成PublicKey对象
            PublicKey publicKey = RSAUtil.getPublicKey(publicKeyStr);
            //用公钥加密
            byte[] publicEncrypt = RSAUtil.publicEncrypt(message.getBytes(), publicKey);
            //加密后的内容Base64编码
            byte2Base64 = RSAUtil.byte2Base64(publicEncrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byte2Base64;
    }
}