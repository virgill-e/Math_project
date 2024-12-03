package tree;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

/* ---------------------------------------------------------------- */

/*
 * Constructor
 */
public class LexicographicTreeTest {
	private static final String[] WORDS = new String[] { "a-cote", "aide", "as", "au", "aujourd'hui", "aux", "bu",
			"bus", "but", "cote", "et", "ete" };
	private static final LexicographicTree DICT = new LexicographicTree();
	private static final String FILENAME = "src/main/resources/mots/dictionnaire_FR_sans_accents.txt";

	private LexicographicTree tree;
	
	@BeforeAll
	public static void initTestDictionary() {
		for (int i = 0; i < WORDS.length; i++) {
			DICT.insertWord(WORDS[i]);
		}
	}
	
	@BeforeEach
	public void setUp() {
		this.tree = new LexicographicTree();
	}
	

	@Test
	void constructor_EmptyDictionary() {
		LexicographicTree dict = new LexicographicTree();
		assertNotNull(dict);
		assertEquals(0, dict.size());
	}

	@Test
	void insertWord_General() {
		LexicographicTree dict = new LexicographicTree();
		for (int i = 0; i < WORDS.length; i++) {
			dict.insertWord(WORDS[i]);
			assertEquals(i + 1, dict.size(), "Mot " + WORDS[i] + " non inséré");
			dict.insertWord(WORDS[i]);
			assertEquals(i + 1, dict.size(), "Mot " + WORDS[i] + " en double");
		}
	}

	@Test
	void containsWord_General() {
		for (String word : WORDS) {
			assertTrue(DICT.containsWord(word), "Mot " + word + " non trouvé");
		}
		for (String word : new String[] { "", "aid", "ai", "aides", "mot", "e" }) {
			assertFalse(DICT.containsWord(word), "Mot " + word + " inexistant trouvé");
		}
	}

	@Test
	void getWords_General() {
		assertEquals(WORDS.length, DICT.getWords("").size());
		assertArrayEquals(WORDS, DICT.getWords("").toArray());

		assertEquals(0, DICT.getWords("x").size());

		assertEquals(3, DICT.getWords("bu").size());
		assertArrayEquals(new String[] { "bu", "bus", "but" }, DICT.getWords("bu").toArray());
	}

	@Test
	void getWordsOfLength_General() {
		assertEquals(4, DICT.getWordsOfLength(3).size());
		assertArrayEquals(new String[] { "aux", "bus", "but", "ete" }, DICT.getWordsOfLength(3).toArray());
	}

	@Test
	void testEmptyTree() {
		LexicographicTree tree = new LexicographicTree();
		assertEquals(0, tree.size());
	}

	@Test
	void testInsertWord() {
		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("chat");
		assertTrue(tree.containsWord("chat"));
		assertEquals(1, tree.size());
	}

	@Test
	void testInsertDuplicateWord() {

		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("chat");
		tree.insertWord("chat");
		assertEquals(1, tree.size());
	}

	@Test
	void testInsertWordWithHyphenAndApostrophe() {

		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("aujourd'hui");
		tree.insertWord("tire-bouchon");
		assertTrue(tree.containsWord("aujourd'hui"));
		assertTrue(tree.containsWord("tire-bouchon"));
		assertEquals(2, tree.size());
	}

	@Test
	void testContainsWord() {

		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("chien");
		assertTrue(tree.containsWord("chien"));
		assertFalse(tree.containsWord("chat"));
	}

	@Test
	void testGetWordsWithPrefix() {

		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("chat");
		tree.insertWord("chien");
		tree.insertWord("cheval");
		tree.insertWord("oiseau");

		List<String> wordsWithPrefix = tree.getWords("ch");
		assertEquals(3, wordsWithPrefix.size());
		assertEquals("chat", wordsWithPrefix.get(0));
		assertEquals("cheval", wordsWithPrefix.get(1));
		assertEquals("chien", wordsWithPrefix.get(2));
	}

