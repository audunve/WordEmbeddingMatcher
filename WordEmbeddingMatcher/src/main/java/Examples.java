import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentVisitor;
import org.semanticweb.owl.align.Cell;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.BasicConfidence;
import fr.inrialpes.exmo.align.impl.URIAlignment;
import fr.inrialpes.exmo.align.impl.rel.A5AlgebraRelation;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;
import fr.inrialpes.exmo.align.parser.AlignmentParser;
import misc.StringUtilities;
import statistics.AlignmentOperations;

public class Examples {

	public static void produceCompleteReferenceAlignment (File refalignFile, File ontoFile1, File ontoFile2, String outputAlignmentFileName) throws OWLOntologyCreationException, AlignmentException, IOException {

		AlignmentParser parser = new AlignmentParser();
		BasicAlignment refalign = (BasicAlignment)parser.parse(refalignFile.toURI().toString());

		//import ontologies
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		//get the ontologies from the alignment file
		OWLOntology onto1 = manager.loadOntologyFromOntologyDocument(ontoFile1);
		OWLOntology onto2 = manager.loadOntologyFromOntologyDocument(ontoFile2);
		BasicAlignment a = new BasicAlignment();		
		URI onto1URI = onto1.getOntologyID().getOntologyIRI().toURI();
		URI onto2URI = onto2.getOntologyID().getOntologyIRI().toURI();

		//get all combinations of relations from the two input ontologies		
		for (OWLClass c : onto1.getClassesInSignature()) {		
			for (OWLClass d : onto2.getClassesInSignature()) {
				a.addAlignCell(c.getIRI().toURI(), d.getIRI().toURI(), "!", 1.0);
			}
		}	

		//remove those relations that are also in the reference alignment
		BasicAlignment alignmentWithoutDuplicates = (BasicAlignment) removeDuplicates(refalign,a);

		//ensure that only objects with proper URIs are included in the alignment
		Alignment alignmentWithProperURIs = new URIAlignment();
		for (Cell c : alignmentWithoutDuplicates) {
			if ((c.getObject1AsURI().getSchemeSpecificPart().toString().equals(onto1URI.getSchemeSpecificPart().toString()) 
					|| c.getObject1AsURI().getSchemeSpecificPart().toString().equals(onto2URI.getSchemeSpecificPart().toString())) 
					&& (c.getObject2AsURI().getSchemeSpecificPart().toString().equals(onto1URI.getSchemeSpecificPart().toString()) 
							|| c.getObject2AsURI().getSchemeSpecificPart().toString().equals(onto2URI.getSchemeSpecificPart().toString()))) {
				alignmentWithProperURIs.addAlignCell(c.getObject1(), c.getObject2(), c.getRelation().getRelation(), c.getStrength());
			}
		}
		
		//need to initialise the alignment with ontology URIs and the type of relation (e.g. A5AlgebraRelation) otherwise exceptions are thrown
		alignmentWithProperURIs.init( onto1URI, onto2URI, A5AlgebraRelation.class );

				System.out.println("The URIs are: " + alignmentWithProperURIs.getOntology1URI() + " and " + alignmentWithProperURIs.getOntology2URI());

				//print alignment to file
				File outputAlignment = new File(outputAlignmentFileName);

				PrintWriter writer = new PrintWriter(
						new BufferedWriter(
								new FileWriter(outputAlignment)), true); 
				AlignmentVisitor renderer = new RDFRendererVisitor(writer);

				alignmentWithProperURIs.render(renderer);

				System.err.println("The StringAlignment contains " + alignmentWithProperURIs.nbCells() + " correspondences");
				writer.flush();
				writer.close();
	}

