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
 * It creates the JSON file using two instances of the same ontology and a reference alignment mapping an ontology to itself for the purpose of training a machine learning method.
 */
public class ProduceJSONSingleOntology {

	//need two OWLOntologyManagers since we cannot parse two OWL ontologies using the same ontology manager (throws an exception)
	static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	static OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
	/**
	 * The OWLReasonerFactory represents a reasoner creation point.
	 */
	static OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();

	static OWLDataFactory factory = manager.getOWLDataFactory();

	public static void main(String[] args) throws AlignmentException, OWLOntologyCreationException, IOException {

		String ontology1 = null;
		String ontology2 = null;
		String concept1Uri = null;
		String concept2Uri = null;
		String label1Name = null;
		String label2Name = null;

		//import reference alignment file
		File refalignFile = new File("./files/wordembedding/refencealignments/303303/303303-refalign.rdf");
		AlignmentParser parser = new AlignmentParser();
		BasicAlignment refalign = (BasicAlignment)parser.parse(refalignFile.toURI().toString());

		//import the ontologies in the reference alignment
		//set the same ontology file for both File instances
		File ontoFile1 = new File("./files/wordembedding/allontologies/301303-303.rdf");
		File ontoFile2 = new File("./files/wordembedding/allontologies/301303-303.rdf");

		//parse the ontologies to OWL
		OWLOntology onto1 = manager.loadOntologyFromOntologyDocument(ontoFile1);
		OWLOntology onto2 = manager2.loadOntologyFromOntologyDocument(ontoFile2);

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

		//instantiate an AlignmentRelation object holding the fields of an Alignment Cell (but that in addition can hold comments (RDFS:Comment) for each entity 
		//and the embedding vectors if needed)
		AlignmentRelation ar = new AlignmentRelation();
		int id = 1;
		
		//the output JSON file
		File outPutFile = new File("./files/wordembedding/json/" + StringUtils.stripOntologyName(ontoFile1.getName()) + "-" + StringUtils.stripOntologyName(ontoFile2.getName()) + ".json");

		PrintWriter pw = new PrintWriter(outPutFile);
		
		JSONObject topLevel = new JSONObject();
		JSONArray array = new JSONArray();

		for (Cell c : refalign) {

			ar.setId(id++);

			//since we are only using a single ontology in this case, just set the ontology name for both
			//ontology 1 and ontology 2
			ontology1 = "303";
			ontology2 = "303";
			
			concept1Uri = c.getObject1AsURI().toString();
			concept2Uri = c.getObject2AsURI().toString();
			
			ar.setRelation(c.getRelation().getRelation());
			label1Name = StringUtils.getString(c.getObject1AsURI().toString()).toLowerCase();
			label2Name = StringUtils.getString(c.getObject2AsURI().toString()).toLowerCase();

			ar.setComment1(mergedCommentMap.get(concept1Uri));
			ar.setComment2(mergedCommentMap.get(concept2Uri));


			
			ar.setOntology1(ontology1);
			ar.setConceptUri1(concept1Uri);
			ar.setLabel1(label1Name);
			ar.setOntology2(ontology2);
			ar.setConceptUri2(concept2Uri);
			ar.setLabel2(label2Name);

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


			JSONObject relation = new JSONObject();
			relation.put("relation", ar.getRelation());

			//nested JSON objects
			topLevel = new JSONObject();
			topLevel.put("id", ar.getId());
			topLevel.put("x", cell1);
			topLevel.put("y", cell2);
			topLevel.put("relation", ar.getRelation());
			
			//create a JSONArray holding each toplevel JSONObject (and the nested ones) in order to have a valid JSON file
			//array = new JSONArray();
			array.add(topLevel);
			

			
			

		}
		//using the Gson library for pretty printing the JSON output
		Gson gsonPrettyPrint = new GsonBuilder().setPrettyPrinting().create();
		String jsonString = gsonPrettyPrint.toJson(array);

		
		//need to convert from unicode for the relation using Apache Commons StringEscapeUtils.unescapeJava
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
	private static String getComment (OWLOntology onto, OWLClass cls) throws IOException {

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
