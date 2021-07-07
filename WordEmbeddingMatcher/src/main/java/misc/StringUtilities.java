package misc;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
//import org.apache.jena.ext.com.google.common.collect.ArrayListMultimap;
//import org.apache.jena.ext.com.google.common.collect.Multimap;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
//import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;


public class StringUtilities {

	//private static OWLAxiomIndex ontology;
	static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	static OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();


	/**
	 * Takes a string as input and returns an arraylist of tokens from this string
	 * @param s: the input string to tokenize
	 * @param lowercase: if the output tokens should be lowercased
	 * @return an ArrayList of tokens
	 */
	public static ArrayList<String> tokenize(String s, boolean lowercase) {
		if (s == null) {
			return null;
		}

		ArrayList<String> strings = new ArrayList<String>();

		String current = "";
		Character prevC = 'x';

		for (Character c: s.toCharArray()) {

			if ((Character.isLowerCase(prevC) && Character.isUpperCase(c)) || 
					c == '_' || c == '-' || c == ' ' || c == '/' || c == '\\' || c == '>') {

				current = current.trim();

				if (current.length() > 0) {
					if (lowercase) 
						strings.add(current.toLowerCase());
					else
						strings.add(current);
				}

				current = "";
			}

			if (c != '_' && c != '-' && c != '/' && c != '\\' && c != '>') {
				current += c;
				prevC = c;
			}
		}

		current = current.trim();

		if (current.length() > 0) {
			// this check is to handle the id numbers in YAGO
			if (!(current.length() > 4 && Character.isDigit(current.charAt(0)) && 
					Character.isDigit(current.charAt(current.length()-1)))) {
				strings.add(current.toLowerCase());
			}
		}

		return strings;
	}

	/**
	 * Returns a string of tokens
	 * @param s: the input string to be tokenized
	 * @param lowercase: whether the output tokens should be in lowercase
	 * @return a string of tokens from the input string
	 */
	public static String stringTokenize(String s, boolean lowercase) {
		String result = "";

		ArrayList<String> tokens = tokenize(s, lowercase);
		for (String token: tokens) {
			result += token + " ";
		}

		return result.trim();
	}

	/**
	 * Convert from a filename to a file URL.
	 */
	public static String convertToFileURL ( String filename )
	{

		String path = new File ( filename ).getAbsolutePath ();
		if ( File.separatorChar != '/' )
		{
			path = path.replace ( File.separatorChar, '/' );
		}
		if ( !path.startsWith ( "/" ) )
		{
			path = "/" + path;
		}
		String retVal =  "file:" + path;

		return retVal;
	}


	/**
	 * Removes prefix from property names (e.g. hasCar is transformed to car)
	 * @param s: the input property name to be 
	 * @return a string without any prefix
	 */
	public static String stripPrefix(String s) {

		if (s.startsWith("has")) {
			s = s.replaceAll("^has", "");
		} else if (s.startsWith("is")) {
			s = s.replaceAll("^is", "");
		} else if (s.startsWith("is_a_")) {
			s = s.replaceAll("^is_a_", "");
		} else if (s.startsWith("has_a_")) {
			s = s.replaceAll("^has_a_", "");
		} else if (s.startsWith("was_a_")) {
			s = s.replaceAll("^was_a_", "");
		} else if (s.endsWith("By")) {
			s = s.replaceAll("By", "");
		} else if (s.endsWith("_by")) {
			s = s.replaceAll("_by^", "");
		} else if (s.endsWith("_in")) {
			s = s.replaceAll("_in^", "");
		} else if (s.endsWith("_at")) {
			s = s.replaceAll("_at^", "");
		}
		s = s.replaceAll("_", " ");
		s = stringTokenize(s,true);

		return s;
	}

	/**
	 * Takes a filename as input and removes the IRI prefix from this file
	 * @param fileName
	 * @return filename - without IRI
	 */
	public static String stripPath(String fileName) {
		String trimmedPath = fileName.substring(fileName.lastIndexOf("/") + 1);
		return trimmedPath;

	}

	/**
	 * Takes a string as input, tokenizes it, and removes stopwords from this string
	 * @param analyzer
	 * @param str
	 * @return results - as a string of tokens, without stopwords
	 */
	public static String tokenize(Analyzer analyzer, String str) {
		String result = null;
		StringBuilder sb = new StringBuilder();

		try {
			TokenStream stream  = analyzer.tokenStream(null, new StringReader(str));
			stream.reset();
			while (stream.incrementToken()) {
				sb.append(stream.getAttribute(CharTermAttribute.class).toString());
				sb.append(" ");
			}
			stream.close();
		} catch (IOException e) {

			throw new RuntimeException(e);
		}


		result = sb.toString();
		return result;
	}


