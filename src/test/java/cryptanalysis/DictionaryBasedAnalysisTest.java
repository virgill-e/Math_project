package cryptanalysis;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;

import tree.LexicographicTree;


public class DictionaryBasedAnalysisTest {
	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String DICTIONARY = "src/main/resources/mots/dictionnaire_FR_sans_accents.txt";
	private static final String CRYPTOGRAM_FILE = "src/main/resources/text/Plus fort que Sherlock Holmes (cryptogram).txt";
	private static final String ENCODING_ALPHABET = "YESUMZRWFNVHOBJTGPCDLAIXQK"; // Sherlock
	private static final String DECODING_ALPHABET = "VNSTBIQLWOZUEJMRYGCPDKHXAF"; // Sherlock
	private static LexicographicTree dictionary = null;
	private static String cryptogramText = null;


	@BeforeAll
	public static void initTestDictionary() {
		dictionary = new LexicographicTree(DICTIONARY);
		cryptogramText = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
	}
	
	@Test
	void applySubstitutionTest() {
		String message = "DEMANDE RENFORTS IMMEDIATEMENT";
		String encoded = "UMOYBUM PMBZJPDC FOOMUFYDMOMBD";
		assertEquals(encoded, DictionaryBasedAnalysis.applySubstitution(message, ENCODING_ALPHABET));
		assertEquals(message, DictionaryBasedAnalysis.applySubstitution(encoded, DECODING_ALPHABET));
	}

