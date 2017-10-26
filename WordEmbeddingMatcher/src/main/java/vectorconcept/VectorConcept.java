package vectorconcept;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import fr.inrialpes.exmo.ontosim.vector.CosineVM;
import misc.Cosine;
import misc.MathUtils;
import misc.StringUtils;

/**
 * @author audunvennesland
 * 6. okt. 2017 
 */
public class VectorConcept {

	String conceptURI;
	String conceptLabel;
	ArrayList<Double> labelVectors = new ArrayList<Double>();
	ArrayList<Double> commentVectors = new ArrayList<Double>();
	ArrayList<Double> globalVectors = new ArrayList<Double>();

	/**
	 * @param conceptURI the URI of the concept (e.g. http;//oaei.ontologymatching.org/2010/benchmarks/301/onto.rdf#Manual)
	 * @param conceptLabel the label associated with a concept (e.g. Manual)
	 * @param labelVectors a set of vectors associated with the label
	 * @param commentVectors a set of vectors associated with comments defining the concept
	 * @param globalVectors vectors as average of the label vectors and comment vectors
	 */
	public VectorConcept(String conceptURI, String conceptLabel, ArrayList<Double> labelVectors, ArrayList<Double> commentVectors,
			ArrayList<Double> globalVectors) {
		super();
		this.conceptURI = conceptURI;
		this.conceptLabel = conceptLabel;
		this.labelVectors = labelVectors;
		this.commentVectors = commentVectors;
		this.globalVectors = globalVectors;
	}
	

	public VectorConcept() {}

	private String getConceptURI() {
		return conceptURI;
	}

	private String getConceptLabel() {
		return conceptLabel;
	}

	/**
	 * @return the labelVectors
	 */
	private ArrayList<Double> getLabelVectors() {
		return labelVectors;
	}

	/**
	 * @param labelVectors the labelVectors to set
	 */
	private void setLabelVectors(ArrayList<Double> labelVectors) {
		this.labelVectors = labelVectors;
	}

	/**
	 * @return the commentVectors
	 */
	private ArrayList<Double> getCommentVectors() {
		return commentVectors;
	}

	/**
	 * @param commentVectors the commentVectors to set
	 */
	private void setCommentVectors(ArrayList<Double> commentVectors) {
		this.commentVectors = commentVectors;
	}

	/**
	 * @return the globalVectors
	 */
	private ArrayList<Double> getGlobalVectors() {
		return globalVectors;
	}

	/**
	 * @param globalVectors the globalVectors to set
	 */
	private void setGlobalVectors(ArrayList<Double> globalVectors) {
		this.globalVectors = globalVectors;
	}

	/**
	 * @param conceptName the conceptName to set
	 */
	private void setConceptURI(String conceptURI) {
		this.conceptURI = conceptURI;
	}

