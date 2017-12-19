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
import misc.StringUtils;
import vectorconcept.VectorConcept;

/**
 * @author audunvennesland
 * 12. des. 2017 
 */

//Should be in the format first concept uri; first concept source label; first concept name label; first concept vector; second concept uri; second concept source label; second concept name label; second concept vector; relation type
public class ClassifyerTrainingSet {
	
	public static void main(String[] args) throws AlignmentException, FileNotFoundException {
		
		//import reference alignment file
		File refalignFile = new File("./files/wordembedding/refencealignments/30304/refalign.rdf");
		
		AlignmentParser parser = new AlignmentParser();

		BasicAlignment refalign = (BasicAlignment)parser.parse(refalignFile.toURI().toString());

				
		//get a set of VectorConcepts for all concepts in the two ontologies of the alignment
		File vectorConcept303 = new File("./files/wordembedding/vector-files/vectorOutput303304-303.txt");
		File vectorConcept304 = new File("./files/wordembedding/vector-files/vectorOutput303304-304.txt");
				
		Set<VectorConcept> vc303Set = vectorconcept.VectorConcept.populate(vectorConcept303);
		Set<VectorConcept> vc304Set = vectorconcept.VectorConcept.populate(vectorConcept304);
		
		//relation type (from alignment file)
		String concept1Uri = null;
		String concept2Uri = null;
		String concept1SourceLabel = null;
		String concept2SourceLabel = null;
		String label1Name = null;
		String label2Name = null;
//		//need a string buffer for appending the vectors
//		String label1Vector = null;
//		String label2Vector = null;
//		String relation = null;
		
		
		
		ArrayList<Double> concept1Vectors = new ArrayList<Double>();
		ArrayList<Double> concept2Vectors = new ArrayList<Double>();
		
		PrintWriter out = new PrintWriter("./files/wordembedding/classifiertraining-303304.txt");

		for (Cell c : refalign) {
			
			concept1Uri = c.getObject1AsURI().toString();
			concept2Uri = c.getObject2AsURI().toString();

			
			char a_char = concept1Uri.charAt(7);
			
			if (Character.toString(a_char).equals("o")) {
				concept1SourceLabel = "304";
				concept2SourceLabel = "303";
			} else {
				concept1SourceLabel = "303";
				concept2SourceLabel = "304";
			}
			
			
			label1Name = StringUtils.getString(c.getObject1AsURI().toString()).toLowerCase();
			label2Name = StringUtils.getString(c.getObject2AsURI().toString()).toLowerCase();
			
			//get vectors for label 1
			for (VectorConcept vc303 : vc303Set) {
				if (vc303.getConceptLabel().equals(label1Name)) {
					concept1Vectors = vc303.getLabelVectors();
				} else if (vc303.getConceptLabel().equals(label2Name)) {
					concept2Vectors = vc303.getLabelVectors();
				}
			}
			
			//get vectors for label 2
			for (VectorConcept vc304 : vc304Set) {
				if (vc304.getConceptLabel().equals(label1Name)) {
					concept1Vectors = vc304.getLabelVectors();
				} else if (vc304.getConceptLabel().equals(label2Name)) {
					concept2Vectors = vc304.getLabelVectors();
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
