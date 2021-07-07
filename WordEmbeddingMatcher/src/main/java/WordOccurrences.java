import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import misc.StringUtilities;


/**
 * @author audunvennesland
 */
public class WordOccurrences {
	    

	//get all class labels from an ontology file
	public static ArrayList<String> getClassLabels (File ontoFile) throws OWLOntologyCreationException {
		ArrayList<String> classLabels = new ArrayList<String>();

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology onto = manager.loadOntologyFromOntologyDocument(ontoFile);

		Set<OWLClass> clsSet = onto.getClassesInSignature();
		String thisClass = null;

		for (OWLClass cl : clsSet) {
			thisClass = cl.getIRI().getFragment();
			//tokenizes the class name before adding it to the arraylist
			//classLabels.add(StringUtilities.stringTokenize(thisClass, true));
			classLabels.add(thisClass.toLowerCase());
		}

		return classLabels;
	}
	
	
	private static <K, V> Map<K, V> sortByValue(Map<K, V> map) {
	    List<Entry<K, V>> list = new LinkedList<>(map.entrySet());
	    Collections.sort(list, new Comparator<Object>() {
	        @SuppressWarnings("unchecked")
	        public int compare(Object o1, Object o2) {
	            return ((Comparable<V>) ((Map.Entry<K, V>) (o1)).getValue()).compareTo(((Map.Entry<K, V>) (o2)).getValue());
	        }
	    });

	    Map<K, V> result = new LinkedHashMap<>();
	    for (Iterator<Entry<K, V>> it = list.iterator(); it.hasNext();) {
	        Map.Entry<K, V> entry = (Map.Entry<K, V>) it.next();
	        result.put(entry.getKey(), entry.getValue());
	    }

	    return result;
	}
	
	public static String html2text(File file) throws UnsupportedEncodingException, IOException {
		StringBuffer text = new StringBuffer();
		String line = null;
		
		try(BufferedReader in = new BufferedReader(new FileReader(file))) {

		    while ((line = in.readLine()) != null) {
		    	String[] lineArray = line.split(" ");
		      // for (int i = 0; i < lineArray.length; i++) {		       
		       text.append(lineArray[0] + " ");
		       //}
		    }
		}
		
//		Scanner scanner = new Scanner(file);
//		String text = scanner.useDelimiter("\\A").next();
//		scanner.close(); 
		
		//text.append(line);
		
	    return Jsoup.parse(text.toString()).text();
	}
	
	public static void main(String[] args) throws OWLOntologyCreationException, IOException {

				
		File ontoFile = new File ("./files/manusquare/ontologies/updatedOntology.owl");		
		ArrayList<String> concepts = getClassLabels(ontoFile);
		
		File corpusFile = new File("./files/manusquare/manusquare_wikipedia_trained.txt");		
		
		String preprocessedText = html2text(corpusFile).toLowerCase();
		
		System.out.println("Size of original text: " + preprocessedText.length());
				
		Map<String, Integer> occurrenceMap = new HashMap<String, Integer>();
		
		System.out.println("Adding concepts and their occurrences to the map");
		for (String s : concepts) {
			occurrenceMap.put(s, StringUtils.countMatches(preprocessedText, s));
		}
		
		Map<String, Integer> sortedMap = sortByValue(occurrenceMap);
		
		System.out.println("Printing sorted occurrenceMap");
		for (Entry<String, Integer> e : sortedMap.entrySet()) {
			if (e.getValue() >= 1) {
			System.out.println(e.getKey() + ": " + e.getValue());
			}
		}

	}
}
