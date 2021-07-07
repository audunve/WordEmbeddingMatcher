import java.io.File;

public class FileTest {

	public static void main(String[] args) {
		
		//System.out.println(Str.lastIndexOf( 'o' ));
		
		String onto1 = "./files/expe_oaei_2011/ontologies/302303/302303-303.rdf";
		String onto1Substring = onto1.substring(onto1.lastIndexOf("-")+1, onto1.lastIndexOf("."));
		
		System.out.println(onto1Substring);
		
		String onto2 = "./files/expe_oaei_2011/ontologies/302303/302303-302.rdf";
		String onto2Substring = onto2.substring(onto2.lastIndexOf("-")+1, onto2.lastIndexOf("."));
		System.out.println(onto1Substring);
		
		String vector1 = "./files/expe_oaei_2011/vector-files-single-ontology/vectorOutput-302.txt";
		String vector1Substring = vector1.substring(vector1.lastIndexOf("-")+1, vector1.lastIndexOf("."));
		String vector2 = "./files/expe_oaei_2011/vector-files-single-ontology/vectorOutput-303.txt";
		String vector2Substring = vector2.substring(vector2.lastIndexOf("-")+1, vector2.lastIndexOf("."));
		
		String newVector1 = null;
		String newVector2 = null;

		
		if (onto1Substring.equals(vector1Substring)) {
			newVector1 = vector1;
			newVector2 = vector2;
		} else {
			newVector1 = vector2;
			newVector2 = vector1;
		}
		
		System.out.println("Ontology 1 is: " + onto1 + " vector file is: " + newVector1);
		System.out.println("Ontology 2 is: " + onto2 + " vector file is: " + newVector2);
	}
}