	@Test
	void testGetWordsOfLength() {
		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("chat");
		tree.insertWord("chien");
		tree.insertWord("cheval");
		tree.insertWord("oiseau");
		tree.insertWord("chat");
		tree.insertWord("chien");
		tree.insertWord("cheval");
		tree.insertWord("oiseau");

		List<String> wordsOfLength = tree.getWordsOfLength(4);
		assertEquals(1, wordsOfLength.size());
		assertTrue(wordsOfLength.contains("chat"));
	}

	@Test
	void testGetWordsWithEmptyPrefix() {
		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("chat");
		tree.insertWord("chien");
		tree.insertWord("cheval");
		tree.insertWord("oiseau");

		List<String> wordsWithEmptyPrefix = tree.getWords("");
		assertEquals(4, wordsWithEmptyPrefix.size());
		assertTrue(wordsWithEmptyPrefix.containsAll(Arrays.asList("chat", "cheval", "chien", "oiseau")));
	}

	@Test
	void testGetWordsWithNonExistentPrefix() {
		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("chat");
		tree.insertWord("chien");
		tree.insertWord("cheval");
		tree.insertWord("oiseau");

		List<String> wordsWithPrefix = tree.getWords("xyz");
		assertEquals(0, wordsWithPrefix.size());
	}

	@Test
	void testInsertWordWithSpecialCharacters() {
		LexicographicTree tree = new LexicographicTree();
		tree.insertWord("chat@123~");
		tree.insertWord("chien$%^");
		tree.insertWord("cheval*&(");
		tree.insertWord("oiseau)_+");

		assertTrue(tree.containsWord("chat@123~"));
		assertTrue(tree.containsWord("chien$%^"));
		assertTrue(tree.containsWord("cheval*&("));
		assertTrue(tree.containsWord("oiseau)_+"));
	}

	@Test
	void testInsertDictionnary() {
		LexicographicTree tree = new LexicographicTree(FILENAME);
		List<String> dict = tree.getWords("");
		assertEquals(327956, dict.size());
	}

	@Test
	void testSearchingForWordsOfIncreasingLength() {
		LexicographicTree dico = new LexicographicTree(FILENAME);
		for (int i = 0; i < 4; i++) {
			int total = 0;
			for (int n = 0; n <= 28; n++) {
				int count = dico.getWordsOfLength(n).size();
				total += count;
			}
			assertEquals(dico.size(), total);
		}
	}

	@Test
	void testSearchingNonExistingWordsInDictionary() {
		int repeatCount = 20;
		File file = new File(FILENAME);
		LexicographicTree dico = new LexicographicTree(FILENAME);
		for (int i = 0; i < repeatCount; i++) {
			Scanner input;
			try {
				input = new Scanner(file);
				while (input.hasNextLine()) {
					String word = input.nextLine() + "xx";
					boolean found = dico.containsWord(word);
					if (found) {
						assertTrue(false, word + " / " + word.length() + " -> " + found);
					}
				}
				input.close();
			} catch (FileNotFoundException e) {
				assertTrue(false, "File not found: " + FILENAME);
			}
		}
	}

	@Test
	void testSearchingExistingWordsInDictionary() {
		int repeatCount = 20;
		File file = new File(FILENAME);
		LexicographicTree dico = new LexicographicTree(FILENAME);
		for (int i = 0; i < repeatCount; i++) {
			Scanner input;
			try {
				input = new Scanner(file);
				while (input.hasNextLine()) {
					String word = input.nextLine();
					boolean found = dico.containsWord(word);
					if (!found) {
						assertTrue(false, word + " / " + word.length() + " -> " + found);
					}
				}
				input.close();
			} catch (FileNotFoundException e) {
				assertTrue(false, "File not found: " + FILENAME);
			}
		}
	}

	
	
	// My tests
	
	
	// InsertWord
	@Test
	void insertWordNormal() {
		// Given
		LexicographicTree dict = new LexicographicTree();
		List<String> words = new ArrayList<>();
		String word = "hello";
		
		// When
		dict.insertWord(word);
		words.add(word);
				
		// Then
		assertEquals(1, dict.size());
		assertEquals(words, dict.getWords(""));
	}
	
	
	

	
	