	public static void main(String[] args) throws OWLOntologyCreationException, AlignmentException, IOException {
		
		File refalignFile = new File("./files/expe_oaei_2011/ref_alignments/301304/301-304.rdf");
		
		File ontoFile1 = new File("./files/expe_oaei_2011/ontologies/301304/301304-301.rdf");
		File ontoFile2 = new File("./files/expe_oaei_2011/ontologies/301304/301304-304.rdf");
		
		String alignmentFileName = "./files/version2/301304.rdf";
		
		produceCompleteReferenceAlignment (refalignFile, ontoFile1, ontoFile2, alignmentFileName);

//		//import reference alignment file
//		File refalignFile = new File("./files/expe_oaei_2011/ref_alignments/302303/302-303.rdf");
//		AlignmentParser parser = new AlignmentParser();
//		BasicAlignment refalign = (BasicAlignment)parser.parse(refalignFile.toURI().toString());
//
//		System.out.println("refalign contains " + refalign.nbCells() + " cells");
//		for (Cell c : refalign) {
//			System.out.println(c.getObject1AsURI().getFragment() + " - " + c.getObject2AsURI().getFragment() + " - " + c.getRelation().getRelation());
//		}
//
//		//import ontologies
//		File ontoFile1 = new File("./files/expe_oaei_2011/ontologies/302303/302303-302.rdf");
//		File ontoFile2 = new File("./files/expe_oaei_2011/ontologies/302303/302303-303.rdf");
//
//		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//
//		//get the ontologies from the alignment file
//		OWLOntology onto1 = manager.loadOntologyFromOntologyDocument(ontoFile1);
//		OWLOntology onto2 = manager.loadOntologyFromOntologyDocument(ontoFile2);
//
//		BasicAlignment a = new BasicAlignment();
//
//		URI onto1URI = onto1.getOntologyID().getOntologyIRI().toURI();
//		URI onto2URI = onto2.getOntologyID().getOntologyIRI().toURI();
//
//		System.out.println("onto1URI: " + onto1URI.toString());
//		System.out.println("onto2URI: " + onto2URI.toString());
//
//		//get all combinations
//
//		for (OWLClass c : onto1.getClassesInSignature()) {		
//			for (OWLClass d : onto2.getClassesInSignature()) {
//				a.addAlignCell(c.getIRI().toURI(), d.getIRI().toURI(), "!", 1.0);
//			}
//		}			
//		System.out.println("The a alignment contains " + a.nbCells() + " cells");
//
//		//ensure consistent order of URIs
//		//Alignment a_fixed = AlignmentOperations.fixEntityOrder(a);
//
//		//System.out.println("The URIs are: " + a_fixed.getOntology1URI() + " and " + a_fixed.getOntology2URI());
//
//		BasicAlignment returnedAlignment = (BasicAlignment) removeDuplicates(refalign,a);
//
//		Alignment returnedAlignmentProperObjects = new URIAlignment();
//
//		System.out.println("Printing returnedAlignment cells");
//		for (Cell c : returnedAlignment) {
//			//System.out.println("Scheme: " + c.getObject1AsURI().getSchemeSpecificPart());
//			if ((c.getObject1AsURI().getSchemeSpecificPart().toString().equals(onto1URI.getSchemeSpecificPart().toString()) 
//					|| c.getObject1AsURI().getSchemeSpecificPart().toString().equals(onto2URI.getSchemeSpecificPart().toString())) 
//					&&
//					(c.getObject2AsURI().getSchemeSpecificPart().toString().equals(onto1URI.getSchemeSpecificPart().toString()) || c.getObject2AsURI().getSchemeSpecificPart().toString().equals(onto2URI.getSchemeSpecificPart().toString()))) {
//
//				returnedAlignmentProperObjects.addAlignCell(c.getObject1(), c.getObject2(), c.getRelation().getRelation(), c.getStrength());
//			}
//		}
//
//		//need to initialise the alignment with ontology URIs and the type of relation (e.g. A5AlgebraRelation) otherwise exceptions are thrown
//		returnedAlignmentProperObjects.init( onto1URI, onto2URI, A5AlgebraRelation.class );
//
//		System.out.println("The URIs are: " + returnedAlignmentProperObjects.getOntology1URI() + " and " + returnedAlignmentProperObjects.getOntology2URI());
//
//		//print alignment to file
//
//		String alignmentFileName = "./files/version2/302303.rdf";
//
//		File outputAlignment = new File(alignmentFileName);
//
//		PrintWriter writer = new PrintWriter(
//				new BufferedWriter(
//						new FileWriter(outputAlignment)), true); 
//		AlignmentVisitor renderer = new RDFRendererVisitor(writer);
//
//		returnedAlignmentProperObjects.render(renderer);
//
//		System.err.println("The StringAlignment contains " + returnedAlignment.nbCells() + " correspondences");
//		writer.flush();
//		writer.close();

	}

	//Removes relations from the input alignment that are also in the reference alignment
	public static Alignment removeDuplicates(BasicAlignment refAlign, BasicAlignment inputAlign) throws AlignmentException {

		Alignment returnedAlignment = new BasicAlignment();

		Set<Cell> sameCells = new HashSet<Cell>();

		//put the cell (relation) that corresponds to the reference alignment in sameCells
		for (Cell c_ref : refAlign) {
			sameCells.addAll(inputAlign.getAlignCells(c_ref.getObject1(), c_ref.getObject2()));
		}

		System.out.println("sameCells contains " + sameCells.size() + " relations");

		System.out.println("These are the same cells: ");
		for (Cell c : sameCells) {
			System.out.println(c.getObject1AsURI().getFragment() + " - " + c.getObject2AsURI().getFragment() + " - " + c.getRelation().getRelation());
		}

		for (Cell c_input : inputAlign) {
			if (!sameCells.contains(c_input)) {
				returnedAlignment.addAlignCell(c_input.getObject1(), c_input.getObject2(), c_input.getRelation().getRelation(), 1.0);
			}
		}

		//unionize the relations in the reference alignment and the input alignment
		for (Cell c : refAlign) {
			returnedAlignment.addAlignCell(c.getObject1(), c.getObject2(), c.getRelation().getRelation(), c.getStrength());
		}

		return returnedAlignment;


	}
}
