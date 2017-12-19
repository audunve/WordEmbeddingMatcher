package matching;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentProcess;


import fr.inrialpes.exmo.align.impl.ObjectAlignment;
import fr.inrialpes.exmo.ontosim.vector.CosineVM;
import fr.inrialpes.exmo.ontowrap.OntowrapException;
import vectorconcept.VectorConcept;


public class WEGlobalMatcher extends ObjectAlignment implements AlignmentProcess {


	public void align(Alignment alignment, Properties param) throws AlignmentException {

		try {
			// Match classes
			for ( Object cl2: ontology2().getClasses() ){
				for ( Object cl1: ontology1().getClasses() ){

					// add mapping into alignment object 
					addAlignCell(cl1,cl2, "=", wordembeddingScore(cl1,cl2));  
				}

			}

		} catch (Exception e) { e.printStackTrace(); }
	}


	public double wordembeddingScore(Object o1, Object o2) throws OntowrapException, FileNotFoundException {

		//get the vector concepts for the ontology files
		File vc1File = new File("./files/wordembedding/vector-files-single-ontology/vectorOutput301303-303.txt");
		File vc2File = new File("./files/wordembedding/vector-files-single-ontology/vectorOutput301304-304.txt");

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

		//measure the cosine similarity between the vector dimensions of these two entities
		CosineVM cosine = new CosineVM();

		double measure = 0;
		if (a1 != null && a2 != null) {

			measure = cosine.getSim(a1, a2);

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



}


