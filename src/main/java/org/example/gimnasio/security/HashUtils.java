package org.example.gimnasio.security;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashUtils {

    private static final String SHA_256 = "SHA-256";
    private static final String HMAC_SHA256 = "HmacSHA256";

    /**
     * Calcula hash SHA-256 de los datos
     */
    public static String hashSHA256(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(SHA_256);
        byte[] hash = digest.digest(data.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Calcula hash SHA-256 devolviendo bytes
     */
    public static byte[] hashSHA256Bytes(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(SHA_256);
        return digest.digest(data.getBytes());
    }

    /**
     * Calcula HMAC usando SHA-256
     */
    public static String calculateHMAC(String data, SecretKey key) throws Exception {
        Mac hmac = Mac.getInstance(HMAC_SHA256);
        hmac.init(key);
        byte[] hmacData = hmac.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(hmacData);
    }

    /**
     * Verifica hash SHA-256
     */
    public static boolean verifyHash(String data, String storedHash) throws NoSuchAlgorithmException {
        String calculatedHash = hashSHA256(data);
        return calculatedHash.equals(storedHash);
    }

    /**
     * Verifica HMAC
     */
    public static boolean verifyHMAC(String data, String storedHmac, SecretKey key) throws Exception {
        String calculatedHmac = calculateHMAC(data, key);
        return calculatedHmac.equals(storedHmac);
    }

    /**
     * Convierte bytes a string hexadecimal
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}