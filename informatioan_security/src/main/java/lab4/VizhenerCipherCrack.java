package lab4;

import lab3.VizhenerCipher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;

public class VizhenerCipherCrack {

    static String encodedText = VizhenerCipher.result;

    private static final int ALPHABET_SIZE = 26;
    private static final char FIRST_CHAR_IN_ALPHABET = 'A';

    public static String doCrack(String encrypted) {
        int keyLength = getKeysByKasiski(encrypted).stream()
                .findFirst()
                .orElseThrow(RuntimeException::new);

        char mostCommonCharOverall = 'E';

        // Blocks
        List<String> blocks = new ArrayList<>();
        for (int startIndex = 0; startIndex < encrypted.length(); startIndex += keyLength) {
            int endIndex = Math.min(startIndex + keyLength, encrypted.length());
            String block = encrypted.substring(startIndex, endIndex);
            blocks.add(block);
        }

        System.out.println("Individual blocks are:");
        blocks.forEach(System.out::println);

        // Frequency
        List<Map<Character, Integer>> digitToCounts = Stream.generate(HashMap<Character, Integer>::new)
                .limit(keyLength)
                .collect(Collectors.toList());

        for (String block : blocks) {
            for (int i = 0; i < block.length(); i++) {
                char c = block.charAt(i);
                Map<Character, Integer> counts = digitToCounts.get(i);
                counts.compute(c, (character, count) -> count == null ? 1 : count + 1);
            }
        }

        List<List<CharacterFrequency>> digitToFrequencies = new ArrayList<>();
        for (Map<Character, Integer> counts : digitToCounts) {
            int totalCharacterCount = counts.values()
                    .stream()
                    .mapToInt(Integer::intValue)
                    .sum();
            List<CharacterFrequency> frequencies = new ArrayList<>();
            for (Map.Entry<Character, Integer> entry : counts.entrySet()) {
                double frequency = entry.getValue() / (double) totalCharacterCount;
                frequencies.add(new CharacterFrequency(entry.getKey(), frequency));
            }
            Collections.sort(frequencies);
            digitToFrequencies.add(frequencies);
        }

        System.out.println("Frequency distribution for each digit is:");
        digitToFrequencies.forEach(System.out::println);

        // Guessing
        StringBuilder keyBuilder = new StringBuilder();
        for (List<CharacterFrequency> frequencies : digitToFrequencies) {
            char mostFrequentChar = frequencies.get(0)
                    .getCharacter();
            int keyInt = mostFrequentChar - mostCommonCharOverall;
            keyInt = keyInt >= 0 ? keyInt : keyInt + ALPHABET_SIZE;
            char key = (char) (FIRST_CHAR_IN_ALPHABET + keyInt);
            keyBuilder.append(key);
        }

        String key = keyBuilder.toString();
        System.out.println("The guessed key is: " + key);
        System.out.print("Is key correct? (yes/no)");
        Scanner scanner = new Scanner(System.in);
        String response = scanner.nextLine();
        if ("no".equals(response)) {
            System.out.print("Enter correct guessing key: ");
            key = scanner.nextLine();
        }

        return decrypt(encrypted, key);
    }

    private static String decrypt(String encryptedMessage, String key) {
        StringBuilder decryptBuilder = new StringBuilder(encryptedMessage.length());
        int digit = 0;
        for (char encryptedChar : encryptedMessage.toCharArray()) {
            char keyForDigit = key.charAt(digit);

            int decryptedCharInt = encryptedChar - keyForDigit;
            decryptedCharInt = decryptedCharInt >= 0 ? decryptedCharInt : decryptedCharInt + ALPHABET_SIZE;
            char decryptedChar = (char) (decryptedCharInt + FIRST_CHAR_IN_ALPHABET);
            decryptBuilder.append(decryptedChar);

            digit = (digit + 1) % key.length();
        }
        return decryptBuilder.toString();
    }

