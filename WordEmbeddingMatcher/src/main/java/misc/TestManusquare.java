package misc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import extraction.VectorExtractor;

public class TestManusquare {
	
	public static void main(String[] args) throws IOException {
		
		//embeddings file
		File embeddingsFile = new File("./files/manusquare/manusquare_wikipedia_trained.txt");
		
		//create a vector map from the embeddings file
		Map<String, ArrayList<Double>> vectorMap = VectorExtractor.createVectorMap(embeddingsFile);
		
		System.out.println("The vectorMap contains " + vectorMap.size() + " entries");
		
		
		
	}

}
