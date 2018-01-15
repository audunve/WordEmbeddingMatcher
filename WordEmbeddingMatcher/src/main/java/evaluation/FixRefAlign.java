package evaluation;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.Cell;

import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.BasicConfidence;
import fr.inrialpes.exmo.align.impl.rel.A5AlgebraRelation;
import fr.inrialpes.exmo.align.parser.AlignmentParser;
import statistics.AlignmentOperations;

/**
 * Ensures that the entities in a cell are represented in the same order as the ontologies for which the alignment is produced
 * @author audunvennesland
 * 29. des. 2017 
 */
public class FixRefAlign {
	
	/*public static BasicAlignment fixAlignment (BasicAlignment inputAlignment) throws AlignmentException {
		BasicAlignment newReferenceAlignment = new BasicAlignment();
		
		URI onto1URI = inputAlignment.getOntology1URI();
		URI onto2URI = inputAlignment.getOntology2URI();
		
		System.out.println(onto1URI);
		System.out.println(onto2URI);
		
		URI entity1 = null;
		URI entity2 = null;
		String relation = null;
		double threshold = 1.0;
		
		//need to initialise the alignment with ontology URIs and the type of relation (e.g. A5AlgebraRelation) otherwise exceptions are thrown
		newReferenceAlignment.init( onto1URI, onto2URI, A5AlgebraRelation.class, BasicConfidence.class );
		
		System.out.println("\nPrinting cells");
		for (Cell c : inputAlignment) {
			if (c.getObject1AsURI().toString().contains(onto1URI.toString())) {
			System.out.println(c.getObject1AsURI());
			entity1 = c.getObject1AsURI();
			entity2 = c.getObject2AsURI();
			relation = c.getRelation().getRelation();
			newReferenceAlignment.addAlignCell(entity1, entity2, relation, threshold);
			
			} else if (c.getObject2().toString().contains(onto1URI.toString())) {
				System.out.println(c.getObject2AsURI());
				entity1 = c.getObject2AsURI();
				entity2 = c.getObject1AsURI();
				relation = c.getRelation().getRelation();
				
				if (relation.equals(">")) {
					relation = "<";
				} else if (relation.equals("<")) {
					relation = ">";
				} else {
					relation = "=";
				}
				
				newReferenceAlignment.addAlignCell(entity1, entity2, relation, threshold);
				
			}
					
		}
		System.out.println("oldReferenceAlignment contains " + inputAlignment.nbCells() + " cells");
		
		for (Cell c : inputAlignment) {
			System.out.println(c.getObject1AsURI() + " - " + c.getObject2AsURI() + " - " + c.getRelation().getRelation());
		}
		
		System.out.println("newReferenceAlignment contains " + newReferenceAlignment.nbCells() + " cells");
		
		for (Cell c : newReferenceAlignment) {
			System.out.println(c.getObject1AsURI() + " - " + c.getObject2AsURI() + " - " + c.getRelation().getRelation());
		}
		
		
		return newReferenceAlignment;
	}*/
	
	public static void main(String[] args) throws AlignmentException, IOException {
	
	//import reference alignment and parse as Alignment object
	File referenceAlignmentFile = new File("./files/OAEI2009/103/refalign.rdf");
	AlignmentParser parser = new AlignmentParser();
	BasicAlignment oldReferenceAlignment = (BasicAlignment) parser.parse(referenceAlignmentFile.toURI().toString());
	
	BasicAlignment newReferenceAlignment = AlignmentOperations.fixEntityOrder(oldReferenceAlignment);
	System.out.println("newReferenceAlignment contains " + newReferenceAlignment.nbCells() + " cells");
	System.out.println("newReferenceAlignment contains " + newReferenceAlignment.getOntology1URI());
	String filename = "./files/testAlignment.rdf";
	AlignmentOperations.printAlignment(filename, newReferenceAlignment);
	
	/*URI onto1URI = oldReferenceAlignment.getOntology1URI();
	URI onto2URI = oldReferenceAlignment.getOntology2URI();
	
	System.out.println(onto1URI);
	System.out.println(onto2URI);
	
	URI entity1 = null;
	URI entity2 = null;
	String relation = null;
	double threshold = 1.0;
	
	//need to initialise the alignment with ontology URIs and the type of relation (e.g. A5AlgebraRelation) otherwise exceptions are thrown
	newReferenceAlignment.init( onto1URI, onto2URI, A5AlgebraRelation.class, BasicConfidence.class );
	
	System.out.println("\nPrinting cells");
	for (Cell c : oldReferenceAlignment) {
		if (c.getObject1AsURI().toString().contains(onto1URI.toString())) {
		System.out.println(c.getObject1AsURI());
		entity1 = c.getObject1AsURI();
		entity2 = c.getObject2AsURI();
		relation = c.getRelation().getRelation();
		newReferenceAlignment.addAlignCell(entity1, entity2, relation, threshold);
		
		} else if (c.getObject2().toString().contains(onto1URI.toString())) {
			System.out.println(c.getObject2AsURI());
			entity1 = c.getObject2AsURI();
			entity2 = c.getObject1AsURI();
			relation = c.getRelation().getRelation();
			
			if (relation.equals(">")) {
				relation = "<";
			} else if (relation.equals("<")) {
				relation = ">";
			} else {
				relation = "=";
			}
			
			newReferenceAlignment.addAlignCell(entity1, entity2, relation, threshold);
			
		}
				
	}
	System.out.println("oldReferenceAlignment contains " + oldReferenceAlignment.nbCells() + " cells");
	
	for (Cell c : oldReferenceAlignment) {
		System.out.println(c.getObject1AsURI() + " - " + c.getObject2AsURI() + " - " + c.getRelation().getRelation());
	}
	
	System.out.println("newReferenceAlignment contains " + newReferenceAlignment.nbCells() + " cells");
	
	for (Cell c : newReferenceAlignment) {
		System.out.println(c.getObject1AsURI() + " - " + c.getObject2AsURI() + " - " + c.getRelation().getRelation());
	}*/
	
	
	}
}
