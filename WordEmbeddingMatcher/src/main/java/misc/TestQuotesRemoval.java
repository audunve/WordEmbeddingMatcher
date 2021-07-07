package misc;

import org.apache.commons.lang3.StringUtils;

public class TestQuotesRemoval {
	
	public static void main(String[] args) {
		
		String quotes = "\"constraint defined objective set cleared flight level flight . this constraint can be : - cleared flight level . category \" executive \". - requested flight level . category \" planning \" - en - route cruise level . category \" flight_plan \". \",";
		
		int numQuotes = StringUtils.countMatches(quotes, "\"");
		
		System.out.println("There are " + numQuotes + " in the sentence");
		
		String withoutQuotes = quotes.replace("\"", "");
		System.out.println(quotes);
		System.out.println(withoutQuotes);
	}

}