    private static Set<Integer> getKeysByKasiski(String encryptedString) {
        Set<Integer> repeatsStarts = new TreeSet<>();
        Map<String, Set<Integer>> substring2repeats = new TreeMap<>();
        String uncheckedText = encryptedString;
        int maxRepeatedSubstringLength = 0;
        int repeatIndx;
        int checkedTextLength = 0;

        for (int substringLength = 10; substringLength >= 3; substringLength--) {
            int substringStart = 0;
            int substringEnd = substringLength;
            while (substringEnd != encryptedString.length()) {
                String substring = encryptedString.substring(substringStart, substringEnd);
                int repeatInUncheckedTextIdx = uncheckedText.indexOf(substring);
                if (isSubstringExist(repeatInUncheckedTextIdx)) {
                    repeatIndx = repeatInUncheckedTextIdx + checkedTextLength;
                    if (isNewRepeatIndx(repeatsStarts, repeatIndx)) {
                        repeatsStarts.add(repeatIndx);
                        uncheckedText = uncheckedText.substring(repeatInUncheckedTextIdx + substringLength);
                        checkedTextLength = repeatIndx + substringLength;
                    } else {
                        substringStart++;
                        substringEnd++;
                    }
                } else {
                    substringStart++;
                    substringEnd++;
                    uncheckedText = encryptedString;
                    checkedTextLength = 0;
                    if (repeatsStarts.size() > 1) {
                        substring2repeats.put(substring, new TreeSet<>(repeatsStarts));
                        maxRepeatedSubstringLength = max(maxRepeatedSubstringLength, substringLength);
                    }
                    repeatsStarts.clear();
                }
            }
        }

        int kl = Vizhener.KEY_WORD.length();
        Map<Integer, Map<Integer, List<Integer>>> length2factors = new TreeMap<>(reverseOrder());
        Set<Integer> distances = new TreeSet<>();
        substring2repeats.forEach((key, value) -> {
            Integer[] repeatStarts = value.toArray(new Integer[0]);
            int substringLength = key.length();
            Map<Integer, List<Integer>> distance2factors = new HashMap<>();
            List<Integer> factors = new ArrayList<>();
            for (int i = 1; i < repeatStarts.length; i++) {
                int current = repeatStarts[i];
                int prev = repeatStarts[i - 1];
                int distance = current - prev;
                for (int factor = 3; factor <= 20; factor++) { // Полагаем, что ключ, длиной 1 и 2 слишком мал и не больше 20
                    if (distance % factor == 0) {
                        factors.add(factor);
                    }
                }
                distances.add(distance);
                distance2factors.put(distance, factors);
            }
            if (length2factors.containsKey(substringLength)) {
                Map<Integer, List<Integer>> existingDistance2Factors = length2factors.get(substringLength);
                distance2factors.forEach((distance, newFactors) -> {
                    if (existingDistance2Factors.containsKey(distance)) {
                        existingDistance2Factors.get(distance).addAll(distance2factors.get(distance));
                        existingDistance2Factors.put(distance, existingDistance2Factors.get(distance).stream().distinct().collect(toList()));
                    } else {
                        existingDistance2Factors.put(distance, newFactors);
                    }
                });
                length2factors.put(substringLength, existingDistance2Factors);
            } else {
                length2factors.put(substringLength, new TreeMap<>(distance2factors));
            }
        });
//        System.out.println("Длина | Расстояние | Факторы");
//        System.out.println(length2factors);
        Set<Integer> factorsSet = new TreeSet<>();
        Map<Integer, Integer> factor2count = new HashMap<>();
        length2factors.forEach((length, distance2factor) ->
                distance2factor.forEach((distance, factors) ->
                        factors.forEach(factor -> {
                            factorsSet.add(factor);
                            factor2count.put(factor, factor2count.getOrDefault(factor, 0) + 1);
                        })));

//        System.out.println("Фактор | Частота");
//        System.out.println(factor2count);

        if (distances.size() > 1) {
            Integer key = findNod(distances);

            return new HashSet<>(singletonList(kl));
        }

        return factorsSet;
    }

    private static boolean isNewRepeatIndx(Set<Integer> repeats, int repeat) {
        return !repeats.contains(repeat);
    }

    private static boolean isSubstringExist(int startIndx) {
        return startIndx > -1;
    }

    private static Integer findNod(Set<Integer> distances) {
        Integer[] distArray = distances.toArray(new Integer[0]);
        List<Integer> pair = asList(distArray[0], distArray[1]);
        int nod = findPairNod(pair);
        for (int i = 2; i < distArray.length; i++) {
            if (1 == findPairNod(asList(nod, distArray[i]))) {
                continue;
            }
            nod = findPairNod(asList(nod, distArray[i]));
        }

        return nod;
    }

    private static int findPairNod(List<Integer> pair) {
        int nod;
        int first = max(pair.get(0), pair.get(1));
        int second = min(pair.get(0), pair.get(1));
        int remainder;
        while (first % second != 0) {
            remainder = first % second;
            first = second;
            second = remainder;
        }
        nod = second;

        return nod;
    }

    private static class CharacterFrequency implements Comparable<CharacterFrequency> {
        private final char character;
        private final double frequency;

        private CharacterFrequency(final char character, final double frequency) {
            this.character = character;
            this.frequency = frequency;
        }

        @Override
        public int compareTo(final CharacterFrequency o) {
            return -1 * Double.compare(frequency, o.frequency);
        }

        private char getCharacter() {
            return character;
        }

        private double getFrequency() {
            return frequency;
        }

        @Override
        public String toString() {
            return character + "=" + String.format("%.3f", frequency);
        }
    }
}
