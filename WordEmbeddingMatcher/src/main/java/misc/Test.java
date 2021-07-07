package misc;

import java.util.StringTokenizer;

public class Test {

	public static void main(String[] args) {

		String sentence = "Test/is a bad (sentence): with commas, punctuation, Capitals. and; CamelCase";
		
		System.out.println(normalizeString(sentence));
		
		System.out.println(addSpace(sentence));

/*		System.out.println("Testing regex()");

		String regexOutput = addSpace(sentence);

		System.out.println(regexOutput);

		String[] splitSentence = sentence.split(" ");

		StringBuilder sb = new StringBuilder();

		String compound = null;

		for (int i = 0; i < splitSentence.length; i++) {
			if (isCompound(splitSentence[i])) {

				compound = splitCompounds(splitSentence[i]);
				sb.append(compound + " ");

			} else {
				sb.append(splitSentence[i] + " ");
			}
		}

		String outputString = addSpace(sb.toString());



		System.out.println(outputString.toLowerCase());*/

	}

	/**
	 * Takes a string (e.g. sentence) as input and 1) splits compounds, adds space before and after special 
	 * characters (e.g. punctuations) and lowercases the sentence.
	 * @param input
	 * @return
	 */
	public static String normalizeString(String input) {


		String[] splitSentence = input.split(" ");

		StringBuilder sb = new StringBuilder();

		String compound = null;

		for (int i = 0; i < splitSentence.length; i++) {
			if (isCompound(splitSentence[i])) {

				compound = splitCompounds(splitSentence[i]);
				sb.append(compound + " ");

			} else {
				sb.append(splitSentence[i] + " ");
			}
		}

		String outputString = addSpace(sb.toString());

		return outputString;

	}


	private static boolean isCompound(String a) {
		boolean test = false;

		String[] compounds = a.split("(?<=.)(?=\\p{Lu})");

		if (compounds.length > 1) {
			test = true;
		}



		return test;
	}

	private static String splitCompounds(String input) {

		String[] compounds = input.split("(?<=.)(?=\\p{Lu})");

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
				.replaceAll("\\s+", " ");

		return output;        
	}

}