	/**
	 * Returns the label from on ontology concept without any prefix
	 * @param label: an input label with a prefix (e.g. an IRI prefix) 
	 * @return a label without any prefix
	 */
	public static String getString(String label) {

		if (label.contains("#")) {
			label = label.substring(label.indexOf('#')+1);
			return label;
		}

		if (label.contains("/")) {
			label = label.substring(label.lastIndexOf('/')+1);
			return label;
		}

		return label;
	}

	/**
	 * Removes underscores from a string (replaces underscores with "no space")
	 * @param input: string with an underscore
	 * @return string without any underscores
	 */
	public static String replaceUnderscore (String input) {
		String newString = null;
		Pattern p = Pattern.compile( "_([a-zA-Z])" );
		Matcher m = p.matcher(input);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, m.group(1).toUpperCase());
		}

		m.appendTail(sb);
		newString = sb.toString();

		return newString;
	}

	/**
	 * Checks if an input string is an abbreviation (by checking if there are two consecutive uppercased letters in the string)
	 * @param s input string
	 * @return boolean stating whether the input string represents an abbreviation
	 */
	public static boolean isAbbreviation(String s) {

		boolean isAbbreviation = false;

		int counter = 0;

		//iterate through the string
		for (int i=0; i<s.length(); i++) {

			if (Character.isUpperCase(s.charAt(i))) {
				counter++;
			}
			if (counter > 2) {
				isAbbreviation = true;
			} else {
				isAbbreviation = false;
			}
		} 

		return isAbbreviation;
	}

	/**
	 * Returns the names of the ontology from the full file path (including owl or rdf suffixes)
	 * @param ontology name without suffix
	 * @return
	 */
	public static String stripOntologyName(String fileName) {

		String trimmedPath = fileName.substring(fileName.lastIndexOf("/") + 1);
		String owl = ".owl";
		String rdf = ".rdf";
		String stripped = null;

		if (fileName.endsWith(".owl")) {
			stripped = trimmedPath.substring(0, trimmedPath.indexOf(owl));
		} else {
			stripped = trimmedPath.substring(0, trimmedPath.indexOf(rdf));
		}

		return stripped;
	}

	/*	Compatibility problems with Lucene (from Alignment API) so commenting out these for now
	 * 
	 * public static String removeStopWordsfromFile(File inputFile) throws IOException {

		StringBuilder tokens = new StringBuilder();

		FileUtils fs = new FileUtils();

		String text = fs.readFileToString(inputFile);

		Analyzer analyzer = new StopAnalyzer(Version.LUCENE_36);
		TokenStream tokenStream = analyzer.tokenStream(
				LuceneConstants.CONTENTS, new StringReader(text));
		TermAttribute term = tokenStream.addAttribute(TermAttribute.class);
		while(tokenStream.incrementToken()) {
			tokens.append(term + " ");
		}

		String tokenizedText = tokens.toString();
		return tokenizedText;

	}

	public static String removeStopWordsFromString(String inputText) throws IOException {

		StringBuilder tokens = new StringBuilder();


		Analyzer analyzer = new StopAnalyzer(Version.LUCENE_36);
		TokenStream tokenStream = analyzer.tokenStream(
				LuceneConstants.CONTENTS, new StringReader(inputText));
		TermAttribute term = tokenStream.addAttribute(TermAttribute.class);
		while(tokenStream.incrementToken()) {
			tokens.append(term + " ");
		}

		String tokenizedText = tokens.toString();
		return tokenizedText;

	}*/

	public static String removeStopWordsFromString (String inputString) {

		//		List<String> stopWordsList = Arrays.asList(
		//				"a", "an", "and", "are", "as", "at", "be", "but", "by",
		//				"for", "if", "in", "into", "is", "it",
		//				"no", "not", "of", "on", "or", "such",
		//				"that", "the", "their", "then", "there", "these",
		//				"they", "this", "to", "was", "will", "with"
		//				);

		List<String> stopWordsList = Arrays.asList("i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", 
				"it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", 
				"had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", 
				"during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", 
				"any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now");

		String output;
		String[] words = inputString.split(" ");
		ArrayList<String> wordsList = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();

		for(String word : words)
		{
			String wordCompare = word.toLowerCase();
			if(!stopWordsList.contains(wordCompare))
			{
				wordsList.add(word);
			}
		}

		for (String str : wordsList){
			sb.append(str + " ");
		}

		return output = sb.toString();
	}



	public static String validateRelationType (String relType) {
		if (relType.equals("<")) {
			relType = "&lt;";
		}

		return relType;
	}

	/**
	 * Takes a string (e.g. sentence) as input and 1) splits compounds, adds space before and after special 
	 * characters (e.g. punctuations) and lowercases the sentence.
	 * @param input
	 * @return
	 */
	public static String normalizeStringCompounds(String input) {

		String outputString = null;

		if (input != null) {

			outputString = addSpace(input.toString());

		} else {
			outputString = " ";
		}

		return outputString.toLowerCase();

	}

	/**
	 * Takes a string (e.g. sentence) as input and 1) splits compounds, adds space before and after special 
	 * characters (e.g. punctuations) and lowercases the sentence.
	 * @param input
	 * @return
	 */
	public static String normalizeStringDecompounded(String input) {

		String outputString = null;

		if (input != null) {


			String[] splitSentence = input.split(" ");

			StringBuilder sb = new StringBuilder();

			String compound = null;

			for (int i = 0; i < splitSentence.length; i++) {
				if (isCompound(splitSentence[i])) {

					compound = splitCompounds(splitSentence[i]);

					System.out.println("Compound: " + compound);
					sb.append(compound + " ");

				} else {
					sb.append(splitSentence[i] + " ");
				}
			}

			outputString = addSpace(sb.toString());

		} else {
			outputString = " ";
		}

		return outputString.toLowerCase();

	}


	private static boolean isCompound(String a) {
		boolean test = false;

		String[] compounds = a.split("(?<=.)(?=\\p{Lu})");

		if (compounds.length > 1) {
			test = true;
		}



		return test;
	}

	public static boolean isCompoundWord(String s) {
		String[] compounds = s.split("(?<=.)(?=\\p{Lu})");

		if (compounds.length > 1) {
			return true;
		} else {
			return false;
		}

	}

	public static String getCompoundWordsWithSpaces (String s) {

		//System.err.println("From getCompoundWordsWithSpaces: String s is " + s);

		StringBuffer sb = new StringBuffer();

		ArrayList<String> compoundWordsList = getWordsFromCompound(s);

		//System.err.println("From getCompoundWordsWithSpaces: The size of compoundWordsList is  " + compoundWordsList.size());

		for (String word : compoundWordsList) {

			sb.append(word + " ");

		}

		String compoundWordWithSpaces = sb.toString();

		return compoundWordWithSpaces;
	}

	public static String getCompoundHead(String s) {
		String[] compounds = s.split("(?<=.)(?=\\p{Lu})");

		String compoundHead = compounds[compounds.length-1];

		return compoundHead;
	}

	public static String getCompoundQualifier(String s) {
		String[] compounds = s.split("(?<=.)(?=\\p{Lu})");

		String compoundHead = compounds[0];

		return compoundHead;
	}

	public static ArrayList<String> getWordsFromCompound (String s) {
		String[] compounds = s.split("(?<=.)(?=\\p{Lu})");

		ArrayList<String> compoundWordsList = new ArrayList<String>();

		for (int i = 0; i < compounds.length; i++) {
			compoundWordsList.add(compounds[i]);
		}

		return compoundWordsList;

	}

	public static Set<String> getWordsAsSetFromCompound (String s) {
		String[] compounds = s.split("(?<=.)(?=\\p{Lu})");

		Set<String> compoundWordsList = new HashSet<String>();

		for (int i = 0; i < compounds.length; i++) {
			compoundWordsList.add(compounds[i]);
		}

		return compoundWordsList;

	}

	public static String splitCompounds(String input) {

		//		String[] compounds = input.split("(?<=.)(?=\\p{Lu})");
		String[] compounds = input.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < compounds.length; i++) {

			sb.append(compounds[i] + " ");

		}

		String compoundedString = sb.toString();

		return compoundedString;
	}

	private static String addSpace(String input) {

		String output = input.replaceAll("([$_\\d\\w])([^$_\\d\\w]+)", "$1 $2")
				.replaceAll("([^$_\\d\\w]+)([$_\\d\\w])", "$1 $2")
				.replaceAll("\\s+", " ")
				.replaceAll("\"", "'"); //replaces double quotes with single quotes in rdfs:comments to avoid problems when generating JSON for the word embedding

		return output;        
	}




	public static void main(String args[]) throws Exception {
		String testString = "motionPicture";
		String experiment = "biblio-bibo";

		System.out.println(tokenize(testString, true));

		String onto1 = experiment.substring(0, experiment.lastIndexOf("-"));
		String onto2 = experiment.substring(experiment.lastIndexOf("-")+1, experiment.length());
		System.out.println(onto1);

		System.out.println(onto2);

		String test = "academicArticle";

		String newString = stringTokenize(test, false);

		System.out.println("Original string: " + test + ", tokenized string: " + newString);

		String prop = "hasCar";
		System.out.println("Without prefix the property name is " + stripPrefix(prop));

		String s = "Testing underscore";
		System.out.println("Without underscore: " + replaceUnderscore(s));

		String stopwordstext = "The snow is falling and it is close to christmas";
		System.out.println(removeStopWordsFromString(stopwordstext));

	}



}
