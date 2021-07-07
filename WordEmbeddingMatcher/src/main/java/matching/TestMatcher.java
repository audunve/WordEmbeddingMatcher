package matching;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Properties;

//Alignment API classes
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owl.align.AlignmentVisitor;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;


public class TestMatcher {
	
	public static void main(String[] args) throws AlignmentException, IOException, URISyntaxException, OWLOntologyCreationException {
		
		//logger.info("Hello from TestMatcher");

		/*** 1. SELECT THE MATCHER TO BE RUN ***/
		final String MATCHER = "STRING";

		/*** 2. SELECT THE TWO ONTOLOGIES TO BE MATCHED ***/
		File ontoFile1 = new File("./files/ATMONTO_AIRM/ontologies/ATMOntoCoreMerged.owl");
		File ontoFile2 = new File("./files/ATMONTO_AIRM/ontologies/airm-mono.owl");
		
		/** SELECT VECTOR FILES **/		
		String vectorFile1 = "./files/ATMONTO_AIRM/vectorfiles/ATMOntoCoreMerged.txt";
		String vectorFile2 = "./files/ATMONTO_AIRM/vectorfiles/airm-mono.txt";
		
		/*** 3. SELECT THE NEO4J DATABASE FILE (FOR THE STRUCTURAL MATCHERS ONLY) ***/
		final File dbFile = new File("/Users/audunvennesland/Documents/PhD/Development/Neo4J/biblio-bibo2");
		

		/*** INITIAL VALUES, NO NEED TO TOUCH THESE ***/
		final double threshold = 0.6;
		final String thresholdValue = removeCharAt(String.valueOf(threshold),1);	
		
		
		String alignmentFileName = null;
		File outputAlignment = null;
		String ontologyParameter1 = null;
		String ontologyParameter2 = null;
		PrintWriter writer = null;
		AlignmentVisitor renderer = null;
		Properties params = new Properties();
		AlignmentProcess a = null;
		
		/*** USED FOR INCLUDING THE ONTOLOGY FILE NAMES IN THE COMPUTED ALIGNMENT FILE ***/
		//String onto1 = StringUtils.stripPath(ontoFile1.toString());
		//String onto2 = StringUtils.stripPath(ontoFile2.toString());
		String onto1 = ontoFile1.getName().substring(ontoFile1.getName().lastIndexOf("/") +1, ontoFile1.getName().lastIndexOf("/") + 4);
		String onto2 = ontoFile2.getName().substring(ontoFile2.getName().lastIndexOf("/") +4, ontoFile2.getName().lastIndexOf("/") + 7);	

		switch(MATCHER) {

		case "WE-GLOBAL":
			a = new WEGlobalMatcher(vectorFile1, vectorFile2);

			a.init(ontoFile1.toURI(), ontoFile2.toURI());
			params = new Properties();
			params.setProperty("", "");
			a.align((Alignment)null, params);	
			
			System.err.println("The a alignment contains " + a.nbCells() + " correspondences");

			alignmentFileName = "./files/ATMONTO_AIRM/alignments/" + onto1 + "-" + onto2 + "-GlobalVectors-" + thresholdValue + ".rdf";

			outputAlignment = new File(alignmentFileName);

			writer = new PrintWriter(
					new BufferedWriter(
							new FileWriter(outputAlignment)), true); 
			renderer = new RDFRendererVisitor(writer);

			BasicAlignment WEGlobalAlignment = (BasicAlignment)(a.clone());
						

			WEGlobalAlignment.cut(threshold);

			WEGlobalAlignment.render(renderer);
			
			System.err.println("The WEGlobal alignment contains " + WEGlobalAlignment.nbCells() + " correspondences");
			writer.flush();
			writer.close();

			System.out.println("Matching completed!");
			break;
			
			
		case "WE-LABEL":
			
			a = new WELabelMatcher();

			a.init(ontoFile1.toURI(), ontoFile2.toURI());
			params = new Properties();
			params.setProperty("", "");
			a.align((Alignment)null, params);	
			
			System.err.println("The a alignment contains " + a.nbCells() + " correspondences");

			alignmentFileName = "./files/ATMONTO_AIRM/alignments/" + onto1 + "-" + onto2 + "-LabelVectors-" + thresholdValue + ".rdf";

			outputAlignment = new File(alignmentFileName);

			writer = new PrintWriter(
					new BufferedWriter(
							new FileWriter(outputAlignment)), true); 
			renderer = new RDFRendererVisitor(writer);

			BasicAlignment WELabelAlignment = (BasicAlignment)(a.clone());
			
			WELabelAlignment.cut(threshold);

			WELabelAlignment.render(renderer);
			
			System.err.println("The WELabel alignment contains " + WELabelAlignment.nbCells() + " correspondences");
			writer.flush();
			writer.close();

			System.out.println("Matching completed!");
			break;
			
		
		case "STRING":
			a = new ISubMatcher();

			a.init(ontoFile1.toURI(), ontoFile2.toURI());
			params = new Properties();
			params.setProperty("", "");
			a.align((Alignment)null, params);	
			
			System.err.println("The a alignment contains " + a.nbCells() + " correspondences");

			alignmentFileName = "./files/ATMONTO_AIRM/alignments/" +  onto1 + "-" + onto2 + "-ISub-" + thresholdValue + ".rdf";

			outputAlignment = new File(alignmentFileName);

			writer = new PrintWriter(
					new BufferedWriter(
							new FileWriter(outputAlignment)), true); 
			renderer = new RDFRendererVisitor(writer);

			BasicAlignment StringAlignment = (BasicAlignment)(a.clone());
			
			

			StringAlignment.cut(threshold);

			StringAlignment.render(renderer);
			
			System.err.println("The StringAlignment contains " + StringAlignment.nbCells() + " correspondences");
			writer.flush();
			writer.close();

			System.out.println("Matching completed!");
			break;
			
			

		}
		
		

	}
	
	public static String removeCharAt(String s, int pos) {
	      return s.substring(0, pos) + s.substring(pos + 1);
	   }

	
}