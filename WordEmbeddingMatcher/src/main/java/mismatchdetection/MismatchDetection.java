package mismatchdetection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.Cell;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import extraction.VectorExtractor;
import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.parser.AlignmentParser;
import fr.inrialpes.exmo.ontosim.vector.CosineVM;
import fr.inrialpes.exmo.ontowrap.OntowrapException;
import misc.MathUtils;
import misc.StopWords;
import misc.StringUtilities;
import vectorconcept.VectorConcept;

public class MismatchDetection {

	/**
	 * An OWLOntologyManager manages a set of ontologies. It is the main point
	 * for creating, loading and accessing ontologies.
	 */
	static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	/**
	 * The OWLReasonerFactory represents a reasoner creation point.
	 */
	static OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();

	static OWLDataFactory factory = manager.getOWLDataFactory();
	
	final static String vectorFileOnto1 = "./files/ATMONTO_AIRM/vectorfiles/ATMOntoCoreMerged.txt";
	final static String vectorFileOnto2 = "./files/ATMONTO_AIRM/vectorfiles/airm-mono.txt";


	public static void main(String[] args) throws FileNotFoundException, OntowrapException, AlignmentException {
		
		//public static BasicAlignment removeConceptScopeMismatch(BasicAlignment inputAlignment) throws AlignmentException {
		File inputAlignmentFile = new File("./files/ATMONTO_AIRM/alignments/AML-ATMONTO-AIRM-05.rdf");
		AlignmentParser parser = new AlignmentParser();
		BasicAlignment inputAlignment = (BasicAlignment) parser.parse(inputAlignmentFile.toURI().toString());
		
		BasicAlignment filteredAlignment = removeConceptScopeMismatch(inputAlignment);
		System.out.println("The filtered alignment contains " + filteredAlignment.nbCells() + " cells");
		for (Cell c : filteredAlignment) {
			System.out.println(c.getObject1AsURI().getFragment() + " - " + c.getObject2AsURI().getFragment());
		}
		
		/* TESTING COMPARING DEFINITIONS WITH WORD EMBEDDING
		 * 
		 * String s1 = StopWords.removeStopWords("a facility where regularly scheduled aircraft arrive and depart");
		String s2 = StopWords.removeStopWords("a defined area on land or water including any buildings, installations and equipment intended to be used either wholly or in part for the arrival, departure and surface movement of aircraft");
		
		double measure = 0;
		double finalMeasure = 0;
		
		String[] s1Array = s1.split(" ");
		String[] s2Array = s2.split(" ");
		
		//see if words are in skybrary library, include only those that are
		//public static Map<String, ArrayList<Double>> createVectorMap (File vectorFile) throws FileNotFoundException {
		File skybraryEmbeddings = new File("./files/ESWC_ATMONTO_AIRM/skybrary/skybrary_trained.txt");
		Map<String, ArrayList<Double>> skybraryVectors = VectorExtractor.createVectorMap(skybraryEmbeddings);
		
		
		
		
		ArrayList<String> s1ArrayList = new ArrayList<String>();
		for (String s : s1Array) {
			if (skybraryVectors.containsKey(s)) {
				s1ArrayList.add(s);
			}
		}
		
		System.out.println("s1ArrayList (" + s1ArrayList.size() + ") contains the following words");
		for (String s : s1ArrayList) {
			System.out.println(s);
		}
		
		
		ArrayList<String> s2ArrayList = new ArrayList<String>();
		for (String s : s2Array) {
			if (skybraryVectors.containsKey(s)) {
				s2ArrayList.add(s);
			}
		}
		
		System.out.println("s2ArrayList (" + s2ArrayList.size() + ") contains the following words");
		for (String s : s2ArrayList) {
			System.out.println(s);
		}
		
		int sumWords = s1Array.length * s2Array.length;
		
		for (String string1 : s1ArrayList) {
			for (String string2 : s2ArrayList) {
				
				//if (isInCorpus1(string1) && isInCorpus2(string2)) {
				System.out.println("Measuring the sim between " + string1 + " and " + string2 + ": " + measureSemInt(string1, string2));
				measure += measureSemInt(string1, string2);
				System.out.println("The measure is now: " + measure);
				//}
			}
		}
		
		finalMeasure = measure/sumWords;

		System.out.println("The similarity is " + finalMeasure);*/
	}
	
	
	public static BasicAlignment removeConceptScopeMismatch(BasicAlignment inputAlignment) throws AlignmentException {
		BasicAlignment filteredAlignment = new BasicAlignment();
		String qualifier = null;
		for (Cell c : inputAlignment) {
			if (StringUtilities.isCompoundWord(c.getObject1AsURI().getFragment())) {
				qualifier = StringUtilities.getCompoundQualifier(c.getObject1AsURI().getFragment());
				if (qualifier.toLowerCase().equals(c.getObject2AsURI().getFragment().toLowerCase())) {
					filteredAlignment.addAlignCell(c.getObject1(), c.getObject2(), c.getRelation().getRelation(), c.getStrength());
				}
				
			} else if (StringUtilities.isCompoundWord(c.getObject2AsURI().getFragment())) {
				qualifier = StringUtilities.getCompoundQualifier(c.getObject2AsURI().getFragment());
				if (qualifier.toLowerCase().equals(c.getObject1AsURI().getFragment().toLowerCase())) {
					filteredAlignment.addAlignCell(c.getObject1(), c.getObject2(), c.getRelation().getRelation(), c.getStrength());
			}
		}
		}
		
		
		return filteredAlignment;
		
	}