	/**
	 * @param conceptName the conceptName to set
	 */
	private void setConceptLabel(String conceptLabel) {
		this.conceptLabel = conceptLabel;
	}


/**
 * Iterates through each line in a vector file, checks which value is covered by that line, and adds the appropriate variable to the VectorConcept object. 
 * Finally, a set of VectorConcepts is used for holding VectorConcepts from a given file
 * @param vectorFile a vector file consists of a concept and its description (URI, label, comment tokens) and associated vectors
 * @return a set of VectorConcepts
 * @throws FileNotFoundException
 */
	public static Set<VectorConcept> populate(File vectorFile) throws FileNotFoundException {

		Set<VectorConcept> vcSet = new HashSet<VectorConcept>();

		String conceptURI = null;
		String conceptLabel = null;
		ArrayList<Double> labelVectors = null;
		ArrayList<Double> commentVectors = null;
		ArrayList<Double> globalVectors = null;

		VectorConcept vc = new VectorConcept();
		Scanner sc = new Scanner(vectorFile);

		//iterates through each line in a vector file, checks which value is covered by that line, and adds the appropriate variable to the VectorConcept object. Finally, a set of VectorConcepts is used for holding VectorConcepts 
		//from a given file
		while (sc.hasNextLine()) {

			String line = sc.nextLine();

			if (!line.isEmpty()) {

				String[] strings = line.split(";");

				if (strings[0].equals("conceptUri")) {
					conceptURI = strings[1];
					vc.setConceptURI(conceptURI);


				} if (strings[0].equals("label")) {
					conceptLabel = strings[1];
					vc.setConceptLabel(conceptLabel);


				} if (strings[0].equals("label vector")) {
					labelVectors = new ArrayList<Double>();
					String lv = strings[1];
					String[] vectors = lv.split(" ");
					for (int i = 0; i < vectors.length; i++) {
						if (!vectors[i].isEmpty()) {
							labelVectors.add(Double.valueOf(vectors[i]));
						}
					}
					vc.setLabelVectors(labelVectors);


				} if (strings[0].equals("comment vector")) {
					commentVectors = new ArrayList<Double>();
					
					
					if (strings[1].equals("no vectors for these comment tokens")) {
					String lv = strings[1];
					String[] vectors = lv.split(" ");
					for (int i = 0; i < vectors.length; i++) {
						if (!vectors[i].isEmpty()) {
							commentVectors.add(Double.valueOf(vectors[i]));
						}
					}

					vc.setCommentVectors(commentVectors);
					} else {
						vc.setCommentVectors(null);
					}


				} if (strings[0].equals("global vector")) {
					globalVectors = new ArrayList<Double>();
					String lv = strings[1];
					String[] vectors = lv.split(" ");
					for (int i = 0; i < vectors.length; i++) {
						if (!vectors[i].isEmpty()) {
							globalVectors.add(Double.valueOf(vectors[i]));
						}
					}

					vc.setGlobalVectors(globalVectors);


					vcSet.add(vc);

					//instantiate a new VectorConcept object to hold next iterations
					vc = new VectorConcept();

				}
			}
		}

		sc.close();

		return vcSet;
	}
	
	/**
	 * Sorts a Map on values where key is a pair of concepts being matched and value is cosine sim
	 * @param cosineMap
	 * @return
	 */
	private static Map<String, Double> sortByValue(Map<String, Double> cosineMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<String, Double>> list =
                new LinkedList<Map.Entry<String, Double>>(cosineMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });
        
        Collections.reverse(list);

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
	
