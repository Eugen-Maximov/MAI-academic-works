package information_security.lab1;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.Character.toLowerCase;
import static java.lang.Math.abs;
import static java.util.Arrays.asList;
public class CaesarCipher {

    static String inputText =  "По легенде Великий Северный Воин, прознал о великане, который жил в озере с лотосами, поедавшего любого, кто подходил близко к озеру и поклялся его убить. Воин нанес татау на своё тело, взяв силу из земли мёртвых. Затем он собрался и пошёл по тропе к озеру с лотосами, воин победил великана и отсёк ему голову своим кинжалом, которая упала на землю и стала островом. Потомки воина стали называться ракьят и заселили остров. Позднее остров был открыт и частично заселён переселенцами из Европы. Ракьят жили в содружестве с колонистами, переняли их язык, научились пользоваться их технологиями, хотя сами не развивались, как и предыдущие несколько тысяч лет. In a hole in the ground there lived a hobbit. Not a nasty, dirty, wet hole, filled with the ends of worms and an oozy smell, nor yet a dry, bare, sandy hole with nothing in it to sit down on or to eat: it was a hobbit-hole, and that means comfort.";
    static String resultText = "";
    private final static List<Character> CYPHER_ALPHABET = fillAlphabet();
    private final static int ENG_ALPHABET_SIZE = 26;
    private final static int RU_ALPHABET_SIZE = 32; // Without Ёё
    private final static List<Character> CYPHER_ALPHABET_FOR_SPEC_SYMBOLS = asList(' ', '.', ',');
    private static final List<Character> SPEC_SYMBOLS = asList('!', '@', '#', '$', '%');
    static int key = 5;

    private static List<Character> fillAlphabet() {
        List<Character> cypherAlphabet = new ArrayList<>();
        List<Character> engAlphabet = getAlphabet("A", ENG_ALPHABET_SIZE);
        List<Character> ruAlphabet = getAlphabet("А", RU_ALPHABET_SIZE);
        cypherAlphabet.addAll(engAlphabet);
        cypherAlphabet.addAll(ruAlphabet);
        return cypherAlphabet;
    }

    public static void main(String[] args) {
        System.out.println("Исходный текст: " + inputText);
        System.out.println("Ключ: " + key);
        System.out.println("Зашифрованная строка: " + encryptText(inputText, key));
        System.out.println("Расшифрованная строка: " + decryptText(resultText, key));
        System.out.println("Взломанная строка: " + breakingTheCipher(resultText));
    }

    private static String encryptText(String inputText, int key) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inputText.length(); i++) {
            char sourceChar = inputText.charAt(i);
            int charIdx;
            if (CYPHER_ALPHABET_FOR_SPEC_SYMBOLS.contains(sourceChar)) {
                sb.append(SPEC_SYMBOLS.get(new Random().nextInt(SPEC_SYMBOLS.size())));
            } else {
                charIdx = CYPHER_ALPHABET.indexOf(sourceChar);
                sb.append(encryptLetter(charIdx, key, CYPHER_ALPHABET));
            }
        }
        resultText = sb.toString();
        return resultText;
    }

    private static String decryptText(String inputText, int key) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inputText.length(); i++) {
            char sourceChar = inputText.charAt(i);
            int charIdx;
            if (SPEC_SYMBOLS.contains(sourceChar)) {
                sb.append(' ');
            } else {
                charIdx = CYPHER_ALPHABET.indexOf(sourceChar);
                sb.append(decryptLetter(charIdx, key, CYPHER_ALPHABET));
            }
        }
        return sb.toString();
    }

    private static String breakingTheCipher(String inputText) {
        StringBuilder sb = new StringBuilder();
        key = findKey(inputText);
        for (int i = 0; i < inputText.length(); i++) {
            char sourceChar = inputText.charAt(i);
            int charIdx;
            if (SPEC_SYMBOLS.contains(sourceChar)) {
                sb.append(' ');
            } else {
                charIdx = CYPHER_ALPHABET.indexOf(sourceChar);
                sb.append(decryptLetter(charIdx, key, CYPHER_ALPHABET));
            }
        }
        return sb.toString();
    }

    private static List<Character> getAlphabet(String firstLetter, int alphabetSize) {
        List<Character> alphabet = new ArrayList<>();
        char letter = firstLetter.toUpperCase().charAt(0);
        boolean lowerCaseFilled = false;
        for (int i = 0; i < alphabetSize * 2; i++) {
            alphabet.add(letter);
            if (i == alphabetSize - 1 && !lowerCaseFilled) {
                letter = firstLetter.toLowerCase().charAt(0);
                lowerCaseFilled = true;
            } else {
                letter++;
            }
        }
        return alphabet;
    }

    private static char encryptLetter(int sourceCharIdx, int key, List<Character> alphabet) {
        return alphabet.get(abs(sourceCharIdx + key) % alphabet.size());
    }

    private static char decryptLetter(int sourceCharIdx, int key, List<Character> alphabet) {
        return sourceCharIdx - key < 0
                ? alphabet.get(alphabet.size() - abs(sourceCharIdx - key))
                : alphabet.get(abs(sourceCharIdx - key) % alphabet.size());
    }

    private static int findKey(String encryptedString) {
        Map<Character, Integer> letter2count = countLetters(encryptedString);
        Character mostOccuredLetter = getMostOccuredCharacter(letter2count);
        Character mostFrequencyEnLetter = 'e';
        Character mostFrequencyRuLetter = 'о';
        int mostOccuredLetterIdx = CYPHER_ALPHABET.indexOf(toLowerCase(mostOccuredLetter));
        int mostOccuredEnLetterIdx = CYPHER_ALPHABET.indexOf(mostFrequencyEnLetter);
        int mostOccuredRuLetterIdx = CYPHER_ALPHABET.indexOf(mostFrequencyRuLetter);
        int key = mostOccuredLetterIdx - mostOccuredEnLetterIdx;
        if (key > CYPHER_ALPHABET.size() / 2) {
            key = mostOccuredLetterIdx - mostOccuredRuLetterIdx;
        }

        return key;
    }

    private static Character getMostOccuredCharacter(Map<Character, Integer> letter2count) {
        int count = 0;
        Character result = null;
        for (Map.Entry<Character, Integer> entry : letter2count.entrySet()) {
            if (entry.getValue() > count) {
                count = entry.getValue();
                result = entry.getKey();
            }
        }
        return result;
    }

    private static Map<Character, Integer> countLetters(String encryptedString) {
        Map<Character, Integer> char2count = new HashMap<>();
        for (Character character : encryptedString.toCharArray()) {
            if (CYPHER_ALPHABET_FOR_SPEC_SYMBOLS.contains(character)) {
                continue;
            }
            if (char2count.containsKey(character)) {
                char2count.put(character, char2count.get(character) + 1);
            } else {
                char2count.put(character, 1);
            }
        }

        return char2count;
    }
}
