package experiment_oaei_2011;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Properties;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owl.align.AlignmentVisitor;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;
import matching.ISubMatcher;
import matching.WEGlobalMatcher;
import matching.WELabelMatcher;

public class ExpeOAEI2011 {
	
public static void main(String[] args) throws AlignmentException, IOException, URISyntaxException, OWLOntologyCreationException {
		
		
		/*** 1. Set the similarity threshold ***/
		final double threshold = 0.9;
		final String thresholdValue = removeCharAt(String.valueOf(threshold),1);	
		
		/*** 2. Define the folder name holding all ontologies to be matched ***/		
		File topOntologiesFolder = new File ("./files/ATMONTO_AIRM/ontologies");
		//File topOntologiesFolder = new File ("./files/expe_oaei_2011/ontologies");
		File[] ontologyFolders = topOntologiesFolder.listFiles();
		
		/*** 3. Define the folder name holding all reference alignments for evaluation of the computed alignments ***/
		//File refAlignmentsFolder = new File("./files/expe_oaei_2011/ref_alignments");
		File refAlignmentsFolder = new File("./files/ATMONTO_AIRM/ref_alignments");


		/*** No need to touch these ***/
		String alignmentFileName = null;
		File outputAlignment = null;
		PrintWriter writer = null;
		AlignmentVisitor renderer = null;
		Properties params = new Properties();
		AlignmentProcess a = null;
		String onto1 = null;
		String onto2 = null;		
		String vectorFile1Name = null;
		String vectorFile2Name = null;

//		//String matcher
//		for (int i = 0; i < ontologyFolders.length; i++) {
//
//			File[] files = ontologyFolders[i].listFiles();
//
//			for (int j = 1; j < files.length; j++) {
//				System.out.println("Matching " + files[0] + " and " + files[1]);
//				
//				//used for retrieving the correct vector files and for presenting prettier names of the stored alignments 
//				onto1 = files[0].getName().substring(files[0].getName().lastIndexOf("/") +1, files[0].getName().lastIndexOf("/") + 4);
//				onto2 = files[1].getName().substring(files[1].getName().lastIndexOf("/") +4, files[1].getName().lastIndexOf("/") + 7);	
//							
//
//				//match the files using the ISUBMatcher
//				a = new ISubMatcher();
//				a.init(files[0].toURI(), files[1].toURI());
//				params = new Properties();
//				params.setProperty("", "");
//				a.align((Alignment)null, params);	
//				
//				//store the computed alignment to file
//				alignmentFileName = "./files/expe_oaei_2011/isub_alignments/" + onto1 + "-" + onto2 + "-" + "ISub" + "-" + thresholdValue + ".rdf";
//				outputAlignment = new File(alignmentFileName);
//				
//				writer = new PrintWriter(
//						new BufferedWriter(
//								new FileWriter(outputAlignment)), true); 
//				renderer = new RDFRendererVisitor(writer);
//
//				BasicAlignment ISubAlignment = (BasicAlignment)(a.clone());
//							
//				//remove all correspondences with similarity below the defined threshold
//				ISubAlignment.cut(threshold);
//
//				ISubAlignment.render(renderer);
//				
//				System.err.println("The ISub alignment contains " + ISubAlignment.nbCells() + " correspondences");
//				writer.flush();
//				writer.close();
//				
//				System.out.println("\nMatching with ISub Matcher completed!");
//
//			}
//		}
		
		 //WEMatcher
		 for (int i = 0; i < ontologyFolders.length; i++) {

			File[] files = ontologyFolders[i].listFiles();

			for (int j = 1; j < files.length; j++) {
				System.out.println("Matching " + files[0] + " and " + files[1]);
				
				//used for retrieving the correct vector files and for presenting prettier names of the stored alignments 
				onto1 = files[0].getName().substring(files[0].getName().lastIndexOf("/") +1, files[0].getName().lastIndexOf("/") + 4);
				onto2 = files[1].getName().substring(files[1].getName().lastIndexOf("/") +4, files[1].getName().lastIndexOf("/") + 7);	
								
				//get the relevant vector files for these ontologies
//				vectorFile1Name = "./files/expe_oaei_2011/vector-files-single-ontology/vectorOutput-" + onto1 + ".txt";
//				vectorFile2Name = "./files/expe_oaei_2011/vector-files-single-ontology/vectorOutput-" + onto2 + ".txt";
				vectorFile1Name = "./files/ATMONTO_AIRM/vector-files-single-ontology/vectorOutput-" + onto1 + ".txt";
				vectorFile2Name = "./files/ATMONTO_AIRM/vector-files-single-ontology/vectorOutput-" + onto2 + ".txt";

				//match the files using the WEGlobalMatcher
				a = new WEGlobalMatcher(vectorFile1Name, vectorFile2Name);
				a.init(files[0].toURI(), files[1].toURI());
				params = new Properties();
				params.setProperty("", "");
				a.align((Alignment)null, params);	
				
				//store the computed alignment to file
//				alignmentFileName = "./files/expe_oaei_2011/alignments/" + onto1 + "-" + onto2 + "-" + "WEGlobal" + "-" + thresholdValue + ".rdf";
				alignmentFileName = "./files/ATMONTO_AIRM/alignments/" + onto1 + "-" + onto2 + "-" + "WEGlobal" + "-" + thresholdValue + ".rdf";
				outputAlignment = new File(alignmentFileName);
				
				writer = new PrintWriter(
						new BufferedWriter(
								new FileWriter(outputAlignment)), true); 
				renderer = new RDFRendererVisitor(writer);

				BasicAlignment WEGlobalAlignment = (BasicAlignment)(a.clone());
							
				//remove all correspondences with similarity below the defined threshold
				WEGlobalAlignment.cut(threshold);

				WEGlobalAlignment.render(renderer);
				
				System.err.println("The WEGlobal alignment contains " + WEGlobalAlignment.nbCells() + " correspondences");
				writer.flush();
				writer.close();
				
				System.out.println("\nMatching with WEGlobal Matcher completed!");
				
				//match the files using the WELabelMatcher
				a = new WELabelMatcher(vectorFile1Name, vectorFile2Name);
				a.init(files[0].toURI(), files[1].toURI());
				params = new Properties();
				params.setProperty("", "");
				a.align((Alignment)null, params);	
				
				//store the computed alignment to file
//				alignmentFileName = "./files/expe_oaei_2011/alignments/" + onto1 + "-" + onto2 + "-" + "WELabel" + "-" + thresholdValue + ".rdf";
				alignmentFileName = "./files/ATMONTO_AIRM/alignments/" + onto1 + "-" + onto2 + "-" + "WELabel" + "-" + thresholdValue + ".rdf";
				outputAlignment = new File(alignmentFileName);
				
				writer = new PrintWriter(
						new BufferedWriter(
								new FileWriter(outputAlignment)), true); 
				renderer = new RDFRendererVisitor(writer);

				BasicAlignment WELabelAlignment = (BasicAlignment)(a.clone());
							
				//remove all correspondences with similarity below the defined threshold
				WELabelAlignment.cut(threshold);

				WELabelAlignment.render(renderer);
				
				System.err.println("The WELabel alignment contains " + WELabelAlignment.nbCells() + " correspondences");
				writer.flush();
				writer.close();
				System.out.println("Matching with WELabel Matcher completed!");
			}
		}
}
		

	public static String removeCharAt(String s, int pos) {
	      return s.substring(0, pos) + s.substring(pos + 1);
	   }

}
