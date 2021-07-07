package json;
import java.util.ArrayList;

/**
 * Prints relations in an alignment file to JSON format.
 * @author audunvennesland
 * 15. des. 2017 
 */
public class AlignmentRelation {
	
	private static int id;
	private static String ontology1;
	private static String conceptUri1;
	private static String label1;
	private static String comment1;
	private static String ontology2;
	private static String conceptUri2;
	private static String label2;
	private static String comment2;
	private static String relation;
	

	/**
	 * @param ontology1
	 * @param conceptUri1
	 * @param label1
	 * @param comment1
	 * @param ontology2
	 * @param conceptUri2
	 * @param label2
	 * @param comment2
	 * @param relation
	 */
	public AlignmentRelation(int id, String ontology1, String conceptUri1, String label1, String comment1, String ontology2,
			String conceptUri2, String label2, String comment2, String relation) {
		this.id = id;
		this.ontology1 = ontology1;
		this.conceptUri1 = conceptUri1;
		this.label1 = label1;
		this.comment1 = comment1;
		this.ontology2 = ontology2;
		this.conceptUri2 = conceptUri2;
		this.label2 = label2;
		this.comment2 = comment2;
		this.relation = relation;
	}

	public AlignmentRelation(){}
	
	
	/**
	 * @return the alignment relation id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	

	/**
	 * @return the ontology1
	 */
	public String getOntology1() {
		return ontology1;
	}

	/**
	 * @param ontology1 the ontology1 to set
	 */
	public void setOntology1(String ontology1) {
		this.ontology1 = ontology1;
	}

	/**
	 * @return the conceptUri1
	 */
	public String getConceptUri1() {
		return conceptUri1;
	}

	/**
	 * @param conceptUri1 the conceptUri1 to set
	 */
	public void setConceptUri1(String conceptUri1) {
		this.conceptUri1 = conceptUri1;
	}

	/**
	 * @return the label1
	 */
	public String getLabel1() {
		return label1;
	}

	/**
	 * @param label1 the label1 to set
	 */
	public void setLabel1(String label1) {
		this.label1 = label1;
	}

	/**
	 * @return the comment1
	 */
	public String getComment1() {
		return comment1;
	}

	/**
	 * @param comment1 the comment1 to set
	 */
	public void setComment1(String comment1) {
		//remove quotes
		this.comment1 = comment1;
	}

	/**
	 * @return the ontology2
	 */
	public String getOntology2() {
		return ontology2;
	}

	/**
	 * @param ontology2 the ontology2 to set
	 */
	public void setOntology2(String ontology2) {
		this.ontology2 = ontology2;
	}

	/**
	 * @return the conceptUri2
	 */
	public String getConceptUri2() {
		return conceptUri2;
	}

	/**
	 * @param conceptUri2 the conceptUri2 to set
	 */
	public void setConceptUri2(String conceptUri2) {
		this.conceptUri2 = conceptUri2;
	}

	/**
	 * @return the label2
	 */
	public String getLabel2() {
		return label2;
	}

	/**
	 * @param label2 the label2 to set
	 */
	public void setLabel2(String label2) {
		this.label2 = label2;
	}

	/**
	 * @return the comment2
	 */
	public String getComment2() {
		return comment2;
	}

	/**
	 * @param comment2 the comment2 to set
	 */
	public void setComment2(String comment2) {
		this.comment2 = comment2;
	}

	/**
	 * @return the relation
	 */
	public String getRelation() {
		return relation;
	}

	/**
	 * @param relation the relation to set
	 */
	public void setRelation(String relation) {
		this.relation = relation;
	}
	
	
	public static void printAlignmentRelation() {
		System.out.println("\n" + id + "\n"+ ontology1 + "\n"+ conceptUri1 + "\n"+ label1 + "\n"+ comment1 + "\n"+ ontology2 + "\n"+ conceptUri2 + "\n"+ label2 + "\n"+ comment2 + "\n"+ relation);
		
	}
	
	@Override
	public String toString(){
		return "\n" + getId()+ "\n"+getConceptUri1()+"\n"+getLabel1() + "\n" + getComment1() + "\n" + getOntology2() + "\n" + getConceptUri2() + "\n" +
				getLabel2() + "\n"  +getComment2() + "\n" + getRelation();
	}
	
	
}


