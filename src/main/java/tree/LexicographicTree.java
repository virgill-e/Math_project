package tree;

import java.nio.file.Paths;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * class d'un arbre lexicographique
 * @author virgi
 *
 */
public class LexicographicTree {

	private Node start;
	private int size;

	private static final String DICTIONARY = "src/main/resources/mots/dictionnaire_FR_sans_accents.txt";
	/*
	 * CONSTRUCTORS
	 */

	/**
	 * Constructor : creates an empty lexicographic tree.
	 */
	public LexicographicTree() {
		this(null);
	}

	/**
	 * Constructor : creates a lexicographic tree populated with words
	 *
	 * @param filename A text file containing the words to be inserted in the tree
	 */
	public LexicographicTree(String filename) {
		start = new Node('\0');
		size = 0;
		if (filename != null) {
			try {
				var list = Files.readAllLines(Paths.get(filename));
				list.forEach(str -> this.insertWord(str));
			} catch (IOException e) {
				System.err.println("Error reading file: " + e.getMessage());			}
		}
	}

	/*
	 * PUBLIC METHODS
	 */

	/**
	 * Returns the number of words present in the lexicographic tree.
	 *
	 * @return The number of words present in the lexicographic tree
	 */
	public int size() {
		return this.size;
	}

	/**
	 * Inserts a word in the lexicographic tree if not already present.
	 *
	 * @param word A word
	 */
	public void insertWord(String word) {
		if (word==null||word.isEmpty()||word.isBlank())
			return;
		if (this.containsWord(word))
			return;
		this.start.addWord(word);
		this.size++;
	}

	/**
	 * Determines if a word is present in the lexicographic tree.
	 *
	 * @param word A word
	 * @return True if the word is present, false otherwise
	 */
	public boolean containsWord(String word) {
		return this.start.containsWord(word);
	}

	/**
	 * Returns an alphabetic list of all words starting with the supplied prefix. If
	 * 'prefix' is an empty string, all words are returned.
	 *
	 * @param givenPrefix Expected prefix
	 * @return The list of words starting with the supplied prefix
	 */
	public List<String> getWords(String givenPrefix) {
		String prefix=givenPrefix.trim();
		List<String> words = new ArrayList<>();
		Node node = getNodePrefix(prefix);
		getAllWord(node, words, prefix);
		Collections.sort(words);
		return words;
	}




	/**
	 * Returns an alphabetic list of all words of a given length. If 'length' is
	 * lower than or equal to zero, an empty list is returned.
	 *
	 * @param length Expected word length
	 * @return The list of words with the given length
	 */
	public List<String> getWordsOfLength(int length) {
		List<String> words = new ArrayList<>();
		if (length <= 0) {
			return words;
		}
		getAllWordsOfLength(this.start, words, length, "");
		Collections.sort(words);
		return words;
	}

	/*
	 * PRIVATE METHODS
	 */

	private void getAllWordsOfLength(Node node, List<String> words, int length, String currentWord) {
		if (currentWord.length() == length) {
			if (node.isFinal()) {
				words.add(currentWord);
			}
			return;
		}
		for (Node child : node.getChilds()) {
			getAllWordsOfLength(child, words, length, currentWord + child.getLetter());
		}
	}

	private void getAllWord(Node node, List<String> words, String prefix) {
		if (node == null) {
			return;
		}
		if (node.isFinal()) {
			words.add(prefix);
		}
		for (Node child : node.getChilds()) {
			getAllWord(child, words, prefix + child.getLetter());
		}
	}

	private Node getNodePrefix(String prefix) {
		Node node = this.start;
		for (int i = 0; i < prefix.length(); i++) {
			node = node.getChild(prefix.charAt(i));
			if (node == null)
				return null;
		}
		return node;

	}

	/**
	 * renvoie si il existe au moins un mot a partir du prefix
	 * @param prefix
	 * @return
	 */
	public boolean isPrefix(String prefix) {
		if(prefix==null)return false;
		Node node=getNodePrefix(prefix);
		if(node==null)return false;
		return node.getChilds().length>0?true:false;
	}

	/*
	 * TEST FUNCTIONS
	 */

	private static String numberToWordBreadthFirst(long number) {
		String word = "";
		int radix = 13;
		do {
			word = (char) ('a' + (int) (number % radix)) + word;
			number = number / radix;
		} while (number != 0);
		return word;
	}

	private static void testDictionaryPerformance(String filename) {
		long startTime;
		int repeatCount = 20;

		// Create tree from list of words
		startTime = System.currentTimeMillis();
		System.out.println("Loading dictionary...");
		LexicographicTree dico = null;
		for (int i = 0; i < repeatCount; i++) {
			dico = new LexicographicTree(filename);
		}
		System.out.println("Load time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println("Number of words : " + dico.size());
		System.out.println();

		// Search existing words in dictionary
		startTime = System.currentTimeMillis();
		System.out.println("Searching existing words in dictionary...");
		File file = new File(filename);
		for (int i = 0; i < repeatCount; i++) {
			Scanner input;
			try {
				input = new Scanner(file);
				while (input.hasNextLine()) {
					String word = input.nextLine();
					boolean found = dico.containsWord(word);
					if (!found) {
						System.out.println(word + " / " + word.length() + " -> " + found);
					}
				}
				input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Search time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println();

		// Search non-existing words in dictionary
		startTime = System.currentTimeMillis();
		System.out.println("Searching non-existing words in dictionary...");
		for (int i = 0; i < repeatCount; i++) {
			Scanner input;
			try {
				input = new Scanner(file);
				while (input.hasNextLine()) {
					String word = input.nextLine() + "xx";
					boolean found = dico.containsWord(word);
					if (found) {
						System.out.println(word + " / " + word.length() + " -> " + found);
					}
				}
				input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Search time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println();

		// Search words of increasing length in dictionary
		startTime = System.currentTimeMillis();
		System.out.println("Searching for words of increasing length...");
		for (int i = 0; i < 4; i++) {
			int total = 0;
			for (int n = 0; n <= 28; n++) {
				int count = dico.getWordsOfLength(n).size();
				total += count;
			}
			if (dico.size() != total) {
				System.out.printf("Total mismatch : dict size = %d / search total = %d\n", dico.size(), total);
			}
		}
		System.out.println("Search time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println();
	}

	private static void testDictionarySize() {
		final int MB = 1024 * 1024;
		System.out.print(Runtime.getRuntime().totalMemory() / MB + " / ");
		System.out.println(Runtime.getRuntime().maxMemory() / MB);

		LexicographicTree dico = new LexicographicTree();
		long count = 0;
		while (true) {
			dico.insertWord(numberToWordBreadthFirst(count));
			count++;
			if (count % MB == 0) {
				System.out.println(count / MB + "M -> " + Runtime.getRuntime().freeMemory() / MB);
			}
		}
	}

	/*
	 * MAIN PROGRAM
	 */

	public static void main(String[] args) {
		// CTT : test de performance insertion/recherche
		testDictionaryPerformance(DICTIONARY);

		// CST : test de taille maximale si VM -Xms2048m -Xmx2048m
		testDictionarySize();
	}


}