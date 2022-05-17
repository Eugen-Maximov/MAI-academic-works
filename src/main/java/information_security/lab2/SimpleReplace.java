package information_security.lab2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static information_security.Data.ENG_ALPHABET_SIZE;
import static information_security.Data.RU_ALPHABET_SIZE;
import static information_security.Data.SPEC_ALPHABET_SIZE;
import static information_security.Data.inputText;
import static java.util.stream.Collectors.toList;

public class SimpleReplace {

    private static final List<Character> ALPHABET = createAlphabet();
    private final static Map<Character, Character> CYPHER_MAP = new HashMap<>();
    private final static Map<Character, Character> DECYPHER_MAP = new HashMap<>();
    private static String result = "";


    public static void main(String[] args) {
        fillCypherMap();

        System.out.println("Исходная строка: " + inputText);
        System.out.println("Зашифрованная строка: " + encrypt(inputText));
        System.out.println("Расшифрованная строка: " + decrypt(result));
    }

    private static void fillCypherMap() {
        List<Character> copyAlphabet = new ArrayList<>(ALPHABET);
        ALPHABET.forEach(alphabetChar -> {
            char randomChar = getRandomChar(copyAlphabet);
            //System.out.println("\"" + alphabetChar + "\"" + " = \"" + randomChar + "\"");
            CYPHER_MAP.put(alphabetChar, randomChar);
            DECYPHER_MAP.put(randomChar, alphabetChar);
        });
    }


    private static String encrypt(String inputText) {
        StringBuilder sb = new StringBuilder();
        for (Character ch : inputText.toCharArray()) {
            Character character = CYPHER_MAP.get(ch);
            if (character == null) {
                throw new RuntimeException("Can't find char " + character + " by " + ch);
            }
            sb.append(CYPHER_MAP.get(ch));
        }
        return result = sb.toString();
    }

    private static String decrypt(String encryptedString) {
        StringBuilder sb = new StringBuilder();
        for (Character ch : encryptedString.toCharArray()) {
            sb.append(DECYPHER_MAP.get(ch));
        }
        return sb.toString();
    }
    private static List<Character> createAlphabet() {
        List<Character> cypherAlphabet = new ArrayList<>();
        List<Character> engAlphabet = getAlphabet("A", ENG_ALPHABET_SIZE);
        List<Character> ruAlphabet = getAlphabet("А", RU_ALPHABET_SIZE);
        List<Character> specAlphabet = getAlphabet(" ", SPEC_ALPHABET_SIZE);
        specAlphabet = specAlphabet.stream()
                .distinct()
                .collect(toList());
        cypherAlphabet.addAll(engAlphabet);
        cypherAlphabet.addAll(ruAlphabet);
        cypherAlphabet.addAll(specAlphabet);
        return cypherAlphabet;
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

    private static Character getRandomChar(List<Character> copyAlphabet) {
        Random random = new Random();
        Character character = copyAlphabet.get(random.nextInt(copyAlphabet.size()));
        copyAlphabet.remove(character);
        return character;
    }
}
