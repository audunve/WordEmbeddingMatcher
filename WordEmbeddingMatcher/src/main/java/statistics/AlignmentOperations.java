package statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentVisitor;
import org.semanticweb.owl.align.Cell;

import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.BasicConfidence;
import fr.inrialpes.exmo.align.impl.URIAlignment;
import fr.inrialpes.exmo.align.impl.rel.A5AlgebraRelation;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;

/**
 * @author audunvennesland
 * 30. des. 2017 
 */
public class AlignmentOperations {

	/**
	 * Prints a BasicAlignment to file
	 * @param alignmentFileName the filename of the printed BasicAlignment
	 * @param alignment an input BasicAlignment object
	 * @throws IOException
	 * @throws AlignmentException
	 */
	public static void printAlignment(String alignmentFileName, BasicAlignment alignment) throws IOException, AlignmentException {

		File alignmentFile = new File(alignmentFileName);
		
		System.err.println("The alignmentFile is " + alignmentFile);
		System.err.println("The alignment contains " + alignment.nbCells() + " cells");
		System.err.println(alignment.getOntology1URI() + " - " + alignment.getOntology2URI());

		PrintWriter pw = new PrintWriter(
				new BufferedWriter(
						new FileWriter(alignmentFile)), true); 
		AlignmentVisitor renderer = new RDFRendererVisitor(pw);
		alignment.render(renderer);
		pw.close();

	}
	
	/**
	 * Ensures that the entities in a cell are represented in the same order as the ontology URIs
	 * @param inputAlignment
	 * @return a BasicAlignment with entities in the correct order
	 * @throws AlignmentException
	 */
	public static URIAlignment fixEntityOrder (BasicAlignment inputAlignment) throws AlignmentException {
		URIAlignment newReferenceAlignment = new URIAlignment();
		
		URI onto1URI = inputAlignment.getOntology1URI();
		URI onto2URI = inputAlignment.getOntology2URI();
		
		URI entity1 = null;
		URI entity2 = null;
		String relation = null;
		double threshold = 1.0;
		
		//need to initialise the alignment with ontology URIs and the type of relation (e.g. A5AlgebraRelation) otherwise exceptions are thrown
		newReferenceAlignment.init( onto1URI, onto2URI, A5AlgebraRelation.class, BasicConfidence.class );
		
		for (Cell c : inputAlignment) {
			if (c.getObject1AsURI().toString().contains(onto1URI.toString())) {
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

		
		return newReferenceAlignment;
	}
	

}
