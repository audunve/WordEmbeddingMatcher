package evaluation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;

import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.eval.PRecEvaluator;
import fr.inrialpes.exmo.align.parser.AlignmentParser;
import misc.StringUtilities;

public class TestEval {
	
	public static void main(String[] args) throws AlignmentException, URISyntaxException {
		
		AlignmentParser testAlignPparser = new AlignmentParser(0);
		AlignmentParser refAlignPparser = new AlignmentParser(1);
		
		Alignment referenceAlignment = refAlignPparser.parse(new URI(StringUtilities.convertToFileURL("./files/expe_oaei_2011/evaluation/referencealignments_all/301-302.rdf")));
		
		Alignment testAlignment = testAlignPparser.parse(new URI(StringUtilities.convertToFileURL("./files/expe_oaei_2011/evaluation/combinedalignments/301-302/301-302-MergedAlignmentSubsAndEqual-WEGlobal-06.rdf")));
		
		double fMeasure = 0;
		double precision = 0;
		double recall = 0;
		
		PRecEvaluator eval = null;
		Properties p = new Properties();
		
		eval = new PRecEvaluator(referenceAlignment, testAlignment);
		eval.eval(p);

		fMeasure = Double.valueOf(eval.getResults().getProperty("fmeasure").toString());
		precision = Double.valueOf(eval.getResults().getProperty("precision").toString());
		recall = Double.valueOf(eval.getResults().getProperty("recall").toString());
		
		System.out.println("Precision: " + precision);
		System.out.println("Recall: " + recall);
		System.out.println("F-measure: " + fMeasure);
		
	}

}
