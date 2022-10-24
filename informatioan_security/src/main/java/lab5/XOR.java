package lab5;

import java.util.Base64;

import static data.Data.inputText;
import static data.Data.keyWord;
import static java.nio.charset.StandardCharsets.UTF_8;

public class XOR {
    //Гаммирование

    private final static String SOURCE_STRING = inputText;
    private final static String KEY = keyWord.toLowerCase();

    private enum Mode {
        ENCRYPT,
        DECRYPT
    }

    public static void main(String[] args) {
        String encodedString = processStinrg(SOURCE_STRING, KEY, Mode.ENCRYPT);
        String decodedString = processStinrg(encodedString, KEY, Mode.DECRYPT);

        System.out.println("Исходная строка: " + SOURCE_STRING);
        System.out.println("Шифр: " + encodedString);
        System.out.println("Расшифровка: " + decodedString);
    }

    private static String processStinrg(String sourceString, String key, Mode mode) {
        StringBuilder sb = new StringBuilder();
        // Если расшифровываем, то надо раскодировать строку из Base64
        char[] sourceChars = Mode.ENCRYPT.equals(mode)
                ? sourceString.toCharArray()
                : new String(Base64.getDecoder().decode(sourceString.getBytes(UTF_8)), UTF_8).toCharArray();
        for (int i = 0; i < sourceChars.length; i++) {
            sb.append((char) (sourceChars[i] ^ key.charAt(i % key.length())));
        }

        // Если шифруем, то шифруем байты в Base64 для удобства восприятия
        if (Mode.ENCRYPT.equals(mode)) {
            return Base64.getEncoder().encodeToString(sb.toString().getBytes(UTF_8));
        }

        return sb.toString();
    }
}

