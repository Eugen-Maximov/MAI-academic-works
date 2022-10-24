package lab7;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;

import static data.Data.inputText;
import static java.nio.charset.StandardCharsets.UTF_8;

public class RSA {
    private static Cipher RSA;
    private static Cipher AES;
    private static SecretKey aesKey;
    private static PrivateKey privateKey; // Используем для расшифровки
    private static PublicKey publicKey; // Используем для шифрования
    private static final String SOURCE_STRING = inputText;

    public static void main(String[] args) throws Exception {
        RSA = Cipher.getInstance("RSA");
        AES = Cipher.getInstance("AES");
        generateKeys();

        byte[] encryptedMessageBytes = encrypt();
        // Шифруем AES ключ
        RSA.init(Cipher.PUBLIC_KEY, publicKey);
        byte[] encryptedAesKey = RSA.doFinal(aesKey.getEncoded());

        RSA.init(Cipher.PRIVATE_KEY, privateKey);
        byte[] decryptedAesKey = RSA.doFinal(encryptedAesKey);
        String decryptedMessage = decrypt(encryptedMessageBytes, decryptedAesKey);

        System.out.println("Исходная строка: " + SOURCE_STRING);
        System.out.println("Зашифрованная строка: " + Base64.getEncoder().encodeToString(encryptedMessageBytes));
        System.out.println("Расшифрованная строка: " + decryptedMessage);

        int n = 18;
        System.out.println("Эйлер" + "(" + n + "): " + eiler(18));

    }

    private static String decrypt(byte[] encryptedMessageBytes, byte[] decryptedKeyBytes) throws Exception {
        SecretKey originalAesKey = new SecretKeySpec(decryptedKeyBytes, 0, decryptedKeyBytes.length, "AES");
        AES.init(Cipher.DECRYPT_MODE, originalAesKey);
        byte[] decryptedBytes = AES.doFinal(encryptedMessageBytes);

        return new String(decryptedBytes, UTF_8);
    }

    private static byte[] encrypt() throws Exception {
        AES.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] sourceStringBytes = SOURCE_STRING.getBytes(UTF_8);

        return AES.doFinal(sourceStringBytes);
    }

    private static void generateKeys() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024); // Размер ключей будет 2048 бит
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        privateKey = keyPair.getPrivate();
        System.out.println("Private: " + Arrays.toString(privateKey.getEncoded()));
        publicKey = keyPair.getPublic();
        System.out.println("Public: " + Arrays.toString((publicKey.getEncoded())));


        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128); // Размер AES ключа - 128 бит
        aesKey = keyGenerator.generateKey();
    }

    private static int eiler(int n) {
        int result = n;
        for (int i = 2; i * i <= n; ++i) {
            if (n % i == 0) {
                while (n % i == 0) {
                    n /= i;
                }
                result -= result / i;
            }
        }
        if (n > 1) {
            result -= result / n;
        }
        return result;
    }
}