	// GetWords
	@Test
	void getWordsOfNulLength() {
		assertEquals(0, DICT.getWordsOfLength(0).size());	
	}
	
	@Test
	void getWordsOfNegativeLength() {
		assertEquals(0, DICT.getWordsOfLength(-5).size());	
	}
	
	@Test
	void getWordsOfTooHighLength() {
		assertEquals(0, DICT.getWordsOfLength(35).size());
	}
	
	
	
	@Test
	void getWordsInAlphabeticalOrdrerByPrefix() {
		// Given
		LexicographicTree dict = new LexicographicTree();
		List<String> words = new ArrayList<>();
		String word2 = "hello";
		String word3 = "nope";
		String word4 = "oukilest";
		String word1 = "azerbaijan";
		
		// When
		words.add(word1);
		words.add(word2);
		words.add(word3);
		words.add(word4);
		
		dict.insertWord(word4);
		dict.insertWord(word3);
		dict.insertWord(word1);
		dict.insertWord(word2);
		
		// Then
		assertEquals(words, dict.getWords(""));
	}
	
	@Test
	void getWordsInAlphabeticalOrdrerByLength() {
		// Given
		LexicographicTree dict = new LexicographicTree();
		List<String> words = new ArrayList<>();
		String word2 = "hello";
		String word3 = "nopel";
		String word4 = "oukil";
		String word1 = "azerb";
		
		// When
		words.add(word1);
		words.add(word2);
		words.add(word3);
		words.add(word4);
		
		dict.insertWord(word4);
		dict.insertWord(word3);
		dict.insertWord(word1);
		dict.insertWord(word2);
		
		// Then
		assertEquals(words, dict.getWordsOfLength(5));
	}
	
	
	@Test
	void getWordWithUppercaseAndAccents(){
		// Given
		LexicographicTree dict = new LexicographicTree(FILENAME);
		List<String> words = new ArrayList<>();
		
		// When
		words = dict.getWords("artIste");
		words = dict.getWords("téléphone");
		words = dict.getWords("héberGEMent");
		
		// Then
		assertEquals(0, words.size());
	}	
	


	@Test
	void constructor_FileNotFound() {
		LexicographicTree dict = new LexicographicTree("src/main/resources/mots/aaaaaaaaaaaaaaaaaaaaaaaaaa.txt");
		assertNotNull(dict);
		assertEquals(0, dict.size());
	}


	//-------------------------------------------------------------------------------------------------------------

	//region insertWord
	@Test
	void insertWordsWithThreeDifferentStartWord(){
		tree.insertWord("test");
		tree.insertWord("soda");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertEquals(4, tree.size());
		List<String> words = tree.getWords("");
		assertEquals(4, words.size());
		assertTrue(words.contains("test"));
		assertTrue(words.contains("soda"));
		assertTrue(words.contains("sodonium"));
		assertTrue(words.contains("coca"));
	}
	@Test
	void insertNullWord(){
		tree.insertWord(null);
		assertEquals(0, tree.size());
	}

	@Test
	void insertWordWithTab(){
		tree.insertWord("						");
		assertEquals(0, tree.size());
	}



	@Test
	void insertTwoSameWord(){
		tree.insertWord("test");
		tree.insertWord("test");
		assertEquals(1, tree.size());
	}


//endregion

	//region getSize
	@Test
	void getSizeEmptyTree(){
		assertEquals(0, tree.size());
	}

	@Test
	void getSizeTree(){
		tree.insertWord("test");
		tree.insertWord("soda");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertEquals(4, tree.size());
	}

//endregion

	//region containsWord

	@Test
	void containsEmptyTree(){
		assertFalse(tree.containsWord("test"));
	}
	@Test
	void containsWord(){
		tree.insertWord("test");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertTrue(tree.containsWord("soda"));
		assertFalse(tree.containsWord("sod"));
		assertTrue(tree.containsWord("sodonium"));
	}

