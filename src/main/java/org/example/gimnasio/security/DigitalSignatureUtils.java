package org.example.gimnasio.security;

import java.security.*;
import java.util.Base64;

public class DigitalSignatureUtils {

    private static final String ECDSA_ALGORITHM = "EC";
    private static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";
    private static final int KEY_SIZE = 256;

    /**
     * Genera par de claves ECDSA
     */
    public static KeyPair generateECDSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ECDSA_ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * Firma datos con clave privada ECDSA
     */
    public static String signECDSA(String data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data.getBytes("UTF-8"));
        byte[] signatureBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    /**
     * Verifica firma con clave pública ECDSA
     */
    public static boolean verifyECDSA(String data, String signature, PublicKey publicKey) throws Exception {
        Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
        sig.initVerify(publicKey);
        sig.update(data.getBytes("UTF-8"));
        return sig.verify(Base64.getDecoder().decode(signature));
    }

    /**
     * Firma transacción para el sistema
     */
    public static String signTransaction(String transactionData, PrivateKey privateKey) throws Exception {
        return signECDSA(transactionData, privateKey);
    }

    /**
     * Verifica firma de transacción
     */
    public static boolean verifyTransaction(String transactionData, String transactionSignature, PublicKey publicKey) throws Exception {
        return verifyECDSA(transactionData, transactionSignature, publicKey);
    }
}