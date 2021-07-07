import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;

import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.Cell;

import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.URIAlignment;
import fr.inrialpes.exmo.align.parser.AlignmentParser;
import misc.StringUtilities;
import vectorconcept.VectorConcept;

/**
 * @author audunvennesland
 * 12. des. 2017 
 */

//Should be in the format first concept uri; first concept source label; first concept name label; first concept vector; second concept uri; second concept source label; second concept name label; second concept vector; relation type
public class ClassifyerTrainingSetSingleOntology {
	
	public static void main(String[] args) throws AlignmentException, FileNotFoundException {
		
		//import reference alignment file
		File refalignFile = new File("./files/wordembedding/refencealignments/301301/301301-refalign.rdf");
		
		AlignmentParser parser = new AlignmentParser();

		BasicAlignment refalign = (BasicAlignment)parser.parse(refalignFile.toURI().toString());

				
		//get a set of VectorConcepts for all concepts in the two ontologies of the alignment
		File vectorConcept301 = new File("./files/wordembedding/vector-files/vectorOutput301302-301.txt");
				
		Set<VectorConcept> vc301Set = vectorconcept.VectorConcept.populate(vectorConcept301);

		String concept1Uri = null;
		String concept2Uri = null;
		String concept1SourceLabel = "301";
		String concept2SourceLabel = "301";
		String label1Name = null;
		String label2Name = null;

		ArrayList<Double> concept1Vectors = new ArrayList<Double>();
		ArrayList<Double> concept2Vectors = new ArrayList<Double>();
		
		PrintWriter out = new PrintWriter("./files/wordembedding/classifiertraining-301301.txt");

		for (Cell c : refalign) {
			
			concept1Uri = c.getObject1AsURI().toString();
			concept2Uri = c.getObject2AsURI().toString();
			
			
			label1Name = StringUtilities.getString(c.getObject1AsURI().toString()).toLowerCase();
			label2Name = StringUtilities.getString(c.getObject2AsURI().toString()).toLowerCase();
			
			//get vectors for label 1
			for (VectorConcept vc301 : vc301Set) {
				if (vc301.getConceptLabel().equals(label1Name)) {
					concept1Vectors = vc301.getLabelVectors();
				} 
			}
			
			//get vectors for label 2
			for (VectorConcept vc301 : vc301Set) {
				if (vc301.getConceptLabel().equals(label2Name)) {
					concept1Vectors = vc301.getLabelVectors();
				} 
			}
			
			out.println("\n" + concept1Uri);
			out.println(concept1SourceLabel);
			out.println(label1Name);
			
			StringBuffer label1VectorSB = new StringBuffer();
			for (Double d303 : concept1Vectors) {
				label1VectorSB.append(Double.toString(d303) + ", ");

			}
			
			out.println(label1VectorSB);
			out.println(concept2Uri);
			out.println(concept2SourceLabel);
			out.println(label2Name);
			
			StringBuffer label2VectorSB = new StringBuffer();
			for (Double d304 : concept2Vectors) {
				label2VectorSB.append(Double.toString(d304) + ", ");

			}			
			out.println(label2VectorSB);
			out.println(c.getRelation().getRelation());
			
		}
		
		out.close();
		
		
	}
	
	

}
