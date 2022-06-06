package lab3;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static data.Data.ENG_ALPHABET_SIZE;
import static data.Data.inputTextEn;
import static data.Data.keyWord;
import static java.lang.Math.max;
public class VizhenerCipher {

    private static final List<Character> ALPHABET;
    public static String result = "";

    static {
        ALPHABET = new ArrayList<>();
        char letter = 'A';
        for (int i = 0; i < ENG_ALPHABET_SIZE; i++) {
            ALPHABET.add(letter);
            letter++;
        }
    }

    private static String clearString = clearSymbols(inputTextEn);

    static {
        encode(clearString, keyWord);
    }

    public static void main(String[] args) {
        System.out.println("Исходный текст: " + inputTextEn);
        System.out.println("Кодовое слово: " + keyWord);
        System.out.println("Отконвертированный текст: " + clearString);
        System.out.println("Зашифрованный текст: " + encode(clearString, keyWord));
        System.out.println("Расшифрованный текст: " + decode(result, keyWord));
    }

    private static String clearSymbols(String inputText) {
        String sourceStringWithoutSymbols = "";
        for (Character ch : inputText.toUpperCase().toCharArray()) {
            if (ALPHABET.contains(ch)) {
                sourceStringWithoutSymbols = sourceStringWithoutSymbols.concat(ch.toString().toUpperCase(Locale.ROOT));
            }
        }
        return sourceStringWithoutSymbols;
    }

    private static String encode(String inputText, String key) {
        int maxLength = max(inputText.length(), key.length());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maxLength; i++) {
            int alphabetCharIdx = ALPHABET.indexOf(inputText.charAt(i >= inputText.length() ? i % inputText.length() : i)); // подгон сообщения к ключу (если меньше)
            Character keyChar = key.charAt(i >= key.length() ? i % key.length() : i); // подгон ключа к сообщению (если короткий)
            int keyCharIdx = ALPHABET.indexOf(keyChar);
            Character tableSymbol = ALPHABET.get((ALPHABET.size() + (alphabetCharIdx + keyCharIdx)) % ALPHABET.size()); // символ по таблице Виженера
            sb.append(tableSymbol);
        }
        result = sb.toString();
        return result;
    }

    private static String decode(String inputText, String key) {
        int maxLength = max(inputText.length(), key.length());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maxLength; i++) {
            int alphabetCharIdx = ALPHABET.indexOf(inputText.charAt(i >= inputText.length() ? i % inputText.length() : i)); // подгон сообщения к ключу (если меньше)
            Character keyChar = key.charAt(i >= key.length() ? i % key.length() : i); // подгон ключа к сообщению (если короткий)
            int keyCharIdx = ALPHABET.indexOf(keyChar);
            keyCharIdx -= keyCharIdx * 2;
            Character tableSymbol = ALPHABET.get((ALPHABET.size() + (alphabetCharIdx + keyCharIdx)) % ALPHABET.size()); // символ по таблице Виженера
            sb.append(tableSymbol);
        }
        return sb.toString();
    }
}
