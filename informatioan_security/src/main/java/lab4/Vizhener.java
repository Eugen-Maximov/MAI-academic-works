package lab4;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static data.Data.inputTextEn;
import static data.Data.keyWord;
import static java.lang.Math.max;

public class Vizhener {
    private static final List<Character> ALPHABET = getEnAlphabet();
    private final static int ENG_ALPHABET_SIZE = 26;
    private final static String SOURCE_STRING = inputTextEn;
    public final static String KEY_WORD = keyWord;
    private enum Mode {
        ENCRYPT,
        DECRYPT
    }

    private static List<Character> getEnAlphabet() {
        List<Character> alphabet = new ArrayList<>();
        char letter = 'A';
        for (int i = 0; i < ENG_ALPHABET_SIZE; i++) {
            alphabet.add(letter);
            letter++;
        }

        return alphabet;
    }

    public static void main(String[] args) {
        String sourceStringWithoutSymbols = "";
        for (Character ch : SOURCE_STRING.toUpperCase().toCharArray()) {
            if (ALPHABET.contains(ch)) {
                sourceStringWithoutSymbols = sourceStringWithoutSymbols.concat(ch.toString().toUpperCase(Locale.ROOT));
            }
        }
        String encryptedString = processString(sourceStringWithoutSymbols, KEY_WORD, Mode.ENCRYPT);
        String crackedString = VizhenerCipherCrack.doCrack(encryptedString);

        System.out.println("Исходный текст: " + sourceStringWithoutSymbols);
        System.out.println("Взломанный текст: " + crackedString);
    }

    private static String processString(String string, String key, Mode mode) {
        int maxLength = max(string.length(), key.length());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maxLength; i++) {
            int alphabetCharIdx = ALPHABET.indexOf(string.charAt(i >= string.length() ? i % string.length() : i)); // подгон сообщения к ключу (если меньше)
            Character keyChar = key.charAt(i >= key.length() ? i % key.length() : i); // подгон ключа к сообщению (если короткий)
            int keyCharIdx = ALPHABET.indexOf(keyChar);
            keyCharIdx = mode.equals(Mode.DECRYPT) ? -keyCharIdx : keyCharIdx; // вычитание при дешифровании, либо сложение
            Character tableSymbol = ALPHABET.get((ALPHABET.size() + (alphabetCharIdx + keyCharIdx)) % ALPHABET.size()); // символ по таблице Виженера
            sb.append(tableSymbol);
        }

        return sb.toString();
    }
}

