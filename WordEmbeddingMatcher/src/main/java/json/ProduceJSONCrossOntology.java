package json;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentVisitor;

import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.BasicRelation;
import fr.inrialpes.exmo.align.impl.URIAlignment;
import fr.inrialpes.exmo.align.impl.rel.A5AlgebraRelation;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;
import fr.inrialpes.exmo.align.parser.AlignmentParser;
import misc.StringUtilities;

import org.semanticweb.owl.align.Cell;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import statistics.Superclasses;

/**
 * @author audunvennesland
 * 15. des. 2017 
 * This class produces a JSON file as a human-readable (and machine processable) representation of an Alignment Cell (correspondence), entity comments, and embedding vectors (if needed).
 * It creates the JSON file using two different ontologies and a reference alignment between those two ontologies for the purpose of training a machine learning method.
 */
public class ProduceJSONCrossOntology {

	/**
	 * The OWLOntologyManager is used for creating and managing an OWLOntology from the ontology file
	 */
	static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	/**
	 * The OWLReasonerFactory represents a reasoner creation point.
	 */
	static OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
	static OWLDataFactory factory = manager.getOWLDataFactory();


	/**
	 * Produces a JSON file containing all relations (permutations of relations) betewen two ontologies along with relations contained in an "official" reference alignment between the two ontologies.
	 * @param refalignFile the "official" reference alignment
	 * @param ontoFile1 input ontology 1
	 * @param ontoFile2 input ontology 2
	 * @param onto1ID a shortname of ontology 1 (e.g. "301")
	 * @param onto2ID a shortname of ontology 2 (e.g. "302")
	 * @throws OWLOntologyCreationException
	 * @throws IOException
	 * @throws AlignmentException
	 */
	public static void printJSONCrossOntology (File refalignFile, File ontoFile1, File ontoFile2, String onto1ID, String onto2ID) throws OWLOntologyCreationException, IOException, AlignmentException {

		//NOTE: DON´T FORGET TO SET a_char in order to distinguish the two ontology tags in the JSON file!!!

		String ontology1 = null;
		String ontology2 = null;
		String concept1Uri = null;
		String concept2Uri = null;
		String label1Name = null;
		String label2Name = null;

		//import reference alignment file		
		AlignmentParser parser = new AlignmentParser();
		BasicAlignment refalign = (BasicAlignment)parser.parse(refalignFile.toURI().toString());

		File outputFile = new File("./files/version3/json/" + onto1ID + "-" + onto2ID + "-decompounded.json");

		//parse the ontologies to OWL
		OWLOntology onto1 = manager.loadOntologyFromOntologyDocument(ontoFile1);
		OWLOntology onto2 = manager.loadOntologyFromOntologyDocument(ontoFile2);

		//get OWLClasses for ontology 1
		Set<OWLClass> clsOnto1 = onto1.getClassesInSignature();

		//get OWL Classes for ontology 1
		Set<OWLClass> clsOnto2 = onto2.getClassesInSignature();

		//create maps where the class name (string) is key and the associated comment is value
		Map<String, String> onto1CommentMap = new HashMap<String, String>();
		Map<String, String> onto2CommentMap = new HashMap<String, String>();

		for (OWLClass cls1 : clsOnto1) { 
			onto1CommentMap.put(cls1.getIRI().toString(), getComment(onto1, cls1));
		}

		for (OWLClass cls2 : clsOnto2) { 
			onto2CommentMap.put(cls2.getIRI().toString(), getComment(onto2, cls2));
		}

		//create a Map holding classes and associated comments for both ontologies
		Map<String, String> mergedCommentMap = new HashMap<String, String>();
		mergedCommentMap.putAll(onto1CommentMap);
		mergedCommentMap.putAll(onto2CommentMap);

		AlignmentRelation ar = new AlignmentRelation();
		int id = 1;

		PrintWriter pw = new PrintWriter(outputFile);

		JSONObject topLevel = new JSONObject();
		JSONArray array = new JSONArray();

		for (Cell c : refalign) {

			//set the id of the JSON element (auto-enumerate)
			ar.setId(id++);		

			//get the concept uri´s from the cell
			concept1Uri = c.getObject1AsURI().toString();
			concept2Uri = c.getObject2AsURI().toString();

			//we need to distinguish the two ontologies, so do this by finding a unique character in the ontology URI
			char a_char = concept1Uri.charAt(8);	
			//System.out.println("a_char is " + a_char);

			//set the ontology
			if (Character.toString(a_char).equals("d")) {
				ontology1 = onto1ID;
				ontology2 = onto2ID;
			} else {
				ontology1 = onto2ID;
				ontology2 = onto1ID;
			}

			ar.setOntology1(ontology1);
			ar.setOntology2(ontology2);

			//set the relation (e.g. '=') of the correspondence
			ar.setRelation(c.getRelation().getRelation());


			//set the concept uri´s
			ar.setConceptUri1(concept1Uri);
			ar.setConceptUri2(concept2Uri);

			//get the label names by retrieving the entity name from the entity uri
			label1Name = StringUtilities.getString(c.getObject1AsURI().toString());
			label2Name = StringUtilities.getString(c.getObject2AsURI().toString());

			//set the label names
			ar.setLabel1(label1Name);
			ar.setLabel2(label2Name);

			//set the comments (these are retrieved from the merged map representation
			ar.setComment1(mergedCommentMap.get(concept1Uri));
			ar.setComment2(mergedCommentMap.get(concept2Uri));

			//create an individual JSONObject for each entity representation in an Alignment Cell
			JSONObject cell1 = new JSONObject();
			cell1.put("ontology", ar.getOntology1());	
			cell1.put("URI", ar.getConceptUri1());

			//normalises the label and the comment (lowercase, split compounds, adds space between tokens)
			cell1.put("label", StringUtilities.normalizeStringDecompounded(ar.getLabel1()));	
			cell1.put("comment", StringUtilities.normalizeStringDecompounded(ar.getComment1()));


			JSONObject cell2 = new JSONObject();
			cell2.put("comment", StringUtilities.normalizeStringDecompounded(ar.getComment2()));
			cell2.put("label", StringUtilities.normalizeStringDecompounded(ar.getLabel2()));
			cell2.put("URI", ar.getConceptUri2());
			cell2.put("ontology", ar.getOntology2());

			//create a JSONObject for the relation
			JSONObject relation = new JSONObject();
			relation.put("relation", ar.getRelation());

			//nest the already created JSONObjects in a top-level JSONObject
			topLevel = new JSONObject();
			topLevel.put("id", ar.getId());
			topLevel.put("x", cell1);
			topLevel.put("y", cell2);
			topLevel.put("relation", ar.getRelation());

			//create a JSONArray holding each toplevel JSONObject (and the nested ones) in order to have a valid JSON file
			array.add(topLevel);

		}
		Gson gsonPrettyPrint = new GsonBuilder().setPrettyPrinting().create();
		String jsonString = gsonPrettyPrint.toJson(array);

		//need to convert from unicode for the relation
		pw.println(StringEscapeUtils.unescapeJava(jsonString));
		pw.close();

	}

