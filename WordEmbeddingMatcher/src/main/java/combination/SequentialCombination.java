package combination;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentVisitor;
import org.semanticweb.owl.align.Cell;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import evaluation.Evaluator;
import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.BasicConfidence;
import fr.inrialpes.exmo.align.impl.BasicRelation;
import fr.inrialpes.exmo.align.impl.URIAlignment;
import fr.inrialpes.exmo.align.impl.rel.A5AlgebraRelation;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;
import fr.inrialpes.exmo.align.parser.AlignmentParser;
import misc.StringUtils;

/**
 * @author audunvennesland
 * 11. des. 2017 
 * This class takes an equivalence Alignment as input (produced by the WEMatcher) and identifies subsumption relations using superclasses of the equivalent classes.
 * So the superclasses of entity 1 in the equivalence relation subsumes entity 2 and vice versa
 */
public class SequentialCombination {

	/**
	 * This method takes an equivalence Alignment as input (produced by the WEMatcher) and identifies subsumption relations using subclasses of the equivalent classes.
	 * So the subclasses of entity 1 in the equivalence relation are subsumed by entity 2 and vice versa
	 * @param inputAlignmentFile
	 * @return
	 * @throws AlignmentException
	 * @throws OWLOntologyCreationException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static BasicAlignment wordEmbeddingSubsumptionSubclasses (File inputAlignmentFile) throws AlignmentException, OWLOntologyCreationException, IOException, URISyntaxException {

		BasicAlignment subsumptionAlignment = new URIAlignment();

		AlignmentParser parser = new AlignmentParser();
		BasicAlignment inputAlignment = (BasicAlignment) parser.parse(inputAlignmentFile.toURI().toString());

		URI onto1URI = inputAlignment.getOntology1URI();
		URI onto2URI = inputAlignment.getOntology2URI();

		//need to initialise the alignment with ontology URIs and the type of relation (e.g. A5AlgebraRelation) otherwise exceptions are thrown
		subsumptionAlignment.init( onto1URI, onto2URI, A5AlgebraRelation.class, BasicConfidence.class );

		File ontoFile1 = new File(inputAlignment.getFile1().getRawPath());
		File ontoFile2 = new File(inputAlignment.getFile2().getRawPath());

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		//get the ontologies from the alignment file
		OWLOntology onto1 = manager.loadOntologyFromOntologyDocument(ontoFile1);
		OWLOntology onto2 = manager.loadOntologyFromOntologyDocument(ontoFile2);

		Map<String, Set<String>> onto1ClassesAndSubclasses = statistics.Subclasses.getSubclasses(onto1);
		Map<String, Set<String>> onto2ClassesAndSubclasses = statistics.Subclasses.getSubclasses(onto2);


		//for each cell in the alignment
		//get all subclasses of e1 and make them subsumed by e2
		//then get all subclasses of e2 and make the subsumed by e1
		Set<String> subclasses = null;
		for (Cell c : inputAlignment) {

			if (onto1ClassesAndSubclasses.containsKey(c.getObject1().toString())) {

				subclasses = onto1ClassesAndSubclasses.get(c.getObject1().toString());

				for (String sc : subclasses) {
					subsumptionAlignment.addAlignCell(new URI(sc), c.getObject2AsURI(), "<", 1.0);
				}
			} if (onto2ClassesAndSubclasses.containsKey(c.getObject2().toString())) {

				subclasses = onto2ClassesAndSubclasses.get(c.getObject2().toString());

				for (String sc : subclasses) {
					subsumptionAlignment.addAlignCell(c.getObject1AsURI(), new URI(sc), ">", 1.0);
				}

			}
		}


		return subsumptionAlignment;


	}

	/**
	 * This method takes an equivalence Alignment as input (produced by the WEMatcher) and identifies subsumption relations using superclasses of the equivalent classes.
	 * So the superclasses of entity 1 in the equivalence relation subsumes entity 2 and vice versa
	 * @param inputAlignmentFile
	 * @return
	 * @throws AlignmentException
	 * @throws OWLOntologyCreationException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static BasicAlignment wordEmbeddingSubsumptionSuperclasses (File inputAlignmentFile) throws AlignmentException, OWLOntologyCreationException, IOException, URISyntaxException {

		BasicAlignment subsumptionAlignment = new URIAlignment();


		AlignmentParser parser = new AlignmentParser();
		BasicAlignment inputAlignment = (BasicAlignment) parser.parse(inputAlignmentFile.toURI().toString());

		URI onto1URI = inputAlignment.getOntology1URI();
		URI onto2URI = inputAlignment.getOntology2URI();

		//need to initialise the alignment with ontology URIs and the type of relation (e.g. A5AlgebraRelation) otherwise exceptions are thrown
		subsumptionAlignment.init( onto1URI, onto2URI, A5AlgebraRelation.class, BasicConfidence.class );

		File ontoFile1 = new File(inputAlignment.getFile1().getRawPath());
		File ontoFile2 = new File(inputAlignment.getFile2().getRawPath());

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		//get the ontologies from the alignment file
		OWLOntology onto1 = manager.loadOntologyFromOntologyDocument(ontoFile1);
		OWLOntology onto2 = manager.loadOntologyFromOntologyDocument(ontoFile2);

		Map<String, Set<String>> onto1ClassesAndSuperclasses = statistics.Superclasses.getSuperclasses(onto1);
		Map<String, Set<String>> onto2ClassesAndSuperclasses = statistics.Superclasses.getSuperclasses(onto2);


		Set<String> superclasses = null;

		for (Cell c : inputAlignment) {

			//if ontology 1 contains Cell.object1
			if (onto1ClassesAndSuperclasses.containsKey(c.getObject1().toString())) {

				superclasses = onto1ClassesAndSuperclasses.get(c.getObject1().toString());

				for (String sc : superclasses) {
					subsumptionAlignment.addAlignCell(new URI(sc), c.getObject2AsURI(), ">", 1.0);

				}

				//if ontology 2 contains Cell.object21
			}  if (onto2ClassesAndSuperclasses.containsKey(c.getObject1().toString())) {

				superclasses = onto1ClassesAndSuperclasses.get(c.getObject1().toString());

				for (String sc : superclasses) {
					subsumptionAlignment.addAlignCell(new URI(sc), c.getObject2AsURI(), ">", 1.0);
				}
				//if ontology 1 contains Cell.object 2
			}  if (onto1ClassesAndSuperclasses.containsKey(c.getObject2().toString())) {

				superclasses = onto1ClassesAndSuperclasses.get(c.getObject2().toString());

				for (String sc : superclasses) {
					subsumptionAlignment.addAlignCell(c.getObject1AsURI(), new URI(sc), "<", 1.0);
				}
			}
			//if ontology 2 contains Cell.object 2
			if (onto2ClassesAndSuperclasses.containsKey(c.getObject2().toString())) {

				superclasses = onto2ClassesAndSuperclasses.get(c.getObject2().toString());

				for (String sc : superclasses) {
					subsumptionAlignment.addAlignCell(c.getObject1AsURI(), new URI(sc), "<", 1.0);
				}

			}
		}


		return subsumptionAlignment;

	}

	public static BasicAlignment wordEmbeddingEquivalenceSuperclasses (File inputAlignmentFile) throws AlignmentException, OWLOntologyCreationException, IOException, URISyntaxException {

		BasicAlignment equivalenceAlignment = new URIAlignment();


		AlignmentParser parser = new AlignmentParser();
		BasicAlignment inputAlignment = (BasicAlignment) parser.parse(inputAlignmentFile.toURI().toString());

		URI onto1URI = inputAlignment.getOntology1URI();
		URI onto2URI = inputAlignment.getOntology2URI();

		//need to initialise the alignment with ontology URIs and the type of relation (e.g. A5AlgebraRelation) otherwise exceptions are thrown
		equivalenceAlignment.init( onto1URI, onto2URI, A5AlgebraRelation.class, BasicConfidence.class );

		File ontoFile1 = new File(inputAlignment.getFile1().getRawPath());
		File ontoFile2 = new File(inputAlignment.getFile2().getRawPath());

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		//get the ontologies from the alignment file
		OWLOntology onto1 = manager.loadOntologyFromOntologyDocument(ontoFile1);
		OWLOntology onto2 = manager.loadOntologyFromOntologyDocument(ontoFile2);

		//create a map holding the set of superclasses (value) for each class (key)
		Map<String, Set<String>> onto1ClassesAndSuperclasses = statistics.Superclasses.getSuperclasses(onto1);
		Map<String, Set<String>> onto2ClassesAndSuperclasses = statistics.Superclasses.getSuperclasses(onto2);

		//set of superclasses for each class in equivalence relation (will always be 1)
		Set<String> super1 = null;
		Set<String> super2 = null;

		//the superclass contained in the set of superclasses (will be transformed to URI within the coming for loop)
		String superCls1 = null;
		String superCls2 = null;


		for (Cell c : inputAlignment) {

			if (onto1ClassesAndSuperclasses.containsKey(c.getObject1().toString()) && 
					onto2ClassesAndSuperclasses.containsKey(c.getObject2().toString())) {

				//get the sets of superclasses for each class in the equivalence relation
				super1 = onto1ClassesAndSuperclasses.get(c.getObject1().toString());
				super2 = onto2ClassesAndSuperclasses.get(c.getObject2().toString());

				for (String s1 : super1) {
					superCls1 = s1;
				}

				for (String s2 : super2) {
					superCls2 = s2;
				}

				//the objects in the equivalence relation have to be URIs
				URI us1 = new URI(superCls1);
				URI us2 = new URI(superCls2);

				equivalenceAlignment.addAlignCell(us1, us2, "=", 1.0);
			}			
		}

		return equivalenceAlignment;


	}

	private static BasicAlignment mergeTwoAlignments (BasicAlignment a1, BasicAlignment a2) throws AlignmentException {

		BasicAlignment mergedAlignment = new BasicAlignment();

		mergedAlignment = (BasicAlignment) a1.clone();

		mergedAlignment.ingest(a2);

		return mergedAlignment;


	}


	/**
	 * Main method
	 * @param args
	 * @throws AlignmentException
	 * @throws OWLOntologyCreationException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static void main(String[] args) throws AlignmentException, OWLOntologyCreationException, IOException, URISyntaxException {

		File equivalenceAlignmentFile = new File("./files/wordembedding/alignments/303-304-LabelVectors-09.rdf");
		String refAlign = "./files/wordembedding/referencealignment_correctOrder/303304/303304_refalign.rdf";		
		
		AlignmentParser parser = new AlignmentParser();
		BasicAlignment originalEquivalenceAlignment = (BasicAlignment) parser.parse(equivalenceAlignmentFile.toURI().toString());

		//********* EQUIVALENCE RELATION *************

		//evaluate the equivalence alignment
		Evaluator.evaluateSingleAlignment(equivalenceAlignmentFile.getPath(), refAlign);

		

		//********* EQUIVALENCE SUPERCLASS RELATION *************

		BasicAlignment equivalenceSuperclassAlignment = wordEmbeddingEquivalenceSuperclasses(equivalenceAlignmentFile);


		System.out.println("Printing equivalenceSuperclassAlignment (" + equivalenceSuperclassAlignment.nbCells() + " cells)");

		for (Cell c : equivalenceSuperclassAlignment) {
			System.out.println(c.getObject1AsURI().getFragment() + " - " + c.getObject2AsURI().getFragment() + " - " + ((BasicRelation) (c.getRelation())).getPrettyLabel() + " - " + c.getStrength());
		}

		//print to alignment file
		String onto1 = equivalenceAlignmentFile.getName().substring(equivalenceAlignmentFile.getName().lastIndexOf("/") +1, equivalenceAlignmentFile.getName().lastIndexOf("/") + 4);
		String onto2 = equivalenceAlignmentFile.getName().substring(equivalenceAlignmentFile.getName().lastIndexOf("/") +4, equivalenceAlignmentFile.getName().lastIndexOf("/") + 8);	
		String equivalenceSuperclassAlignmentFileName = "./files/wordembedding/alignments/sequentialcombination/" + onto1 + "-" + onto2 + "_" + "EquivalenceSuperclassAlignment.rdf";

		File outputAlignmentEquivalenceSuper = new File(equivalenceSuperclassAlignmentFileName);

		PrintWriter writerEquivalenceSuper = new PrintWriter(
				new BufferedWriter(
						new FileWriter(outputAlignmentEquivalenceSuper)), true); 
		AlignmentVisitor rendererEquivalenceSuper = new RDFRendererVisitor(writerEquivalenceSuper);

		equivalenceSuperclassAlignment.render(rendererEquivalenceSuper);

		writerEquivalenceSuper.flush();
		writerEquivalenceSuper.close();

		//evaluate
		Evaluator.evaluateSingleAlignment(equivalenceSuperclassAlignmentFileName, refAlign);


		//merge the initial equivalence relation and the equivalence super relation alignment files and use this for the sub/superclass alignments		
		BasicAlignment mergedEquivalenceRelationAlignment = mergeTwoAlignments(originalEquivalenceAlignment, equivalenceSuperclassAlignment);
		
		//print to alignment file
		String mergedEquivalenceRelationAlignmentFileName = "./files/wordembedding/alignments/sequentialcombination/" + onto1 + "-" + onto2 + "_" + "MergedEquivalenceAlignment.rdf";
		
		File mergedEquivalenceRelationFile = new File(mergedEquivalenceRelationAlignmentFileName);

		PrintWriter writerMergedEquivalence = new PrintWriter(
				new BufferedWriter(
						new FileWriter(mergedEquivalenceRelationFile)), true); 
		AlignmentVisitor rendererMergedEquivalence = new RDFRendererVisitor(writerMergedEquivalence);

		mergedEquivalenceRelationAlignment.render(rendererMergedEquivalence);

		writerMergedEquivalence.flush();
		writerMergedEquivalence.close();

		
		//********* SUBSUMPTION SUPERCLASS RELATION *************

		BasicAlignment superclassAlignment = wordEmbeddingSubsumptionSuperclasses(mergedEquivalenceRelationFile);	

		System.out.println("Printing superclassAlignment (" + superclassAlignment.nbCells() + " cells)");

		//		for (Cell c : superclassAlignment) {
		//			System.out.println(c.getObject1AsURI().getFragment() + " - " + c.getObject2AsURI().getFragment() + " - " + ((BasicRelation) (c.getRelation())).getPrettyLabel() + " - " + c.getStrength());
		//		}

		//print to alignment file
		String superclassAlignmentFileName = "./files/wordembedding/alignments/sequentialcombination/" + onto1 + "-" + onto2 + "_" + "SuperclassAlignment.rdf";

		File outputAlignmentSuper = new File(superclassAlignmentFileName);

		PrintWriter writerSuper = new PrintWriter(
				new BufferedWriter(
						new FileWriter(outputAlignmentSuper)), true); 
		AlignmentVisitor rendererSuper = new RDFRendererVisitor(writerSuper);

		superclassAlignment.render(rendererSuper);

		writerSuper.flush();
		writerSuper.close();

		//evaluate
		Evaluator.evaluateSingleAlignment(superclassAlignmentFileName, refAlign);


		//********* SUBSUMPTION SUBCLASS RELATION *************

		BasicAlignment subclassAlignment = wordEmbeddingSubsumptionSubclasses(mergedEquivalenceRelationFile);

		System.out.println("\nPrinting subclassAlignment (" + subclassAlignment.nbCells() + " cells)");

		//		for (Cell c : subclassAlignment) {
		//			System.out.println(c.getObject1AsURI().getFragment() + " - " + c.getObject2AsURI().getFragment() + " - " + ((BasicRelation) (c.getRelation())).getPrettyLabel() + " - " + c.getStrength());
		//		}

		//print to alignment file
		String subclassAlignmentFileName = "./files/wordembedding/alignments/sequentialcombination/" + onto1 + "-" + onto2 + "_" + "SubclassAlignment.rdf";

		File outputAlignmentSub = new File(subclassAlignmentFileName);

		PrintWriter writerSub = new PrintWriter(
				new BufferedWriter(
						new FileWriter(outputAlignmentSub)), true); 
		AlignmentVisitor rendererSub = new RDFRendererVisitor(writerSub);

		subclassAlignment.render(rendererSub);
		writerSub.flush();
		writerSub.close();

		//evaluate
		Evaluator.evaluateSingleAlignment(subclassAlignmentFileName, refAlign);


		//********* MERGED ALIGNMENT (SUPERCLASS AND SUBCLASS) *************


		BasicAlignment mergedAlignment = mergeTwoAlignments(superclassAlignment, subclassAlignment);

		System.out.println("\nPrinting mergedAlignment (" + mergedAlignment.nbCells() + " cells)");

		//		for (Cell c : mergedAlignment) {
		//			System.out.println(c.getObject1AsURI().getFragment() + " - " + c.getObject2AsURI().getFragment() + " - " + ((BasicRelation) (c.getRelation())).getPrettyLabel() + " - " + c.getStrength());
		//		}

		//print to alignment file
		String mergedAlignmentFileName = "./files/wordembedding/alignments/sequentialcombination/" + onto1 + "-" + onto2 + "_" + "MergedAlignment.rdf";

		File outputAlignmentMerged = new File(mergedAlignmentFileName);

		PrintWriter writerMerged = new PrintWriter(
				new BufferedWriter(
						new FileWriter(outputAlignmentMerged)), true); 
		AlignmentVisitor rendererMerged = new RDFRendererVisitor(writerMerged);
		mergedAlignment.render(rendererMerged);
		writerMerged.flush();
		writerMerged.close();

		//evaluate
		Evaluator.evaluateSingleAlignment(mergedAlignmentFileName, refAlign);


		//********* MERGED ALIGNMENT (SUPERCLASS, SUBCLASS and EQUAL) *************

		//parse the original equivalence alignment
		AlignmentParser parser2 = new AlignmentParser();

		BasicAlignment equivalenceAlignment = (BasicAlignment) parser2.parse(new URI(StringUtils.convertToFileURL(equivalenceAlignmentFile.getPath())));

		//merge the mergedAlignment (subclasses + superclasses and the equivalence alignment)
		BasicAlignment mergedAlignmentSubsAndEqual = mergeTwoAlignments(mergedAlignment, mergedEquivalenceRelationAlignment);

		System.out.println("\nPrinting mergedAlignmentSubsAndEqual (" + mergedAlignmentSubsAndEqual.nbCells() + " cells)");

		for (Cell c : mergedAlignmentSubsAndEqual) {
			System.out.println(c.getObject1AsURI().getFragment() + " - " + c.getObject2AsURI().getFragment() + " - " + ((BasicRelation) (c.getRelation())).getPrettyLabel() + " - " + c.getStrength());
		}

		//print to alignment file
		String mergedSubsAndEqualAlignmentFileName = "./files/wordembedding/alignments/sequentialcombination/" + onto1 + "-" + onto2 + "_" + "MergedAlignmentSubsAndEqual.rdf";

		File outputAlignmentMergedSubsAndEqual = new File(mergedSubsAndEqualAlignmentFileName);

		PrintWriter writerMergedSubsAndEqual = new PrintWriter(
				new BufferedWriter(
						new FileWriter(outputAlignmentMergedSubsAndEqual)), true); 
		AlignmentVisitor rendererMergedSubsAndEqual = new RDFRendererVisitor(writerMergedSubsAndEqual);
		mergedAlignmentSubsAndEqual.render(rendererMergedSubsAndEqual);
		writerMergedSubsAndEqual.flush();
		writerMergedSubsAndEqual.close();

		//evaluate
		Evaluator.evaluateSingleAlignment(mergedSubsAndEqualAlignmentFileName, refAlign);
	}

}
