package org.example.gimnasio.security;

import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class KeyUtils {

    private static final String ECDSA_ALGORITHM = "EC";

    /**
     * Convierte PublicKey a Base64
     */
    public static String publicKeyToBase64(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * Convierte Base64 a PublicKey
     */
    public static PublicKey base64ToPublicKey(String publicKeyBase64) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ECDSA_ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * Convierte PrivateKey a Base64
     */
    public static String privateKeyToBase64(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    /**
     * Convierte Base64 a PrivateKey
     */
    public static PrivateKey base64ToPrivateKey(String privateKeyBase64) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ECDSA_ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * Genera un ID único para una clave
     */
    public static String generateKeyId(PublicKey publicKey) throws Exception {
        byte[] keyBytes = publicKey.getEncoded();
        return HashUtils.hashSHA256(Base64.getEncoder().encodeToString(keyBytes));
    }
}