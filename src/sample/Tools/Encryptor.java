package sample.Tools;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryptor {
    final static String key = "OmonatniSaqla555"; // 128 bit key
    final static String initVector = "RandomInitVector"; // 16 bytes IV

    public static String encrypt(String value) {
        String encrypterString = "";
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            encrypterString = Base64.encodeBase64String(encrypted);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return encrypterString;
    }

    public static String decrypt(String encrypted) {
        String deCryptedString = "";
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            deCryptedString = new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return deCryptedString;
    }

    public static void main(String[] args) {
        String enCryptString = encrypt("Bismillah Allohu Akbar");
        System.out.println(enCryptString);

        System.out.println(decrypt(enCryptString));
    }

    public static String getKey() {
        return key;
    }

    public static String getInitVector() {
        return initVector;
    }
}