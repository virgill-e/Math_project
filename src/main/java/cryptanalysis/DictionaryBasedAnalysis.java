package cryptanalysis;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import tree.LexicographicTree;

/**
 * classe effectuant la cryptanalyse
 * @author virgi
 *
 */
public class DictionaryBasedAnalysis {

	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String DICTIONARY = "src/main/resources/mots/dictionnaire_FR_sans_accents.txt";
	private static final String CRYPTOGRAM_FILE = "src/main/resources/text/Plus fort que Sherlock Holmes (cryptogram).txt";
	private static final String DECODING_ALPHABET = "VNSTBIQLWOZUEJMRYGCPDKHXAF"; // Sherlock
	private static final Pattern PATTERN_ALL_WORD = Pattern.compile("(\\w+)");
	private static final Comparator<String> COMP_STRING_BY_LENGTH = (word1, word2) -> word2.length() - word1.length();

	private final List<String> encodedWords;
	private final LexicographicTree dict;
	private final Map<Integer, List<String>> wordsByLength;
	private final Map<String, String> solvedWords;

	/*
	 * CONSTRUCTOR
	 * 
	 */
	
	/**
	 * constucteur du DictionaryBasedAnalysis recevant le cryptogram et le dictionnaire
	 * @param cryptogram
	 * @param dict
	 */
	public DictionaryBasedAnalysis(String cryptogram, LexicographicTree dict) {
		this.solvedWords = new HashMap<>();
		this.wordsByLength = new HashMap<>();
		this.dict = dict;
		this.encodedWords = new ArrayList<String>(Arrays.asList(cryptogram.split(" "))).stream()
				.filter(word -> PATTERN_ALL_WORD.matcher(word).matches() && word.length() >= 3).map(String::trim)
				.distinct().sorted(COMP_STRING_BY_LENGTH).collect(Collectors.toList());
	}

	/*
	 * PUBLIC METHODS
	 */

	/**
	 * Performs a dictionary-based analysis of the cryptogram and returns an
	 * approximated decoding alphabet.
	 * 
	 * @param alphabet The decoding alphabet from which the analysis starts
	 * @return The decoding alphabet at the end of the analysis process
	 */
	public String guessApproximatedAlphabet(String givenAlphabet) {
		if (givenAlphabet==null||givenAlphabet.length() != 26||!checkAlphabet(givenAlphabet)) {
			throw new IllegalArgumentException("the alphabet must be 26 in length");
		}
	
		String alphabet = givenAlphabet.toUpperCase();
		int score = this.alphabetScore(alphabet);
		int actualScore;
		String actualAlphabet;
		for (String encodedWord : encodedWords) {
			if (solvedWords.containsKey(encodedWord))
				continue;
			String encodedApply = applySubstitution(encodedWord, alphabet);
			// if(dict.containsWord(encodedApply))continue;
			String word = getCompatibleWord(encodedApply);
			if (word == null)
				continue;
			actualAlphabet = generateAlphabet(encodedApply, word.toUpperCase(), alphabet);
			actualScore = this.alphabetScore(actualAlphabet);

			if (actualScore > score) {
				score = actualScore;
				alphabet = actualAlphabet;
				// System.out.println(alphabet);
			}

		}

		return alphabet;
	}

	/**
	 * Applies an alphabet-specified substitution to a text.
	 * 
	 * @param text     A text
	 * @param alphabet A substitution alphabet
	 * @return The substituted text
	 */
	public static String applySubstitution(String text, String alphabet) {
		if (alphabet == null || !PATTERN_ALL_WORD.matcher(alphabet).matches() || alphabet.length() != 26)
			throw new IllegalArgumentException("incorrect alphabet.");

		if (!checkAlphabet(alphabet))
			throw new IllegalArgumentException("incorrect alphabet.");

		if (text == null) {
			throw new IllegalArgumentException("incorrect text.");

		}

		String result = "";
		for (int i = 0; i < text.length(); i++) {
			char character = text.charAt(i);
			if (character == ' ' || character == '\n') {
				result += character;
			} else {
				int index = LETTERS.indexOf(character);
				if (index != -1) {
					result += alphabet.charAt(index);
				}
			}
		}
		return result;
	}

	/*
	 * PRIVATE METHODS
	 */
	/**
	 * Compares two substitution alphabets.
	 * 
	 * @param a First substitution alphabet
	 * @param b Second substitution alphabet
	 * @return A string where differing positions are indicated with an 'x'
	 */
	private static String compareAlphabets(String a, String b) {
		String result = "";
		for (int i = 0; i < a.length(); i++) {
			result += (a.charAt(i) == b.charAt(i)) ? " " : "x";
		}
		return result;
	}

