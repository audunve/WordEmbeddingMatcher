package misc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author audunvennesland
 * 15. des. 2017 
 */
public class StopWords {


	final static List<String> stopWordsList = Arrays.asList(
			"a", "an", "and", "are", "as", "at", "be", "but", "by",
			"for", "if", "in", "into", "is", "it",
			"no", "not", "of", "on", "or", "such",
			"that", "the", "their", "then", "there", "these",
			"they", "this", "to", "was", "will", "with"
			);


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String s="I love this phone, its super fast and there's so" +
				" much new and cool things with jelly bean....but of recently I've seen some bugs.";

		String output = removeStopWords(s);

		System.out.println(output);

	}

	public static String removeStopWords (String inputString) {

		List<String> stopWordsList = Arrays.asList(
				"a", "an", "and", "are", "as", "at", "be", "but", "by",
				"for", "if", "in", "into", "is", "it",
				"no", "not", "of", "on", "or", "such",
				"that", "the", "their", "then", "there", "these",
				"they", "this", "to", "was", "will", "with"
				);

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


}
