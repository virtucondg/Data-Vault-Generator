package com.biogen.utils;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
 
/**
 * This class provides methods to encrypt and decrypt data.
 * @author drothste
 */
public class Crypto {
    static private byte[] initialVector = "0246842013579531".getBytes(); // This has to be 16 characters
    static private String secretKey = "S3cR3tK3y4uB11B";
    static private SecretKeySpec skeySpec;
    static private IvParameterSpec initialVectorP;
    
	private static String md5(final String input) throws NoSuchAlgorithmException {
        final MessageDigest md = MessageDigest.getInstance("MD5");
        final byte[] messageDigest = md.digest(input.getBytes());
        final BigInteger number = new BigInteger(1, messageDigest);
        return String.format("%032x", number);
    }
 
    static private Cipher initCipher(final int mode)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        skeySpec = new SecretKeySpec(md5(secretKey).getBytes(), "AES");
        initialVectorP = new IvParameterSpec(initialVector);
        final Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        cipher.init(mode, skeySpec, initialVectorP);
        return cipher;
    }
 
    /**
     * Encrypt a string
     * @param dataToEncrypt the string to encrypt
     * @return the encrypted string
     */
    static public String encrypt(final String dataToEncrypt) {
        String encryptedData = null;
        try {
            final Cipher cipher = initCipher(Cipher.ENCRYPT_MODE);
            final byte[] encryptedByteArray = cipher.doFinal(dataToEncrypt.getBytes());
            encryptedData = Base64.getEncoder().encodeToString(encryptedByteArray);
        } catch (Exception e) {
            System.err.println("Problem encrypting the data");
            e.printStackTrace();
        }
        return encryptedData;
    }
 
    /**
     * Decrypt a string
     * @param encryptedData the string to decrypt
     * @return the decrypted string
     */
    static public String decrypt(final String encryptedData) {
        String decryptedData = null;
        try {
            final Cipher cipher = initCipher(Cipher.DECRYPT_MODE);
            final byte[] encryptedByteArray = Base64.getDecoder().decode(encryptedData);
            final byte[] decryptedByteArray = cipher.doFinal(encryptedByteArray);
            decryptedData = new String(decryptedByteArray, "UTF8");
        } catch (Exception e) {
            System.err.println("Problem decrypting the data");
            e.printStackTrace();
        }
        return decryptedData;
    }
 
    /**
     * Main method for testing; it encrypts and then decrypts a string.
     * @param args arguments that are ignored
     */
    public static void main(final String[] args) {
        final String encryptedData = Crypto.encrypt("This is a Test");
        System.out.println("\"" + encryptedData + "\"");
 
        final String decryptedData = Crypto.decrypt(encryptedData);
        System.out.println("\"" + decryptedData + "\"");
    }
}
