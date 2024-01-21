package com.example.blackbox;

import android.util.Base64;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    public static String encrypt(String plainText, SecretKey key, String iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.decode(iv, Base64.DEFAULT));

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
        byte[] cipherText = cipher.doFinal(plainText.getBytes());
        String encryptedValue = Base64.encodeToString(cipherText, Base64.DEFAULT);
        return encryptedValue;
    }

    public static String decrypt(String cipherText, SecretKey key, String iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.decode(iv, Base64.DEFAULT));

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
        byte[] plainText = cipher.doFinal(Base64.decode(cipherText, Base64.DEFAULT));
        String decryptedValue = new String(plainText);
        return decryptedValue;
    }

    public static SecretKey generateKeyBySize(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }

    public static byte[] getKeyFromPassword(String masterKey, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory secretKeyfactory = SecretKeyFactory.getInstance("PBKDF2withHmacSHA1"); //PBKDF2withHmacSHA1  vs   PBKDF2WithHmacSHA256(NOT SUPPORTED)
        KeySpec spec = new PBEKeySpec(masterKey.toCharArray(), salt, 1000, 256);
        byte[] secret = secretKeyfactory.generateSecret(spec).getEncoded();
        return secret;
    }

    public static SecretKey getKeyFromPassword(String masterKey, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2withHmacSHA1");
        KeySpec spec = new PBEKeySpec(masterKey.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), 1000, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        return secret;
    }

    public static String generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return Base64.encodeToString(iv, Base64.DEFAULT);
    }

    public static IvParameterSpec generateIv2() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.encodeToString(salt, Base64.DEFAULT);
    }

    public static byte[] generateSaltByte() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public static void encryptFile(String key, String dbPath)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, IOException, InvalidKeySpecException {


        String salt =" generateSalt()";
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getKeyFromPassword("123456", salt), generateIv2());
        FileInputStream inputStream = new FileInputStream(dbPath);
        FileOutputStream outputStream = new FileOutputStream(dbPath + "_encrypted");
        byte[] buffer = new byte[64];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                //outputStream.write(output);
            }
        }

        byte[] outputBytes = cipher.doFinal();
        outputStream.write(salt.getBytes());
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
    }
    public static void decryptDB(String key, String dbPath)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, IOException, InvalidKeySpecException {


        FileInputStream inputStream = new FileInputStream(dbPath + "_encrypted");
        byte[] saltByte = new byte[16];
        String salt = "a";

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, getKeyFromPassword("123456", salt), generateIv2());
        FileOutputStream outputStream = new FileOutputStream(dbPath + "_encrypted");
        outputStream.write(salt.getBytes());
        byte[] buffer = new byte[64];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }

        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
    }
    /*private String secretKey = "this_is_secret_key_1";

    //(String data, String secretKey)
    public String encrypt(String d) throws Exception{
        SecretKeySpec key = generateKey();
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(d.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;//encrypted plain text
    }
    //(String encryptedText, String secretKey)
    public String decrypt(String encryptedText) throws Exception{
        SecretKeySpec key = generateKey();
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.decode(encryptedText, Base64.DEFAULT);
        byte[] decVal = c.doFinal(decodedValue);
        String decryptedValue = new String(decVal);
        return decryptedValue;
    }

    public SecretKeySpec generateKey() throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = secretKey.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }*/
}