	public static double measureSemInt (String s1, String s2) throws OntowrapException, FileNotFoundException {

		//String vectorFileOnto1 = "./files/ATMONTO_AIRM/vectorfiles/ATMOntoCoreMerged.txt";
		//String vectorFileOnto2 = "./files/ATMONTO_AIRM/vectorfiles/airm-mono.txt";

		File vc1File = new File(vectorFileOnto1);
		File vc2File = new File(vectorFileOnto2);

		//		//get the vector concepts for the ontology files
		vc1File = new File(vectorFileOnto1); 
		vc2File = new File(vectorFileOnto2);

		//each concept in both ontologies being matched are represented as a set of VectorConcepts
		Set<VectorConcept> vc1Set = VectorConcept.populate(vc1File);
		Set<VectorConcept> vc2Set = VectorConcept.populate(vc2File);


		//test:System.out.println("Matching " + s1 + " and " + s2);

		double[] a1 = null;
		double[] a2 = null;


		//get the vectors of the two concepts being matched
		for (VectorConcept c1 : vc1Set) {		
			if (s1.equals(c1.getConceptLabel())) {
				a1 = ArrayUtils.toPrimitive(c1.getGlobalVectors().toArray((new Double[c1.getGlobalVectors().size()])));		
			} else {
			}
		}

		for (VectorConcept c2 : vc2Set) {
			if (s2.equals(c2.getConceptLabel())) {
				a2 = ArrayUtils.toPrimitive(c2.getGlobalVectors().toArray((new Double[c2.getGlobalVectors().size()])));			
			} else {
			}
		}

		//measure the cosine similarity between the vector dimensions of these two entities
		CosineVM cosine = new CosineVM();

		double measure = 0;
		if (a1 != null && a2 != null) {

			measure = cosine.getSim(a1, a2);

		}

		if (measure > 0.0) {
			//test:System.err.println("The similarity is " + measure);
		}

		//we do not allow similarity scores above 1.0
		if (measure > 0) {
			if (measure > 1.0) {
				measure = 1.0;
			}
			return measure;
		} else {
			return 0;
		}

	}

	/**
	 * Returns the average vector of all tokens represented in the RDFS comment for an OWL class
	 * @param onto The ontology holding the OWL class
	 * @param op The OWL object property
	 * @param vectorMap The map of vectors from en input vector file
	 * @return An average vector for all (string) tokens in an RDFS comment
	 * @throws IOException
	 */
	public static String getCommentVector(OWLOntology onto, OWLObjectProperty op, Map<String, ArrayList<Double>> vectorMap) throws IOException {


		Map<String, ArrayList<Double>> allCommentVectors = new HashMap<String, ArrayList<Double>>();
		StringBuffer sb = new StringBuffer();
		String comment = getComment(onto, op);
		String commentVector = null;

		ArrayList<Double> commentVectors = new ArrayList<Double>();

		if (comment != null && !comment.isEmpty()) {

			//create tokens from comment
			ArrayList<String> tokens = StringUtilities.tokenize(comment, true);

			//put all tokens that have an associated vector in the vectorMap in allCommentVectors along with the associated vector
			for (String s : tokens) {
				if (vectorMap.containsKey(s)) {
					commentVectors = vectorMap.get(s);

					allCommentVectors.put(s, commentVectors);

				} else {
					commentVectors = null;
				}

			}

			//create average vector representing all token vectors in each comment
			ArrayList<Double> avgs = new ArrayList<Double>();

			int numVectors = 0;

			for (Entry<String, ArrayList<Double>> e : vectorMap.entrySet()) {

				numVectors = e.getValue().size();

			}

			for (int i = 0; i < numVectors; i++) {

				ArrayList<Double> temp = new ArrayList<Double>();

				for (Entry<String, ArrayList<Double>> e : allCommentVectors.entrySet()) {

					ArrayList<Double> a = e.getValue();

					temp.add(a.get(i));

				}

				double avg = 0;

				int entries = temp.size();

				for (double d : temp) {
					avg += d;
				}

				double newAvg = avg/entries;


				if (newAvg != 0.0 && !Double.isNaN(newAvg)) {
					avgs.add(newAvg);

				}

			}

			for (double d : avgs) {
				sb.append(Double.toString(MathUtils.round(d, 6)) + " ");

			}

			commentVector = sb.toString();
		} else {
			commentVector = null;
		}

		return commentVector;

	}

	/**
	 * Returns a set of string tokens from the RDFS comment associated with an OWL class
	 * @param onto The ontology holding the OWL class
	 * @param cls The OWL class
	 * @return A string representing the set of tokens from a comment without stopwords
	 * @throws IOException
	 */
	public static String getComment (OWLOntology onto, OWLObjectProperty cls) throws IOException {

		String comment = null;
		String commentWOStopWords = null;

		for(OWLAnnotation a : cls.getAnnotations(onto, factory.getRDFSComment())) {
			OWLAnnotationValue value = a.getValue();
			if(value instanceof OWLLiteral) {
				comment = ((OWLLiteral) value).getLiteral().toString();
				commentWOStopWords = StringUtilities.removeStopWordsFromString(comment);
			}
		}

		return commentWOStopWords;

	}


}