	/**
	 * Returns a set of string tokens from the RDFS comment associated with an OWL class
	 * @param onto The ontology holding the OWL class
	 * @param cls The OWL class
	 * @return A string representing the set of tokens from a comment without stopwords
	 * @throws IOException
	 */
	public static String getComment (OWLOntology onto, OWLClass cls) throws IOException {

		String comment = null;
		String commentWOStopWords = null;

		for(OWLAnnotation a : cls.getAnnotations(onto, factory.getRDFSComment())) {
			OWLAnnotationValue value = a.getValue();
			if(value instanceof OWLLiteral) {
				comment = ((OWLLiteral) value).getLiteral().toString();
				commentWOStopWords = StringUtilities.removeStopWordsFromString(comment);
			}
		}

		return commentWOStopWords;

	}
	

	/**
	 * Produces a complete reference alignment, that is, a reference alignment that contains all permutations of relations from two input ontologies along with all relations from an "official" reference alignment between the two input ontologies.
	 * The "non-official" relations have relation type "!"
	 * @param refalignFile
	 * @param ontoFile1
	 * @param ontoFile2
	 * @param outputAlignmentFileName
	 * @throws OWLOntologyCreationException
	 * @throws AlignmentException
	 * @throws IOException
	 */
	public static File produceCompleteReferenceAlignment (File refalignFile, File ontoFile1, File ontoFile2, String outputAlignmentFileName) throws OWLOntologyCreationException, AlignmentException, IOException {

		AlignmentParser parser = new AlignmentParser();
		BasicAlignment refalign = (BasicAlignment)parser.parse(refalignFile.toURI().toString());
		
		
		//duplicate the refalign to add indirect subsumption relations
		BasicAlignment refalignCopy = (BasicAlignment) refalign.clone();

		//import ontologies
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		//get the ontologies from the alignment file
		OWLOntology onto1 = manager.loadOntologyFromOntologyDocument(ontoFile1);
		OWLOntology onto2 = manager.loadOntologyFromOntologyDocument(ontoFile2);
		BasicAlignment a = new BasicAlignment();		
		URI onto1URI = onto1.getOntologyID().getOntologyIRI().toURI();
		URI onto2URI = onto2.getOntologyID().getOntologyIRI().toURI();

		//get all combinations of relations from the two input ontologies		
		System.err.println("Getting all relations from the two ontologies and puts them in alignment a");
		for (OWLClass c : onto1.getClassesInSignature()) {		
			for (OWLClass d : onto2.getClassesInSignature()) {
				a.addAlignCell(c.getIRI().toURI(), d.getIRI().toURI(), "!", 1.0);
			}
		}	
		System.err.println("Finished the job of getting all relations from the two ontologies and puts them in alignment a");
		
//		System.out.println("Printing alignment a: ");
//		for (Cell c : a) {
//			System.out.println(c.getObject1AsURI() + " - " + c.getObject2AsURI());
//		}

		//TODO: Separate the getSuperClasses into own methods. So first, put all possible relations in an initial alignment, then 
		//get super- and subclasses 
		//get the superclasses for each class in onto1
		Map<String, Set<String>> superOnto1 = statistics.Superclasses.getSuperclasses(onto1);
//		for (Entry<String, Set<String>> e : superOnto1.entrySet()) {
//			System.out.println("class in superOnto1: " + e.getKey());
//		}

		//get the superclasses for each class in onto2
		Map<String, Set<String>> superOnto2 = statistics.Superclasses.getSuperclasses(onto2);

		//get the subclasses for each class in onto1
		Map<String, Set<String>> subOnto1 = statistics.Subclasses.getSubclasses(onto1);

		//get the subclasses for each class in onto2
		Map<String, Set<String>> subOnto2 = statistics.Subclasses.getSubclasses(onto2);
		System.out.println("Finished retrieving super- and subclasses");

		//get the indirect subsumption relations using the reference alignment as input
		//if a > b, then all parent classes of a > b + all children of b - and vice versa
		System.err.println("Getting the indirect subsumption relations");
		for (Cell c : refalign) {
			//System.out.println("c.getRelation() is " + c.getRelation().getRelation() + " and the objects are: " + c.getObject1AsURI() + " and " + c.getObject2AsURI());
			//if c.object1 > c.object2, then c.object1.parents >> c.object2 && c.object1 >> c.object2.children && c.object1.parents >> c.object2.children
			if (c.getRelation().getRelation().equals(">")) {
				
				//get all superclasses of c.object1
				if (superOnto1.containsKey(c.getObject1AsURI().toString())) {
					Set<String> o1_supers = superOnto1.get(c.getObject1AsURI().toString());


					//have all superclasses of c.object1 subsume c.object2
					if (o1_supers!=null) {
						for (String s1 : o1_supers) {
//							refalignCopy.addAlignCell(URI.create(s1), c.getObject2AsURI(), "&#8658;", 1.0);	
							refalignCopy.addAlignCell(URI.create(s1), c.getObject2AsURI(), "*>", 1.0);
						}
					}

					//have all superclasses of c.object1 subsume c.object2.children
					Set<String> o2_subs = subOnto2.get(c.getObject2AsURI().toString());
					if (o1_supers!=null && o2_subs!=null) {						
						for (String s1 : o1_supers) {
							for (String s2 : o2_subs) {
//								refalignCopy.addAlignCell(URI.create(s1), URI.create(s2), "&#8658;", 1.0);
								refalignCopy.addAlignCell(URI.create(s1), URI.create(s2), "*>", 1.0);
							}
						}
					}

					//get all subclasses of c.object2
					if (subOnto2.containsKey(c.getObject2AsURI().toString())) {
						Set<String> subs = subOnto2.get(c.getObject2AsURI().toString());

						//have all subclasses of c.object2 be subsumed by c.object1
						for (String s : subs) {
//							refalignCopy.addAlignCell(c.getObject1AsURI(), URI.create(s), "&#8658;", 1.0);
							refalignCopy.addAlignCell(c.getObject1AsURI(), URI.create(s), "*>", 1.0);
						}
					}
				}

			}
			
			//if c.object1 < c.object2, then c.object1 << c.object2.parents && c.object1.children << c.object2 && c.object1.children << c.object2.parents
			if (c.getRelation().getRelation().equals("<")) {
				
				//get all superclasses of c.object2
				if (superOnto2.containsKey(c.getObject2AsURI().toString())) {
					Set<String> o2_supers = superOnto2.get(c.getObject2AsURI().toString());
					
					//have all superclasses of c.object2 subsume c.object1
					if (o2_supers!=null) {
						for (String s2 : o2_supers) {
							refalignCopy.addAlignCell(c.getObject1AsURI(), URI.create(s2),"LLT", 1.0);	
//							refalignCopy.addAlignCell(c.getObject1AsURI(), URI.create(s2),"<*", 1.0);
						}
					}
					
					//have all superclasses of c.object2 subsume c.object1.children
					Set<String> o1_subs = subOnto1.get(c.getObject1AsURI().toString());
					if (o2_supers != null && o1_subs != null) {
						for (String s2 : o2_supers) {
							for (String s1 : o1_subs) {
								refalignCopy.addAlignCell(URI.create(s1), URI.create(s2), "LLT", 1.0);
//								refalignCopy.addAlignCell(URI.create(s1), URI.create(s2), "<*", 1.0);
							}
						}
					}
					
					//get all subclasses of c.object1
					if (subOnto1.containsKey(c.getObject1AsURI().toString())) {
						Set<String> subs = subOnto1.get(c.getObject1AsURI().toString());
						
						//have all subclasses of c.object1 be subsumed by c.object2
						for (String s : subs) {
							refalignCopy.addAlignCell(URI.create(s), c.getObject2AsURI(), "LLT", 1.0);
//							refalignCopy.addAlignCell(URI.create(s), c.getObject2AsURI(), "<*", 1.0);
						}
					}
				}
				
			}


		}



		//remove those relations that are also in the reference alignment
		System.err.println("Removing relations that are also in reference alignment");
		BasicAlignment alignmentWithoutDuplicates = (BasicAlignment) removeDuplicates(refalignCopy,a);

		//ensure that only objects with proper URIs are included in the alignment
		System.err.println("Ensuring that only objects with proper URIs are included in alignment");
		Alignment alignmentWithProperURIs = new URIAlignment();
		for (Cell c : alignmentWithoutDuplicates) {
			if ((c.getObject1AsURI().getSchemeSpecificPart().toString().equals(onto1URI.getSchemeSpecificPart().toString()) 
					|| c.getObject1AsURI().getSchemeSpecificPart().toString().equals(onto2URI.getSchemeSpecificPart().toString())) 
					&& (c.getObject2AsURI().getSchemeSpecificPart().toString().equals(onto1URI.getSchemeSpecificPart().toString()) 
							|| c.getObject2AsURI().getSchemeSpecificPart().toString().equals(onto2URI.getSchemeSpecificPart().toString()))) {
				if (c.getRelation().getRelation().equals("<")) {
					alignmentWithProperURIs.addAlignCell(c.getObject1(), c.getObject2(), "&lt;", c.getStrength());
				} else {
					alignmentWithProperURIs.addAlignCell(c.getObject1(), c.getObject2(), c.getRelation().getRelation(), c.getStrength());
				}

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

		System.out.println("Output alignment file written to " + outputAlignment.getPath());

		return outputAlignment;
	}

	//Removes relations from the input alignment that are also in the reference alignment
	public static Alignment removeDuplicates(BasicAlignment refAlign, BasicAlignment inputAlign) throws AlignmentException {

		Alignment returnedAlignment = new BasicAlignment();

		Set<Cell> sameCells = new HashSet<Cell>();

		//put the cell (relation) that corresponds to the reference alignment in sameCells
		for (Cell c_ref : refAlign) {
			System.out.println("Trying to get aligned cells for " + c_ref.getObject1() + " and " + c_ref.getObject2());
			Set<Cell> alignedCells = inputAlign.getAlignCells(c_ref.getObject1(), c_ref.getObject2());
			
			if (alignedCells != null) {
			sameCells.addAll(inputAlign.getAlignCells(c_ref.getObject1(), c_ref.getObject2()));
			System.out.println("Have added " + c_ref.getObject1() + " and "+ c_ref.getObject2());
		}
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

	/**
	 * The main method
	 * @param args
	 * @throws AlignmentException
	 * @throws OWLOntologyCreationException
	 * @throws IOException
	 */
	public static void main(String[] args) throws AlignmentException, OWLOntologyCreationException, IOException {

		//NOTE: DON´T FORGET TO SET a_char in printJSONCrossOntology() in order to get the correct order of the two ontology tags in the resulting JSON file!!!

		String onto1ID = "atmonto";
		String onto2ID = "airm";
		
		File ontoFile1 = new File("./files/ATMONTO_AIRM/ontologies/ATMOntoCoreMerged.owl");
		File ontoFile2 = new File("./files/ATMONTO_AIRM/ontologies/airm-mono.owl");
		
		File refalignFile = new File("./files/version3/ReferenceAlignment-ATMONTO-AIRM-EQ-SUB.rdf");
		

/*		OAEI-2011 configuration
 		String onto1ID = "301";
		String onto2ID = "303";
		//import the ontologies in the reference alignment
		File ontoFile1 = new File("./files/wordembedding/allontologies/"+onto1ID+onto2ID+"-"+onto1ID+".rdf");
		File ontoFile2 = new File("./files/wordembedding/allontologies/"+onto1ID+onto2ID+"-"+onto2ID+".rdf");

		//import the reference alignment for these two ontologies
		File refalignFile = new File("./files/expe_oaei_2011/ref_alignments/"+onto1ID+onto2ID+"/"+onto1ID+"-"+onto2ID+".rdf");*/

		//create the complete reference alignment file (and print it to disk)
		String outputAlignmentFileName = "./files/version3/"+onto1ID+onto2ID+"-decompounded.rdf";
		File completeRefAlignFile = produceCompleteReferenceAlignment (refalignFile, ontoFile1, ontoFile2, outputAlignmentFileName);

		//create JSON output from the complete reference alignment file
		System.out.println("Printing reference alignment to JSON");
		printJSONCrossOntology (completeRefAlignFile, ontoFile1, ontoFile2, onto1ID, onto2ID);


	}

}