	public static void printLabelAndGlobalSim(File vectorFileDir, double threshold) throws FileNotFoundException {
		
		//using the cosine measure from OntoSim
		CosineVM cosine = new CosineVM();
		File[] filesInDir = vectorFileDir.listFiles();
		
		for (int i = 0; i < filesInDir.length; i++) {
			for (int j = i+1; j < filesInDir.length; j++) {
				if (filesInDir[i].isFile() && filesInDir[j].isFile() && i != j) {
					System.out.println("\n*****Computing cosine for " + StringUtils.stripPath(filesInDir[i].toString()) + " and " + StringUtils.stripPath(filesInDir[j].toString()) + " *****");
					
					Set<VectorConcept> vc1 = populate(filesInDir[i]);
					Set<VectorConcept> vc2 = populate(filesInDir[j]);
					
					System.out.println("Number of vector concepts for " + StringUtils.stripPath(filesInDir[i].toString()) + " " + vc1.size());
					System.out.println("Number of vector concepts for " + StringUtils.stripPath(filesInDir[j].toString()) + " " + vc2.size());
					
					Map<String, Double> rankedLabelMap = new HashMap<String, Double>();
					Map<String, Double> rankedGlobalMap = new HashMap<String, Double>();
					
					//label vectors sim
					System.out.println("\n---Label Vectors Similarity > " + threshold + " ---");
					for (VectorConcept v1 : vc1) {
						for (VectorConcept v2 : vc2) {
							double[] a1 = ArrayUtils.toPrimitive(v1.getLabelVectors().toArray((new Double[v1.getLabelVectors().size()])));
							double[] a4 = ArrayUtils.toPrimitive(v2.getLabelVectors().toArray((new Double[v2.getLabelVectors().size()])));
							double c = cosine.getSim(a1, a4);
							if (c > threshold) {
								String pair = v1.getConceptLabel() + " -" + v2.getConceptLabel();
								rankedLabelMap.put(pair, MathUtils.round(c, 6));
							}
						}
					}
					
					Map<String, Double> sortedLabelMap = sortByValue(rankedLabelMap);
					for (Entry<String, Double> e : sortedLabelMap.entrySet()) {
						System.out.println(e.getKey() + ": " + e.getValue());
					}
					
					//global vectors sim
					System.out.println("\n---Global Vectors Similarity > " + threshold + " ---");
					for (VectorConcept v1 : vc1) {
						for (VectorConcept v2 : vc2) {
							double[] a1 = ArrayUtils.toPrimitive(v1.getGlobalVectors().toArray((new Double[v1.getGlobalVectors().size()])));
							double[] a4 = ArrayUtils.toPrimitive(v2.getGlobalVectors().toArray((new Double[v2.getGlobalVectors().size()])));
							double c = cosine.getSim(a1, a4);
							if (c > threshold) {
								String pair = v1.getConceptLabel() + " -" + v2.getConceptLabel();
								rankedGlobalMap.put(pair, MathUtils.round(c, 6));
							}
						}
					}
					
					Map<String, Double> sortedGlobalMap = sortByValue(rankedGlobalMap);
					for (Entry<String, Double> e : sortedGlobalMap.entrySet()) {
						System.out.println(e.getKey() + ": " + e.getValue());
					}
					
				}
			}
		}
		
	}

	
public static void printGlobalSim(File vectorFileDir, double threshold) throws FileNotFoundException {
		
		//using the cosine measure from OntoSim
		CosineVM cosine = new CosineVM();
		File[] filesInDir = vectorFileDir.listFiles();
		
		for (int i = 0; i < filesInDir.length; i++) {
			for (int j = i+1; j < filesInDir.length; j++) {
				if (filesInDir[i].isFile() && filesInDir[j].isFile() && i != j) {
					System.out.println("\n*****Computing cosine for " + StringUtils.stripPath(filesInDir[i].toString()) + " and " + StringUtils.stripPath(filesInDir[j].toString()) + " *****");
					
					Set<VectorConcept> vc1 = populate(filesInDir[i]);
					Set<VectorConcept> vc2 = populate(filesInDir[j]);
					
					System.out.println("Number of vector concepts for " + StringUtils.stripPath(filesInDir[i].toString()) + " " + vc1.size());
					System.out.println("Number of vector concepts for " + StringUtils.stripPath(filesInDir[j].toString()) + " " + vc2.size());
					
					Map<String, Double> rankedMap = new HashMap<String, Double>();
					
					System.out.println("\n---Global Vectors Similarity > " + threshold + " ---");
					for (VectorConcept v1 : vc1) {
						for (VectorConcept v2 : vc2) {
							double[] a1 = ArrayUtils.toPrimitive(v1.getGlobalVectors().toArray((new Double[v1.getGlobalVectors().size()])));
							double[] a4 = ArrayUtils.toPrimitive(v2.getGlobalVectors().toArray((new Double[v2.getGlobalVectors().size()])));
							double c = cosine.getSim(a1, a4);
							if (c > threshold) {

								String pair = v1.getConceptLabel() + " -" + v2.getConceptLabel();
								rankedMap.put(pair, MathUtils.round(c, 6));
							}

						}
					}
					
					Map<String, Double> sortedMap = sortByValue(rankedMap);
					for (Entry<String, Double> e : sortedMap.entrySet()) {
						System.out.println(e.getKey() + ": " + e.getValue());
					}
					
				}
			}
		}
		
	}
	

