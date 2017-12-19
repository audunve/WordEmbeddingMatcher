package combination;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.Cell;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.BasicRelation;
import fr.inrialpes.exmo.align.impl.URIAlignment;
import fr.inrialpes.exmo.align.parser.AlignmentParser;

/**
 * @author audunvennesland
 * 11. des. 2017 
 */
public class SequentialCombination {

	public static BasicAlignment wordEmbeddingSubsumption (File inputAlignmentFile) throws AlignmentException, OWLOntologyCreationException, IOException, URISyntaxException {

		BasicAlignment subsumptionAlignment = new URIAlignment();

		AlignmentParser parser = new AlignmentParser();
		BasicAlignment inputAlignment = (BasicAlignment) parser.parse(inputAlignmentFile.toURI().toString());

		File ontoFile1 = new File(inputAlignment.getFile1().getRawPath());
		File ontoFile2 = new File(inputAlignment.getFile2().getRawPath());

		System.out.println("ontoFile1: " + ontoFile1.getAbsolutePath());
		System.out.println("ontoFile2: " + ontoFile2.getCanonicalPath());

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		//get the ontologies from the alignment file
		OWLOntology onto1 = manager.loadOntologyFromOntologyDocument(ontoFile1);
		OWLOntology onto2 = manager.loadOntologyFromOntologyDocument(ontoFile2);

		//public static Map<OWLClass, Set<OWLClass>> getSubclasses(OWLOntology onto) {
		Map<String, Set<String>> onto1ClassesAndSubclasses = statistics.Subclasses.getSubclasses(onto1);
		Map<String, Set<String>> onto2ClassesAndSubclasses = statistics.Subclasses.getSubclasses(onto2);

		System.out.println("Printing all OWLClasses (keys) from onto1ClassesAndSubclasses");
		for (Entry<String, Set<String>> e : onto1ClassesAndSubclasses.entrySet()) {
			System.out.println(e.getKey());
		}

		System.out.println("Printing all OWLClasses (keys) from onto2ClassesAndSubclasses");
		for (Entry<String, Set<String>> e : onto2ClassesAndSubclasses.entrySet()) {
			System.out.println(e.getKey());
		}


		//for each cell in the alignment
		//get all subclasses of e1 and make them subsumed by e2
		//then get all subclasses of e2 and make the subsumed by e1
		Set<String> subclasses = null;
		for (Cell c : inputAlignment) {

			if (onto1ClassesAndSubclasses.containsKey(c.getObject1().toString())) {
				System.out.println("The set contains " + c.getObject1());
				subclasses = onto1ClassesAndSubclasses.get(c.getObject1().toString());

				for (String sc : subclasses) {
					subsumptionAlignment.addAlignCell(new URI(sc), c.getObject2AsURI(), "<", 1.0);
				}
			} else if (onto2ClassesAndSubclasses.containsKey(c.getObject2().toString())) {
				System.out.println("The set contains " + c.getObject2());
				subclasses = onto2ClassesAndSubclasses.get(c.getObject2().toString());
				
				for (String sc : subclasses) {
					subsumptionAlignment.addAlignCell(new URI(sc), c.getObject1AsURI(), "<", 1.0);
				}
				
			}
		}
		
		//TO-DO:need to "prettylabel" the relation types
		

		return subsumptionAlignment;


	}

	public static void main(String[] args) throws AlignmentException, OWLOntologyCreationException, IOException, URISyntaxException {

		File alignmentFile = new File("./files/wordembedding/alignments/301304-301.rdf-301304-304.rdf_0.6_-LabelVectors.rdf");

		BasicAlignment resultAlignment = wordEmbeddingSubsumption(alignmentFile);

		System.out.println("The resultAlignment contains " + resultAlignment.nbCells() + " cells");

		System.out.println("Printing alignment");

		for (Cell c : resultAlignment) {
			System.err.println(c.getObject1AsURI().getFragment() + " - " + c.getObject2AsURI().getFragment() + " - " + ((BasicRelation) (c.getRelation())).getPrettyLabel() + " - " + c.getStrength());
		}
	}

}
