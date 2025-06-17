package org.example.gimnasio.security;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public class SecurityValidator {

    /**
     * Valida integridad usando SHA-256
     */
    public static boolean validateIntegritySHA256(String data, String storedHash) throws NoSuchAlgorithmException {
        return HashUtils.verifyHash(data, storedHash);
    }

    /**
     * Valida integridad usando HMAC
     */
    public static boolean validateIntegrityHMAC(String data, String storedHmac, SecretKey key) throws Exception {
        return HashUtils.verifyHMAC(data, storedHmac, key);
    }

    /**
     * Verifica huella digital
     */
    public static boolean verifyFingerprint(String capturedFingerprint, String storedFingerprintHash) throws NoSuchAlgorithmException {
        String hashCaptured = HashUtils.hashSHA256(capturedFingerprint);
        return hashCaptured.equals(storedFingerprintHash);
    }

    /**
     * Verifica firma digital de transacción
     */
    public static boolean verifyTransactionSignature(String transactionData, String signature, PublicKey publicKey) throws Exception {
        return DigitalSignatureUtils.verifyTransaction(transactionData, signature, publicKey);
    }

    /**
     * Valida formato de email básico
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * Valida formato de teléfono básico
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^[0-9]{10,15}$");
    }
}