	@Test
	void containsWord1500Words(){
		add1500WordsInTree(tree);
		assertTrue(contains1500WordUtils(tree));
	}

	@Test
	void containPrefixButIsNotAWord(){
		tree.insertWord("test");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertFalse(tree.containsWord("sod"));
		assertFalse(tree.containsWord("co"));
	}

	@Test
	void containWordWithEmptyString(){
		tree.insertWord("test");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertFalse(tree.containsWord(""));
	}

	@Test
	void containWordWithNull(){
		tree.insertWord("test");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertTrue(tree.containsWord(null));
	}
	//endregion

	//region getWords
	@Test
	void getWordsEmptyTree(){
		assertEquals(0, tree.getWords("test").size());
	}

	@Test
	void getWords(){
		tree.insertWord("test");
		tree.insertWord("soda");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertEquals(2, tree.getWords("sod").size());
		assertEquals(1, tree.getWords("coca").size());
		assertEquals(1, tree.getWords("coc").size());
		assertEquals(0, tree.getWords("qo").size());
	}


	@Test
	void getWordsWithNull(){
		assertThrows(NullPointerException.class, () ->tree.getWords(null));
	}

	@Test
	void getNonexistentWords(){
		tree.insertWord("test");
		tree.insertWord("soda");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertEquals(0, tree.getWords("qo").size());
		assertEquals(0, tree.getWords("sodas").size());//Extention de mot existant avec 1 seule lettre en plus
	}

	@Test
	void getNonexistentWordMoreShorterThanWordInTree(){
		tree.insertWord("tests");
		tree.insertWord("test");
		tree.insertWord("soda");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertEquals(2, tree.getWords("so").size());
		assertEquals(2, tree.getWords("test").size());
	}

	@Test
	void getWordsWithEmptyString(){
		tree.insertWord("test");
		tree.insertWord("tests");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertEquals(5, tree.getWords("").size());
		String[] words = {"coca", "soda", "sodonium", "test","tests"};
		assertArrayEquals(words, tree.getWords("").toArray());
	}
//endregion

	//region getWordsOfLength
	@Test
	void getWordsOfLengthEmptyTree(){
		assertEquals(0, tree.getWordsOfLength(4).size());
	}

	@Test
	void getWordsOfLength(){
		tree.insertWord("test");
		tree.insertWord("tests");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertEquals(3, tree.getWordsOfLength(4).size());
		assertEquals(1, tree.getWordsOfLength(8).size());
		assertEquals(0, tree.getWordsOfLength(3).size());
	}

	@Test
	void getWordOfNullLength(){
		tree.insertWord("test");
		tree.insertWord("tests");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertEquals(0, tree.getWordsOfLength(0).size());
		assertEquals(new ArrayList<>(), tree.getWordsOfLength(0));
	}

	@Test
	void getWordsOfLengthGreaterThanWordInTree(){
		tree.insertWord("test");
		tree.insertWord("tests");
		tree.insertWord("soda");
		tree.insertWord("sodonium");
		tree.insertWord("coca");
		assertEquals(0, tree.getWordsOfLength(100000000).size());
		assertEquals(new ArrayList<>(), tree.getWordsOfLength(100000000));
	}


	//endregion