	//testing by comparing vectors from VectorConcepts using cosine
	public static void main(String[] args) throws FileNotFoundException {
		
		final File vectorFileDir = new File("./files/wordembedding/vector-files-single-ontology");
		double threshold = 0.6;
		
		printLabelAndGlobalSim(vectorFileDir, threshold);
		
		/*//using the cosine measure from OntoSim
		CosineVM cosine = new CosineVM();
		
		File[] filesInDir = vectorFileDir.listFiles();
		
		for (int i = 0; i < filesInDir.length; i++) {
			for (int j = i+1; j < filesInDir.length; j++) {
				if (filesInDir[i].isFile() && filesInDir[j].isFile() && i != j) {
					System.out.println("\n*****Computing cosine for " + StringUtils.stripPath(filesInDir[i].toString()) + " and " + StringUtils.stripPath(filesInDir[j].toString()) + " *****");
					
					Set<VectorConcept> vc1 = populate(filesInDir[i]);
					Set<VectorConcept> vc2 = populate(filesInDir[j]);
					
					System.out.println("Number of vector concepts for " + StringUtils.stripPath(filesInDir[i].toString()) + " " + vc1.size());
					System.out.println("Number of vector concepts for " + StringUtils.stripPath(filesInDir[j].toString()) + " " + vc2.size());
					
					Map<String, Double> rankedMap = new HashMap<String, Double>();
					
					System.out.println("\n---Label Vectors Similarity > " + threshold + " ---");
					for (VectorConcept v1 : vc1) {
						for (VectorConcept v2 : vc2) {
							double[] a1 = ArrayUtils.toPrimitive(v1.getLabelVectors().toArray((new Double[v1.getLabelVectors().size()])));
							double[] a4 = ArrayUtils.toPrimitive(v2.getLabelVectors().toArray((new Double[v2.getLabelVectors().size()])));
							double c = cosine.getSim(a1, a4);
							if (c > threshold) {

								String pair = v1.getConceptLabel() + " -" + v2.getConceptLabel();
								rankedMap.put(pair, MathUtils.round(c, 6));
							}

						}
					}
					
					Map<String, Double> sortedMap = sortByValue(rankedMap);
					for (Entry e : sortedMap.entrySet()) {
						System.out.println(e.getKey() + ": " + e.getValue());
					}
					
				}
			}
		}*/
		
		/*

		File onto301302_301 = new File("./files/wordembedding/vector-files/vectorOutput301302-301.txt");
		File onto301302_302 = new File("./files/wordembedding/vector-files/vectorOutput301302-302.txt");
		
		File onto301303_301 = new File("./files/wordembedding/vector-files/vectorOutput301303-301.txt");
		File onto301303_302 = new File("./files/wordembedding/vector-files/vectorOutput301303-303.txt");
		
		File onto301304_301 = new File("./files/wordembedding/vector-files/vectorOutput301304-301.txt");
		File onto301304_304 = new File("./files/wordembedding/vector-files/vectorOutput301304-304.txt");
		
		File onto302303_302 = new File("./files/wordembedding/vector-files/vectorOutput302303-302.txt");
		File onto302303_303 = new File("./files/wordembedding/vector-files/vectorOutput302303-303.txt");
		
		File onto302304_302 = new File("./files/wordembedding/vector-files/vectorOutput302304_302.txt");
		File onto302304_304 = new File("./files/wordembedding/vector-files/vectorOutput302304_304.txt");
		
		File onto303304_303 = new File("./files/wordembedding/vector-files/vectorOutput303304-303.txt");
		File onto303304_304 = new File("./files/wordembedding/vector-files/vectorOutput303304-304.txt");

		Set<VectorConcept> vc301302_301 = populate(onto301302_301);
		Set<VectorConcept> vc301302_302 = populate(onto301302_302);

		System.out.println("Number of vector concepts for 301: " + vc301302_301.size());
		System.out.println("Number of vector concepts for 304: " + vc301302_302.size());

		//using the cosine measure from OntoSim
		CosineVM cosine = new CosineVM();

		
		System.out.println("\n---Label Vectors Similarity using OntoSim Cosine---");
		for (VectorConcept vc1 : vc301302_301) {
			for (VectorConcept vc4 : vc301302_302) {
				double[] a1 = ArrayUtils.toPrimitive(vc1.getLabelVectors().toArray((new Double[vc1.getLabelVectors().size()])));
				double[] a4 = ArrayUtils.toPrimitive(vc4.getLabelVectors().toArray((new Double[vc4.getLabelVectors().size()])));
				double c = cosine.getSim(a1, a4);
				if (c > 0.6) {
					System.out.println(vc1.getConceptLabel() + " and " + vc4.getConceptLabel() + ": " + c);
				}

			}
		}

		System.out.println("\n---Global Vectors Similarity using OntoSIM Cosine---");
		for (VectorConcept vc1 : vc301302_301) {
			for (VectorConcept vc4 : vc301302_302) {
				double[] a1 = ArrayUtils.toPrimitive(vc1.getGlobalVectors().toArray((new Double[vc1.getGlobalVectors().size()])));
				double[] a4 = ArrayUtils.toPrimitive(vc4.getGlobalVectors().toArray((new Double[vc4.getGlobalVectors().size()])));
				double cos = cosine.getSim(a1,  a4);
				if (cos > 0.6) {
					System.out.println(vc1.getConceptLabel() + " and " + vc4.getConceptLabel() + ": " + Cosine.cosineSimilarity(a1, a4));
				}

			}
		}*/

	}
}
