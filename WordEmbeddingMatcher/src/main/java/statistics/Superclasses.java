package statistics;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

/**
 * 
 * @author audunvennesland
 * 26. nov. 2017 
 */

public class Superclasses {

	/**
	 * Main method
	 * @param args
	 * @throws OWLOntologyCreationException
	 * @throws AlignmentException
	 */
	public static void main(String[] args) throws OWLOntologyCreationException, AlignmentException{


		final File ontologyDir = new File("./files/wordembedding/allontologies");
		File[] filesInDir = null;

		filesInDir = ontologyDir.listFiles();

		for (int i = 0; i < filesInDir.length; i++) {


			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntology onto1 = manager.loadOntologyFromOntologyDocument(filesInDir[i]);

			Map<String, Set<String>> classesAndSuperclasses = getSuperclasses(onto1);

			System.out.println("\n***** Printing classes and superclasses for " + misc.StringUtils.stripOntologyName(filesInDir[i].getName()) + " *****");

			for (Entry<String, Set<String>> e : classesAndSuperclasses.entrySet()) {

					System.out.println("\nSuperclasses of " + e.getKey() + ":");

					Set<String> supercls = e.getValue();

					for (String sup : supercls) {
						System.out.println(sup);
					}
			}
		}
	}

	/**
	 * Retrieves the subclasses for each entity in an ontology and returns a Map where the entity name is key and the set of associated subclasses is value
	 * @param onto the input OWLOntology
	 * @return Map<String, Set<String> where the entity name is key and the set of associated subclasses is value
	 */
	public static Map<String, Set<String>> getSuperclasses(OWLOntology onto) {

		Map<String, Set<String>> allClassesAndSuperclasses = new HashMap<String, Set<String>>();
		Map<String, Set<String>> classesAndSuperclasses = new HashMap<String, Set<String>>();


		Set<OWLClass> allClasses = onto.getClassesInSignature();

		for (OWLClass cls : allClasses) {

			allClassesAndSuperclasses.put(cls.getIRI().toString(), getEntitySuperclasses(onto, cls));
		}
		
		//only keep the entries where there are subclasses in the set
		for (Entry<String, Set<String>> e : allClassesAndSuperclasses.entrySet()) {
			
			if (!e.getKey().equals("Thing") && e.getValue().size() > 0) {
				classesAndSuperclasses.put(e.getKey(), e.getValue());
			}
		}


		return classesAndSuperclasses;

	}

	
	/**
	 * Helper method that retrieves a set of superclasses for an OWLClass (provided as parameter along with the OWLOntology which is needed for allowing the reasoner to get all superclasses for an OWLClass)
	 * @param onto the input OWLOntology
	 * @param inputClass the OWLClass for which superclasses will be retrieved
	 * @return Set<String> of superclasses for an OWLClass
	 */
	private static Set<String> getEntitySuperclasses (OWLOntology onto, OWLClass inputClass) {
		OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(onto);
		
		NodeSet<OWLClass> superclasses = reasoner.getSuperClasses(inputClass, true);

		Set<String> superclsSet = new HashSet<String>();

		for (OWLClass cls : superclasses.getFlattened()) {
			if (!cls.isOWLNothing() && !cls.isOWLThing()) {
				superclsSet.add(cls.getIRI().toString());
			}
		}

		return superclsSet;

	}



}