	//region Utils methods
	private void add1500WordsInTree(LexicographicTree tree2){
		try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
			String line;
			while ((line = reader.readLine()) != null) {
				tree2.insertWord(line);
			}
		} catch (IOException e) {
			System.out.println("Erreur de lecture du fichier : " + e.getMessage());
		}
	}
	private boolean contains1500WordUtils(LexicographicTree tree2){
		boolean result = true;
		try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if(!tree2.containsWord(line)){
					result = false;
				}
			}
		} catch (IOException e) {
			System.out.println("Erreur de lecture du fichier : " + e.getMessage());
		}
		return result;
	}

	//endregion
	
	// CONSTRUCTOR TESTS
		@Test
		void insertsFileWordsAtConstruct() {
			// GIVEN
			String fileName = FILENAME;

			// WHEN
			LexicographicTree dict = new LexicographicTree(fileName);

			// THEN
			assertEquals(327956, dict.size());
		}



		// INSERTWORD TESTS
		@Test
		void insertWord() {
			// GIVEN
			LexicographicTree dict = new LexicographicTree();

			// THEN
			dict.insertWord("lexicographique");

			// THEN
			assertTrue(dict.containsWord("lexicographique"));
		}

		@Test
		void insertWordWithSpecialCharacters() {
			// GIVEN
			LexicographicTree dict = new LexicographicTree();

			// WHEN
			dict.insertWord("'ai+d&5-ees");

			// THEN
			assertTrue(dict.containsWord("'ai+d&5-ees"));
		}

		@Test
		void insertEmptyWord() {
			// GIVEN
			LexicographicTree dict = new LexicographicTree();

			// THEN
			dict.insertWord("      ");

			// THEN
			assertFalse(dict.containsWord("      "));
		}


		@Test
		void doesNotContainEmptyWord() {
			// GIVEN
			LexicographicTree dict = new LexicographicTree(FILENAME);

			// EXPECT
			assertFalse(dict.containsWord(""));
		}

		@Test
		void doesNotContainSpecialCharactersWord() {
			// GIVEN
			LexicographicTree dict = new LexicographicTree(FILENAME);

			// EXPECT
			assertFalse(dict.containsWord("aidée"));
		}


		// GETWORDS TESTS
		@Test
		void getsAllWordBeginningByPrefix() {
			// GIVEN
			LexicographicTree dict = new LexicographicTree();

			dict.insertWord("javascript");
			dict.insertWord("java");
			dict.insertWord("maths");
			dict.insertWord("dev-web");

			// WHEN
			var result = dict.getWords("java");

			// THEN
			assertEquals(2, result.size());
			assertEquals("java", result.get(0));
			assertEquals("javascript", result.get(1));
		}

		@Test
		void getsAllWordWithEmptyPrefix() {
			// GIVEN
			LexicographicTree dict = new LexicographicTree();

			dict.insertWord("javascript");
			dict.insertWord("maths");
			dict.insertWord("dev-web");

			// WHEN
			var result = dict.getWords("   ");

			// THEN
			assertEquals(3, result.size());
			assertEquals("dev-web", result.get(0));
			assertEquals("javascript", result.get(1));
			assertEquals("maths", result.get(2));
		}

		@Test
		void doesNotFindWordsWithSpecialCharactersPrefix() {
			// GIVEN
			LexicographicTree dict = new LexicographicTree(FILENAME);

			// EXPECT
			assertEquals(0, dict.getWords("é").size());
		}

		@Test
		void throwIfPrefixIsNull() {
			// GIVEN
			LexicographicTree dict = new LexicographicTree();

			// EXPECT
			assertThrows(NullPointerException.class, () -> {
				dict.getWords(null);
			});
		}

		// GETWORDSOFLENGTH TESTS

		@Test
		void getsWordsOfGivenLength() {
			// GIVEN
			LexicographicTree dict = new LexicographicTree();

			dict.insertWord("javascript");
			dict.insertWord("math");
			dict.insertWord("dev-web");
			dict.insertWord("java");

			// WHEN
			var result = dict.getWordsOfLength(4);

			// THEN
			assertEquals(2, result.size());
			assertEquals("java", result.get(0));
			assertEquals("math", result.get(1));
		}

		@Test
		void getsEmptyListIfLengthIsLessOrEqualToZero() {
			// GIVEN
			LexicographicTree dict = new LexicographicTree();

			dict.insertWord("javascript");
			dict.insertWord("maths");
			dict.insertWord("dev-web");

			// WHEN
			var result = dict.getWordsOfLength(0);

			// THEN
			assertEquals(0, result.size());
		}
}