	/**
	 * mets a jour un alphabet de substitution en recenvant un mot chiffré, le mot
	 * candidat et l'alphabet actuel
	 *
	 *
	 *
	 * @param encoded
	 * @param word
	 * @param alphabet
	 * @return
	 */
	private String generateAlphabet(String encoded, String word, String alphabet) {

		Set<Character> set = new HashSet<>();
		char[] actualAlphabet = alphabet.toCharArray();
		char[] newAlphabet = new char[26];

		for (int i = 0; i < encoded.length(); i++) {
			char wordChar = word.charAt(i);
			char encodedChar = encoded.charAt(i);

			if (set.contains(wordChar))
				continue;

			int indexWord = alphabet.indexOf(wordChar);
			int indexEncoded = alphabet.indexOf(encodedChar);

			if (newAlphabet[indexWord] != 0 || newAlphabet[indexEncoded] != 0)
				continue;

			newAlphabet[indexEncoded] = wordChar;
			newAlphabet[indexWord] = encodedChar;

			set.add(wordChar);
		}

		for (int i = 0; i < newAlphabet.length; i++) {
			if (newAlphabet[i] == 0) {
				newAlphabet[i] = actualAlphabet[i];
			}
		}

		return new String(newAlphabet);
	}

	/**
	 * Load the text file pointed to by pathname into a String.
	 * 
	 * @param pathname A path to text file.
	 * @param encoding Character set used by the text file.
	 * @return A String containing the text in the file.
	 * @throws IOException
	 */
	private static String readFile(String pathname, Charset encoding) {
		String data = "";
		try {
			data = Files.readString(Paths.get(pathname), encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	private int alphabetScore(String alphabet) {
		if (alphabet.length() != 26)
			return 0;
		int score = 0;
		for (String encodedword : this.encodedWords) {
			String word = applySubstitution(encodedword, alphabet).toLowerCase();

			if (dict.containsWord(word)) {
				this.solvedWords.put(encodedword, word);
				score += 1;
			}
		}
		return score;
	}

	private String getCompatibleWord(String encodedWord) {
		List<String> words = this.wordsByLength.get(encodedWord.length());

		if (words == null) {
			words = dict.getWordsOfLength(encodedWord.length());
			this.wordsByLength.put(encodedWord.length(), words);
		}

		String encodedRepetition = wordToCorrespondence(encodedWord);
		if (encodedRepetition == null)
			return null;

		for (String word : words) {
			if (!PATTERN_ALL_WORD.matcher(word).matches())
				continue;
			String wordRepetition = wordToCorrespondence(word);
			if (wordRepetition == null)
				continue;
			if (wordRepetition.equals(encodedRepetition)) {
				return word;
			}
		}
		return null;
	}

	private String wordToCorrespondence(String word) {
		StringJoiner wordCorrespondance = new StringJoiner("");
		Map<String, Integer> letters = new HashMap<>();
		boolean repetition = false;

		for (String let : word.split("")) {
			if (!letters.containsKey(let)) {
				letters.put(let, letters.size());
			} else {
				repetition = true;
			}
			wordCorrespondance.add(String.valueOf(letters.get(let)));
		}

		if (!repetition) {
			return null;
		}
		return wordCorrespondance.toString();
	}
	
	/**
	 * verifie si le String fournit est un alphabet correctement constitué
	 * @param givenAlphabet
	 * @return
	 */
	public static boolean checkAlphabet(String givenAlphabet) {
		String alphabet = givenAlphabet.toLowerCase();


		//boolean[] letters = new boolean[26];
		Set<Character> letters=new HashSet<>();

		for (int i = 0; i < alphabet.length(); i++) {
			char character = alphabet.charAt(i);

			if (Character.isLetter(character)) {
				letters.add(Character.valueOf(character));				
			}else {
				return false;
			}
		}

		return letters.size()==26;
	}

	/*
	 * MAIN PROGRAM
	 */
	/**
	 * main
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * Load dictionary
		 */
		System.out.print("Loading dictionary... ");
		LexicographicTree dict = new LexicographicTree(DICTIONARY);
		System.out.println("done.");
		System.out.println();

		/*
		 * Load cryptogram
		 */
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
		System.out.println("*** CRYPTOGRAM ***\n" + cryptogram.substring(0, 100));
		System.out.println();

		/*
		 * Decode cryptogram
		 */
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dict);
		// String startAlphabet = LETTERS;
		String startAlphabet = "ZISHNFOBMAVQLPEUGWXTDYRJKC"; // Random alphabet
		String finalAlphabet = dba.guessApproximatedAlphabet(startAlphabet);

		// Display final results
		System.out.println();
		System.out.println("Decoding     alphabet : " + DECODING_ALPHABET);
		System.out.println("Approximated alphabet : " + finalAlphabet);
		System.out.println("Remaining differences : " + compareAlphabets(DECODING_ALPHABET, finalAlphabet));
		System.out.println();

		// Display decoded text
		System.out.println("*** DECODED TEXT ***\n" + applySubstitution(cryptogram, finalAlphabet).substring(0, 200));
		System.out.println();
	}
}