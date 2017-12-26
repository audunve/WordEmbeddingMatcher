package json;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.semanticweb.owl.align.AlignmentException;

import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.parser.AlignmentParser;
import misc.StringUtils;

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
	 * The main method
	 * @param args
	 * @throws AlignmentException
	 * @throws OWLOntologyCreationException
	 * @throws IOException
	 */
	public static void main(String[] args) throws AlignmentException, OWLOntologyCreationException, IOException {

		String ontology1 = null;
		String ontology2 = null;
		String concept1Uri = null;
		String concept2Uri = null;
		String label1Name = null;
		String label2Name = null;

		//import reference alignment file
		File refalignFile = new File("./files/wordembedding/refencealignments/303304/refalign.rdf");
		AlignmentParser parser = new AlignmentParser();
		BasicAlignment refalign = (BasicAlignment)parser.parse(refalignFile.toURI().toString());

		//import the ontologies in the reference alignment
		File ontoFile1 = new File("./files/wordembedding/allontologies/303304-303.rdf");
		File ontoFile2 = new File("./files/wordembedding/allontologies/303304-304.rdf");
		
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
		
		File outPutFile = new File("./files/wordembedding/json/" + StringUtils.stripOntologyName(ontoFile1.getName()) + "-" + StringUtils.stripOntologyName(ontoFile2.getName()) + ".json");

		PrintWriter pw = new PrintWriter(outPutFile);
		
		JSONObject topLevel = new JSONObject();
		JSONArray array = new JSONArray();

		for (Cell c : refalign) {

			//set the id of the JSON element (auto-enumerate)
			ar.setId(id++);		

			//get the concept uri´s from the cell
			concept1Uri = c.getObject1AsURI().toString();
			concept2Uri = c.getObject2AsURI().toString();
			
			//we need to distinguish the two ontologies, so do this by finding a unique character in the ontology URI
			char a_char = concept1Uri.charAt(7);	
			
			//set the ontology
			if (Character.toString(a_char).equals("o")) {
				ontology1 = "304";
				ontology2 = "303";
			} else {
				ontology1 = "303";
				ontology2 = "304";
			}
			
			ar.setOntology1(ontology1);
			ar.setOntology2(ontology2);
			
			//set the relation (e.g. '=') of the correspondence
			ar.setRelation(c.getRelation().getRelation());
			
			
			//set the concept uri´s
			ar.setConceptUri1(concept1Uri);
			ar.setConceptUri2(concept2Uri);
			
			//get the label names by retrieving the entity name from the entity uri
			label1Name = StringUtils.getString(c.getObject1AsURI().toString()).toLowerCase();
			label2Name = StringUtils.getString(c.getObject2AsURI().toString()).toLowerCase();
			
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
			cell1.put("label", ar.getLabel1());	
			cell1.put("comment", ar.getComment1());
				

			JSONObject cell2 = new JSONObject();
			cell2.put("comment", ar.getComment2());
			cell2.put("label", ar.getLabel2());
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
				commentWOStopWords = StringUtils.removeStopWordsFromString(comment);
			}
		}

		return commentWOStopWords;

	}

}