	@Test
	void guessApproximatedAlphabetTest() {
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		assertNotNull(dba);
		String alphabet = dba.guessApproximatedAlphabet(LETTERS);
		int score = 0;
		for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
			if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
		}
		assertTrue(score >= 9, "Moins de 9 correspondances trouvées [" + score + "]");
	}
	
	private static String readFile(String pathname, Charset encoding) {
		String data = "";
		try {
			data = Files.readString(Paths.get(pathname), encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}


	
	
	// My tests
	// Guess
	@Test
	void guessApproximatedAlphabetWithDecodingAlphabet() {
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		assertNotNull(dba);
		String alphabet = dba.guessApproximatedAlphabet(DECODING_ALPHABET);
		int score = 0;
		for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
			if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
		}
		assertTrue(score >= 9, "Moins de 9 correspondances trouvées [" + score + "]");
	}
	
	@Test
	void guessApproximatedAlphabetShortAlphabet() {
		// Given
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		String shortAlphabet = "AZERTYUIOP";
		
		// Then
		assertThrows(IllegalArgumentException.class, () -> dba.guessApproximatedAlphabet(shortAlphabet));
	}
	
	
	
	// Substitution
	
	@Test
	void applySubstitutionTestOnArtiste() {
		String message = "ARTISTE";
		String encoded = "YPDFCDM";
		assertEquals(encoded, DictionaryBasedAnalysis.applySubstitution(message, ENCODING_ALPHABET));
		assertEquals(message, DictionaryBasedAnalysis.applySubstitution(encoded, DECODING_ALPHABET));
	}
	
	
	@Test
	void applySubstitutionTestBadAlphabet() {
		String message = "ARTISTE";
		assertThrows(IllegalArgumentException.class, () -> DictionaryBasedAnalysis.applySubstitution(message, "ABCDEFGHIJKLMNOPQRST^^158Z"));
	}
	

	@Test
	void applySubstitution() {
		String encode_alphabet = "VNSTBIQLWOZUEJMRYGCPDKHXAF";
		String decode_alphabet = "YESUMZRWFNVHOBJTGPCDLAIXQK";
		String message = "BONJOUR, COMMENT CA VA ?";
		String encoded = "NMJOMDG SMEEBJP SV KV ";
		String decode_expected = "BONJOUR COMMENT CA VA ";
		assertEquals(encoded, DictionaryBasedAnalysis.applySubstitution(message, encode_alphabet).toUpperCase());
		assertEquals(decode_expected, DictionaryBasedAnalysis.applySubstitution(encoded, decode_alphabet).toUpperCase());
	}

	@Test
	void applySubstitutionWithNullMessage() {
		assertThrows(IllegalArgumentException.class, () -> DictionaryBasedAnalysis.applySubstitution(null, ENCODING_ALPHABET));
	}

	@Test
	void applySubstitutionWithEmptyMessage() {
		assertEquals("",DictionaryBasedAnalysis.applySubstitution("", ENCODING_ALPHABET));
	}

	@Test
	void applySubstitutionWithNullAlphabet() {
		assertThrows(IllegalArgumentException.class, () -> DictionaryBasedAnalysis.applySubstitution("BONJOUR", null));
	}

	@Test
	void applySubstitutionWithEmptyAlphabet() {
		assertThrows(IllegalArgumentException.class, () -> DictionaryBasedAnalysis.applySubstitution("BONJOUR", ""));
	}

	@Test
	void applySubstitutionWithAlphabetLessThan26Letter() {
		assertThrows(IllegalArgumentException.class, () -> DictionaryBasedAnalysis.applySubstitution("BONJOUR", "ABCDEFGHIJKLMNOPQRSTUVWXYZABC"));
	}

	@Test
	void applySubstitutionWithAlphabetMoreThan26Letter() {
		assertThrows(IllegalArgumentException.class, () -> DictionaryBasedAnalysis.applySubstitution("BONJOUR", "ABCDEFGHIJKLMNOPQRSTUVWXYZABCDS"));
	}
	@Test
	void applySubstitutionWithAlphabetWithSpecialCharacter() {
		assertThrows(IllegalArgumentException.class, () -> DictionaryBasedAnalysis.applySubstitution("BONJOUR", "ABCDËFGHIJKLMNOPQRSTUVWXYZÄBCDS!"));
	}

	@Test
	void applySubstitutionWithAlphabetWithMultipleSameLetter() {
		assertThrows(IllegalArgumentException.class, () -> DictionaryBasedAnalysis.applySubstitution("BONJOUR", "YESUMZRWFNVHOBJTGPCDLAIXQJ"));
	}

	@Test
	void applySubstitutionWithAlphabetWithSpace() {
		assertThrows(IllegalArgumentException.class, () -> DictionaryBasedAnalysis.applySubstitution("BONJOUR", "ABCDEFGHIJKLMNOPQRSTUVWXYZ ABCDS"));
	}

	@Test
	void guessAlphabetWithStartAlphabetNull(){
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogramText, dictionary);
		assertThrows(IllegalArgumentException.class, () -> dba.guessApproximatedAlphabet(null));
	}

	@Test
	void guessAlphabetWithStartAlphabetEmpty(){
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogramText, dictionary);
		assertThrows(IllegalArgumentException.class, () -> dba.guessApproximatedAlphabet(""));
	}

	@Test
	void guessAlphabetWithStartAlphabetMajLessThen26Letter(){
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogramText, dictionary);
		assertThrows(IllegalArgumentException.class, () -> dba.guessApproximatedAlphabet("ABCDEFGHIJKLMNOPQRST"));
	}

	@Test
	void guessAlphabetWithStartAlphabetMajMoreThan26Letter(){
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogramText, dictionary);
		assertThrows(IllegalArgumentException.class, () -> dba.guessApproximatedAlphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDS"));
	}

	@Test
	void guessAlphabetWithStartAlphabetMoreThan26Letter(){
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogramText, dictionary);
		assertThrows(IllegalArgumentException.class, () -> dba.guessApproximatedAlphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDS"));
	}

	@Test
	void guessAlphabetWithStartAlphabetWithSpecialCharacter(){
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogramText, dictionary);
		assertThrows(IllegalArgumentException.class, () -> dba.guessApproximatedAlphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZéàABCDS!"));
	}

	@Test
	void guessAlphabetWithStartAlphabetWithSpace(){
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogramText, dictionary);
		assertThrows(IllegalArgumentException.class, () -> dba.guessApproximatedAlphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ ABCDS"));
	}

	@Test
	void guessAlphabetWithStartAlphabetWithMultipleSameLetter(){
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogramText, dictionary);
		assertThrows(IllegalArgumentException.class, () -> dba.guessApproximatedAlphabet("YESUMZRWFNVHOBJTGPCDLAIXQJ"));
	}



	@Test
	void guessApproximatedAlphabetWithLowerCaseAlphabet() {
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		assertNotNull(dba);
		String alphabet = dba.guessApproximatedAlphabet(LETTERS.toLowerCase());
		int score = 0;
		for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
			if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
		}
		System.out.println(alphabet + "-----------------------");
		assertTrue(score >= 9, "Moins de 9 correspondances trouvées [" + score + "]");
	}
	
	// CONSTRUCTOR TESTS
		@Test
		void throwsNullPointerExceptionIfCryptogramIsNull() {
			// GIVEN
			String cryptogram = null;

			// EXPECT
			assertThrows(NullPointerException.class, () -> {
				new DictionaryBasedAnalysis(cryptogram, dictionary);
			});
		}




		




	
}
