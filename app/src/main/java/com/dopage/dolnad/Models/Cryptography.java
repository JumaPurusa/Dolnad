package com.dopage.dolnad.Models;

import android.util.Base64;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Cryptography {

    private static final String ALGO = "AES";
    private String securityKey;

    public Cryptography(String key){
        this.securityKey = key;
    }

    public String encrypt(String dataToEncrypt, String securityKey) throws Exception {
        SecretKeySpec key = generatedKey(securityKey);
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(dataToEncrypt.getBytes());
        String encryptedData = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedData;
    }

    public String decrypt(String encryptedData, String securityKey) throws Exception{
        SecretKeySpec key = generatedKey(securityKey);
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decVal = Base64.decode(encryptedData, Base64.DEFAULT);
        byte[] decodedVal = c.doFinal(decVal);
        return new String(decodedVal);
    }

    private SecretKeySpec generatedKey(String securityKey) throws Exception{
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = securityKey.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGO);
        return secretKeySpec;
    }
}
