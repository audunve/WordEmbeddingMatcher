import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

/**
 * @author audunvennesland
 * 15. des. 2017 
 */
public class TokenizeWithAnalyzer {
	
	private TokenizeWithAnalyzer(){}
	
	public static List tokenizeString(Analyzer analyzer, String str) {

		    List result = new ArrayList<Object>();
	
		    try {

		      TokenStream stream  = analyzer.tokenStream(null, new StringReader(str));

		      stream.reset();

		      while (stream.incrementToken()) {

		        result.add(stream.getAttribute(CharTermAttribute.class).toString());

		      }

		    } catch (IOException e) {
	
		      // not thrown b/c we're using a string reader...

		      throw new RuntimeException(e);
	
		    }
	
		    return result;
	
		  }
	
	public static void main(String[] args) {
		
		String text = "Lucene is a simple yet powerful java based search library.";

		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);

		       List ss=TokenizeWithAnalyzer.tokenizeString(analyzer, text);

		  System.out.print("==>"+ss+" \n");

	}


}
