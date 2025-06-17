package org.example.gimnasio.security;

import javax.crypto.SecretKey;
import java.security.*;
import java.util.Base64;
import javax.crypto.Mac;

public class SecurityUtils {

    private static final String SHA_256 = "SHA-256";


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

    // Firmar datos con clave privada (ECDSA)
    public static String signECDSA(String data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes());
        byte[] signatureBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signatureBytes);//Firma en base 64
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

}
