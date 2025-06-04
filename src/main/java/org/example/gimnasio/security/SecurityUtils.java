package org.example.gimnasio.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Mac;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class SecurityUtils {

    private static final String AES = "AES";
    private static final String SHA_256 = "SHA-256";
    private static final String SECRET_KEY = "my_secret_key_123"; // Esta es una clave secreta de ejemplo. Cambiar a algo más seguro.
    private static final int AES_KEY_SIZE = 256; //256 bits para mayor seguridad

    // Generar una clave secreta para AES
    public static SecretKey generateAES256Key() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
        keyGenerator.init(AES_KEY_SIZE); // AES-256 bits
        return keyGenerator.generateKey();
    }

    // Cifrar datos usando AES
    public static String encryptAES(String data) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), AES);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted); // Convertimos el byte[] a una cadena Base64
    }

    // Descifrar datos usando AES
    public static String decryptAES(String encryptedData) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), AES);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData)); // Convertimos Base64 a byte[] y luego desencriptamos
        return new String(decrypted);
    }

    // Cálculo del hash SHA-256 para datos (por ejemplo, para la huella digital)
    public static String hashSHA256(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(SHA_256);
        byte[] hash = digest.digest(data.getBytes());
        return Base64.getEncoder().encodeToString(hash); // Convertimos el hash a Base64 para almacenamiento o transmisión
    }

    // Cálculo HMAC usando SHA-256
    public static String calculateHMAC(String data, SecretKey key) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(key);
        byte[] hmacData = hmac.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(hmacData);
    }

    // Generar par de claves ECDSA
    public static KeyPair generateECDSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        keyPairGenerator.initialize(256); // Usamos una curva de 256 bits para mayor seguridad
        return keyPairGenerator.generateKeyPair();
    }

    // Firmar datos con clave privada (ECDSA)
    public static String signECDSA(String data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes());
        byte[] signatureBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    // Verificar firma con clave pública (ECDSA)
    public static boolean verifyECDSA(String data, String signature, PublicKey publicKey) throws Exception {
        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initVerify(publicKey);
        sig.update(data.getBytes());
        return sig.verify(Base64.getDecoder().decode(signature));
    }

    // Metodo para verificar la huella digital usando SHA-256
    public static boolean verifyHuella(String originalHuella, String hashedHuella) throws NoSuchAlgorithmException {
        String hashOriginal = hashSHA256(originalHuella);
        return hashOriginal.equals(hashedHuella); // Comparamos el hash de la huella original con el almacenado
    }

    // Generar un hash SHA-256 de cualquier string
    public static String hashString(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(SHA_256);
        byte[] hash = digest.digest(input.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }

    // Convertir byte en una cadena hexadecima
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Firma digital para transacciones (ventas y corte de caja)
    public static String signTransaction(String transactionData, PrivateKey privateKey) throws Exception {
        return signECDSA(transactionData, privateKey);
    }

    // Verificación de la firma digital para transacciones
    public static boolean verifyTransaction(String transactionData, String transactionSignature, PublicKey publicKey) throws Exception {
        return verifyECDSA(transactionData, transactionSignature, publicKey);
    }

    // Validación de integridad usando SHA-256
    public static boolean validateIntegritySHA256(String data, String storedHash) throws NoSuchAlgorithmException {
        String calculatedHash = hashSHA256(data);
        return calculatedHash.equals(storedHash);  // Compara el hash calculado con el almacenado
    }

    // Validación de integridad usando HMAC
    public static boolean validateIntegrityHMAC(String data, String storedHmac, SecretKey key) throws Exception {
        String calculatedHmac = calculateHMAC(data, key);
        return calculatedHmac.equals(storedHmac);  // Compara el HMAC calculado con el almacenado
    }
}
