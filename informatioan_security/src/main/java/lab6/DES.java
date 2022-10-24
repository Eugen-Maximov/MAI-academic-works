package lab6;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;

import static data.Data.inputText;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

public class DES {
    private final static String SOURCE_STRING = inputText;
    private static Cipher DES;
    private static SecretKey KEY;

    public static void main(String[] args) throws Exception {
        DES = Cipher.getInstance("DES");
        KEY = KeyGenerator.getInstance("DES").generateKey();
        DES.init(ENCRYPT_MODE, KEY);

        String encodedString = encode(SOURCE_STRING);
        DES.init(DECRYPT_MODE, KEY);
        String decodedString = decode(encodedString);

        System.out.println("Исходная строка: " + SOURCE_STRING);
        System.out.println("Шифр: " + encodedString);
        System.out.println("Расшифровка: " + decodedString);
    }

    private static String decode(String encodedString) throws IOException, IllegalBlockSizeException, BadPaddingException {
        byte[] encodedStringBytes = new BASE64Decoder().decodeBuffer(encodedString);
        byte[] decodedBytes = DES.doFinal(encodedStringBytes);

        return new String(decodedBytes, UTF_8);
    }

    private static String encode(String sourceString) throws IllegalBlockSizeException, BadPaddingException {
        byte[] sourceStringBytes = sourceString.getBytes(UTF_8);
        byte[] encryptedBytes = DES.doFinal(sourceStringBytes);

        return new BASE64Encoder().encode(encryptedBytes);
    }
}

