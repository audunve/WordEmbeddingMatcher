package matching;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.ObjectAlignment;
import fr.inrialpes.exmo.align.parser.AlignmentParser;
import fr.inrialpes.exmo.ontosim.vector.CosineVM;
import fr.inrialpes.exmo.ontowrap.OntowrapException;
import vectorconcept.VectorConcept;


public class WESubsMatcher extends ObjectAlignment implements AlignmentProcess {
	
	static File ontoFile1;
	static File ontoFile2;
	
	
	/**
	 * Constructor that receives the ontology files from interacting matcher ui (currently TestMatcher.java)
	 * @param onto1 the name of the first ontology to match
	 * @param onto2 the name of the second ontology to match
	 */
	public WESubsMatcher(File onto1, File onto2) {
		ontoFile1 = onto1;
		ontoFile2 = onto2;

	}

	/**
	 * The align() method is imported from the Alignment API and is modified to use the methods declared in this class
	 */
	public void align( Alignment alignment, Properties param ) throws AlignmentException {
		try {

			// Match classes
			for ( Object cl2: ontology2().getClasses() ){
				for ( Object cl1: ontology1().getClasses() ){

					//get map from matchSubClasses2Class where the relation is the key and the value is the score
					Map<String, Double> matchingMap = matchSubClasses(cl1, cl2);
					
					// add mapping into alignment object for each entry in the matching map
					for (Map.Entry<String, Double> entry : matchingMap.entrySet()) {
						addAlignCell(cl1,cl2, entry.getKey(), entry.getValue());  
					}

				}

			}

		} catch (Exception e) { e.printStackTrace(); }
	}
	

	/*public void align(Alignment alignment, Properties param) throws AlignmentException {

		try {
			// Match classes
			for ( Object cl2: ontology2().getClasses() ){
				for ( Object cl1: ontology1().getClasses() ){

					// add mapping into alignment object 
					addAlignCell(cl1,cl2, "=", wordembeddingScore(cl1,cl2));  
				}

			}

		} catch (Exception e) { e.printStackTrace(); }
	}*/
 
	
	private Map<String, Double> matchSubClasses (Object o1, Object o2) {
	
		if (wordembeddingScore(o1, o2) > 0.9) {
			
		}
		
	}
	
	/*private Map<String, Double> matchCommonSubclasses(Object o1, Object o2) throws OWLOntologyCreationException, OntowrapException, IOException {

		String s1 = ontology1().getEntityName(o1);
		String s2 = ontology2().getEntityName(o2);
		
		//test
		//System.out.println("The concepts to be matched are: " + s1 + " and " + s2);
		
		//System.out.println("Labels are " + labelOnto1.toString() + " and " + labelOnto2.toString());

		//get the s1 node from ontology 1
		Node s1Node = getNode(s1, labelOnto1);
		//System.out.println("S1 Node retrieved: " + s1Node.toString());

		//get the s2 node from ontology 2
		Node s2Node = getNode(s2, labelOnto2);
		//System.out.println("S2 Node retrieved: " + s2Node.toString());
		
		//get the subclasses of s1
		ArrayList onto1SubClasses = getClosestChildNodesAsList(s1Node, labelOnto1);
		
		//get the subclasses of s2
		ArrayList onto2SubClasses = getClosestChildNodesAsList(s2Node, labelOnto2);

		double distance = 0;

		//map to keep the relation and matching score
		Map<String,Double> matchingMap = new HashMap<String,Double>();
				
				
		//iterate through all subclasses and s2 and compare with s1
		for (int i = 0; i < onto2SubClasses.size(); i++) {
			//System.err.println("Trying to match " + s1 + " and " + onto2SubClasses.get(i).toString().toLowerCase());
			if (s1.toLowerCase().equals(onto2SubClasses.get(i).toString().toLowerCase())) {
				distance = 1;
				matchingMap.put(isA, distance);
				
			}
		}
		
		//iterate through all subclasses of s1 and compare with s2
		for (int i = 0; i < onto1SubClasses.size(); i++) {
			//System.err.println("Trying to match " + s2 + " and " + onto1SubClasses.get(i).toString().toLowerCase());
			if(s2.toLowerCase().equals(onto1SubClasses.get(i).toString().toLowerCase()))
				distance = 1;
			matchingMap.put(hasA, distance);
		}

		return matchingMap;
	}*/

	private double wordembeddingScore(Object o1, Object o2) throws OntowrapException, FileNotFoundException {

		//get the vector concepts for ontology 301 and 302
		File vc1File = new File("./files/wordembedding/vector-files-single-ontology/vectorOutput" + ontoFile1 + ".txt");
		File vc2File = new File("./files/wordembedding/vector-files-single-ontology/vectorOutput" + ontoFile2 + ".txt");

		VectorConcept vc1 = new VectorConcept();
		VectorConcept vc2 = new VectorConcept();

		//each concept in both ontologies being matched are represented as a set of VectorConcepts
		Set<VectorConcept> vc1Set = vc1.populate(vc1File);
		Set<VectorConcept> vc2Set = vc2.populate(vc2File);

		//get the objects (entities) being matched
		String s1 = ontology1().getEntityName(o1).toLowerCase();
		String s2 = ontology2().getEntityName(o2).toLowerCase();

		double[] a1 = null;
		double[] a2 = null;

		//get the vectors of the two concepts being matched
		for (VectorConcept c1 : vc1Set) {		
			if (s1.equals(c1.getConceptLabel())) {
				a1 = ArrayUtils.toPrimitive(c1.getGlobalVectors().toArray((new Double[c1.getGlobalVectors().size()])));		
			}
		}

		for (VectorConcept c2 : vc2Set) {
			if (s2.equals(c2.getConceptLabel())) {
				a2 = ArrayUtils.toPrimitive(c2.getGlobalVectors().toArray((new Double[c2.getGlobalVectors().size()])));			
			}
		}

		CosineVM cosine = new CosineVM();

		double measure = 0;
		if (a1 != null && a2 != null) {

			measure = cosine.getSim(a1, a2);

		}


		if (measure > 0) {
			return measure;
		} else {
			return 0;
		}

	}



}


