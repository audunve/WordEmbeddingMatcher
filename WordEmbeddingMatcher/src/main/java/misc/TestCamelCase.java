package misc;

import java.util.LinkedList;

public class TestCamelCase {
	
	public static void main(String[] args) {
		
		String test = "ThisIsACompound";
		
		LinkedList<String> testList = splitCamelCaseString(test);
		
		for (String s : testList) {
			System.out.println(s);
		}
	}
	
	//accept a string, like aCamelString
	//return a list containing strings, in this case, [a, Camel, String]
	public static LinkedList<String> splitCamelCaseString(String s){
	    LinkedList<String> result = new LinkedList<String>();	
	    for (String w : s.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
	    	result.add(w);
	    }    
	    return result;
	}

}
