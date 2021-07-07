package matching;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;

import extraction.VectorExtractor;

public class TestWEMatcher {
	
	public static void main(String[] args) throws FileNotFoundException {
		
		
		File wikipedia_embeddings = new File("./files/wikipedia_trained.txt");
		Map<String, ArrayList<Double>> skybraryVectors = VectorExtractor.createVectorMap(wikipedia_embeddings);
		
	}

}