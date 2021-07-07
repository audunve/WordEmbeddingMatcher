package evaluation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;

import misc.StringUtilities;
import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.eval.PRecEvaluator;
import fr.inrialpes.exmo.align.parser.AlignmentParser;

/**
 * @author audunvennesland
 * 9. apr. 2017 
 */
public class Evaluator {

	/**
	 * Evaluates a single alignment from file against a reference alignment (also from file) and prints precision, recall, f-measure, true positives (TP), false positives (FP) and false negatives (FN)
	 * @param inputAlignmentFileName
	 * @param referenceAlignmentFileName
	 * @throws AlignmentException
	 * @throws URISyntaxException
	 */
	public static void evaluateSingleAlignmentFile (String inputAlignmentFileName, String referenceAlignmentFileName) throws AlignmentException, URISyntaxException {

		AlignmentParser refAlignParser = new AlignmentParser(0);
		AlignmentParser evalAlignParser = new AlignmentParser(1);

		Alignment referenceAlignment = refAlignParser.parse(new URI(StringUtilities.convertToFileURL(referenceAlignmentFileName)));
		BasicAlignment inputAlignment = (BasicAlignment) evalAlignParser.parse(new URI(StringUtilities.convertToFileURL(inputAlignmentFileName)));

		Properties p = new Properties();
		PRecEvaluator eval = new PRecEvaluator(referenceAlignment, inputAlignment);

		eval.eval(p);

		System.err.println("------------------------------");
		System.err.println("Evaluator scores for " + inputAlignmentFileName);
		System.err.println("------------------------------");
		System.err.println("F-measure: " + eval.getResults().getProperty("fmeasure").toString());
		System.err.println("Precision: " + eval.getResults().getProperty("precision").toString());
		System.err.println("Recall: " + eval.getResults().getProperty("recall").toString());

		System.err.println("True positives (TP): " + eval.getResults().getProperty("true positive").toString());

		int fp = eval.getFound() - eval.getCorrect();
		System.err.println("False positives (FP): " + fp);
		int fn = eval.getExpected() - eval.getCorrect();
		System.err.println("False negatives (FN): " + fn);
		System.err.println("\n");

	}

	/**
	 * Evaluates a single alignment against a reference alignment and prints precision, recall, f-measure, true positives (TP), false positives (FP) and false negatives (FN)
	 * @param inputAlignmentFileName
	 * @param referenceAlignmentFileName
	 * @throws AlignmentException
	 * @throws URISyntaxException
	 */
	public static void evaluateSingleAlignment (String inputAlignmentFileName, String referenceAlignmentFileName) throws AlignmentException, URISyntaxException {

		AlignmentParser aparser = new AlignmentParser(0);
		AlignmentParser refParser = new AlignmentParser(1);
		Alignment evaluatedAlignment = aparser.parse(new URI(StringUtilities.convertToFileURL(inputAlignmentFileName)));
		Alignment referenceAlignment = refParser.parse(new URI(StringUtilities.convertToFileURL(referenceAlignmentFileName)));
		
		Properties p = new Properties();
		PRecEvaluator eval = new PRecEvaluator(referenceAlignment, evaluatedAlignment);

		eval.eval(p);

		System.err.println("------------------------------");
		System.err.println("Evaluator scores for " + evaluatedAlignment);
		System.err.println("------------------------------");
		System.err.println("F-measure: " + eval.getResults().getProperty("fmeasure").toString());
		System.err.println("Precision: " + eval.getResults().getProperty("precision").toString());
		System.err.println("Recall: " + eval.getResults().getProperty("recall").toString());

		System.err.println("True positives (TP): " + eval.getResults().getProperty("true positive").toString());

		int fp = eval.getFound() - eval.getCorrect();
		System.err.println("False positives (FP): " + fp);
		int fn = eval.getExpected() - eval.getCorrect();
		System.err.println("False negatives (FN): " + fn);
		System.err.println("\n");

	}

	/**
	 * Evaluates all alignments in a folder against a reference alignment prints for each alignment: precision, recall, f-measure, true positives (TP), false positives (FP) and false negatives (FN)
	 * @param folderName The folder holding all alignments
	 * @param referenceAlignmentFileName
	 * @throws AlignmentException
	 * @throws URISyntaxException
	 */
	public static void evaluateAlignmentFolder (String folderName, String referenceAlignmentFileName) throws AlignmentException, URISyntaxException {

		AlignmentParser aparser = new AlignmentParser(0);
		Alignment referenceAlignment = aparser.parse(new URI(StringUtilities.convertToFileURL(referenceAlignmentFileName)));

		Alignment inversedAlignment = null;

		Properties p = new Properties();

		File folder = new File(folderName);
		File[] filesInDir = folder.listFiles();
		Alignment evaluatedAlignment = null;
		PRecEvaluator eval = null;

		for (int i = 0; i < filesInDir.length; i++) {

			String URI = StringUtilities.convertToFileURL(folderName) + "/" + StringUtilities.stripPath(filesInDir[i].toString());
			System.out.println("Evaluating file " + URI);
			evaluatedAlignment = aparser.parse(new URI(URI));

			//need to make sure that the ontologies are in the same order in the reference alignment and the alignment to be evaluated
			String onto1EvalAlign = evaluatedAlignment.getFile1().toString().substring(evaluatedAlignment.getFile1().toString().lastIndexOf("-"), evaluatedAlignment.getFile1().toString().lastIndexOf("."));
			String onto1RefAlign = referenceAlignment.getFile1().toString().substring(referenceAlignment.getFile1().toString().lastIndexOf("/"), referenceAlignment.getFile1().toString().lastIndexOf("."));

			System.out.println("Does the order in the reference alignment match the order in the alignment to be evaluated?: " + onto1EvalAlign.equals(onto1RefAlign));

			if (!onto1EvalAlign.equals(onto1RefAlign)) {
				inversedAlignment = evaluatedAlignment.inverse();

				eval = new PRecEvaluator(referenceAlignment, inversedAlignment);

				eval.eval(p);

				System.out.println("------------------------------");
				System.out.println("Evaluator scores for " + StringUtilities.stripPath(filesInDir[i].toString()));
				System.out.println("------------------------------");
				System.out.println("F-measure: " + eval.getResults().getProperty("fmeasure").toString());
				System.out.println("Precision: " + eval.getResults().getProperty("precision").toString());
				System.out.println("Recall: " + eval.getResults().getProperty("recall").toString());

				System.out.println("True positives (TP): " + eval.getResults().getProperty("true positive").toString());

				int fp = eval.getFound() - eval.getCorrect();
				System.out.println("False positives (FP): " + fp);
				int fn = eval.getExpected() - eval.getCorrect();
				System.out.println("False negatives (FN): " + fn);
				System.out.println("\n");
			} else {



				eval = new PRecEvaluator(referenceAlignment, evaluatedAlignment);

				eval.eval(p);

				System.out.println("------------------------------");
				System.out.println("Evaluator scores for " + StringUtilities.stripPath(filesInDir[i].toString()));
				System.out.println("------------------------------");
				System.out.println("F-measure: " + eval.getResults().getProperty("fmeasure").toString());
				System.out.println("Precision: " + eval.getResults().getProperty("precision").toString());
				System.out.println("Recall: " + eval.getResults().getProperty("recall").toString());

				System.out.println("True positives (TP): " + eval.getResults().getProperty("true positive").toString());

				int fp = eval.getFound() - eval.getCorrect();
				System.out.println("False positives (FP): " + fp);
				int fn = eval.getExpected() - eval.getCorrect();
				System.out.println("False negatives (FN): " + fn);
				System.out.println("\n");
			}
		}

	}

	/**
	 * Produces a Map of key: matcher (i.e. alignment produced by a particular matcher) and value: F-measure score from evaluation of against the alignment for that particular matcher
	 * @param folderName The folder holding the alignments to be evaluated
	 * @param referenceAlignmentFileName
	 * @return A Map<String, Double) holding the matcher (alignment) and F-measure score for that particular matcher (alignment)
	 * @throws AlignmentException
	 * @throws URISyntaxException
	 */
	public static Map<String, Double> evaluateAlignmentFolderMap (String folderName, String referenceAlignmentFileName) throws AlignmentException, URISyntaxException {

		Map<String, Double> evalFolderMap = new HashMap<String, Double>();

		//select if the evaluation result is fmeasure, precision or recall
		double fMeasure = 0;
		double precision = 0;
		double recall = 0;
		

		AlignmentParser aparser = new AlignmentParser(0);
		Alignment referenceAlignment = aparser.parse(new URI(StringUtilities.convertToFileURL(referenceAlignmentFileName)));

		Alignment inversedAlignment = null;

		Properties p = new Properties();

		File folder = new File(folderName);
		File[] filesInDir = folder.listFiles();
		Alignment evaluatedAlignment = null;
		PRecEvaluator eval = null;

		for (int i = 0; i < filesInDir.length; i++) {

			String URI = StringUtilities.convertToFileURL(folderName) + "/" + StringUtilities.stripPath(filesInDir[i].toString());

			evaluatedAlignment = aparser.parse(new URI(URI));
			
			//need to make sure that the ontologies are in the same order in the reference alignment and the alignment to be evaluated
			String onto1EvalAlign = evaluatedAlignment.getOntology1URI().toString();
			String onto1RefAlign = referenceAlignment.getOntology1URI().toString();
			
			System.out.println("Original: " + evaluatedAlignment.getFile1().toString() + " versus " + referenceAlignment.getFile1().toString());
			System.out.println("Substringed: " + onto1EvalAlign + " versus " + onto1RefAlign);

			//if the ontologies are not in the same order in the reference alignment and the alignment to be evaluated, we inverse the original alignment
			if (!onto1EvalAlign.equals(onto1RefAlign)) {

				inversedAlignment = evaluatedAlignment.inverse();
				
				eval = new PRecEvaluator(referenceAlignment, inversedAlignment);
				eval.eval(p);

				fMeasure = Double.valueOf(eval.getResults().getProperty("fmeasure").toString());
				precision = Double.valueOf(eval.getResults().getProperty("precision").toString());
				recall = Double.valueOf(eval.getResults().getProperty("recall").toString());

			} else {

				eval = new PRecEvaluator(referenceAlignment, evaluatedAlignment);
				eval.eval(p);

				fMeasure = Double.valueOf(eval.getResults().getProperty("fmeasure").toString());
				precision = Double.valueOf(eval.getResults().getProperty("precision").toString());
				recall = Double.valueOf(eval.getResults().getProperty("recall").toString());

				
			}
			evalFolderMap.put(URI, fMeasure);
			System.err.println("Precision: " + precision);
			System.err.println("Recall: " + recall);
			System.err.println("F-measure: " + fMeasure);
		}

		
		return evalFolderMap;

	}

	/**
	 * Runs a complete evaluation producing F-measure scores for individual matchers and combination strategies. The F-measure scores are printed to console.
	 * TO-DO: Implement an "Excel printer" that prints the F-measure scores to an Excel sheet for easy chart-making, e.g. using Apache POI.
	 * @throws AlignmentException
	 * @throws URISyntaxException
	 * @throws FileNotFoundException 
	 */
	public static void runCompleteEvaluation () throws AlignmentException, URISyntaxException, FileNotFoundException {

		File allIndividualAlignments = new File("./files/expe_wn_domain/evaluation/alignments");
		File refAlignFolder = new File("./files/expe_wn_domain/evaluation/referencealignments");

		File[] folders = allIndividualAlignments.listFiles();

		String refAlign = null;

		XSSFWorkbook workbook = new XSSFWorkbook();		
		XSSFSheet spreadsheet = null;


		//***** FOR EVALUATING INDIVIDUAL MATCHERS (ALIGNMENTS) ***
		System.out.println("\n*******************Individual Matchers*******************");
		for (int i = 0; i < folders.length; i++) {
			refAlign = refAlignFolder + "/" + folders[i].getName() + ".rdf";


			//get a map<matcherName, fMeasureValue>
			Map<String, Double> evalMap = evaluateAlignmentFolderMap(folders[i].getPath(), refAlign);
			
			System.out.println("The evalMap contains " + evalMap.size() + " entries");
			
			System.out.println("Dataset: " + folders[i].getName());
			System.out.println("Comparing with reference alignment: " + refAlign);

			spreadsheet = workbook.createSheet(folders[i].getName());
			

			Cell cell = null;

			//Create a new font and alter it.
			XSSFFont font = workbook.createFont();
			font.setFontHeightInPoints((short) 30);
			font.setItalic(true);
			font.setBold(true);

			//Set font into style
			CellStyle style = workbook.createCellStyle();
			style.setFont(font);
			
			int rowNum = 0;
			
			
			for (Entry<String, Double> e : evalMap.entrySet()) {

				int cellnum = 0;
				
				Row row = spreadsheet.createRow(rowNum++);
				cell = row.createCell(cellnum++);
				cell.setCellValue(e.getKey());
				cell = row.createCell(cellnum++);
				cell.setCellValue(e.getValue());
				cell = row.createCell(cellnum++);

			}

			try {
				FileOutputStream outputStream = 
						new FileOutputStream(new File("./files/expe_wn_domain/evaluation/AllMatchersFmeasure.xlsx"));
				workbook.write(outputStream);
				outputStream.close();
				System.out.println("Excel written successfully..");

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/*
	 * Procedure for generating Excel files for evaluation of dataset
	 * In the runCompleteEvaluation() method define the folder having the alignments (these should be in separate folders for each dataset)
	 * ->		e.g. File allIndividualAlignments = new File("./files/expe_oaei_2011/evaluation/isub_alignments");
	 * In the runCompleteEvaluation() method define the folder having the reference alignments (these should be in separate folders for each dataset, similar as the alignments)
	 * ->		e.g. File refAlignFolder = new File("./files/expe_oaei_2011/evaluation/referencealignments_equivalence");
	 * Define evaluation method (precision, recall or fMeasure) on line 252 ( e.g. evalFolderMap.put(URI, recall); )
	 * Define output file name on line 327 ( e.g. FileOutputStream outputStream = new FileOutputStream(new File("./files/expe_oaei_2011/evaluation/ISub-EQ-recall.xlsx")); )
	 */


	public static void main(String[] args) throws AlignmentException, URISyntaxException, FileNotFoundException {

		String evaluatedAlignment = "./files/expe_oaei_2011/logmap_alignments/303304/logmap-303304.rdf";
		//String alignmentFolder = "./files/expe_wn_domain/evaluation/operations";
		String refalign = "./files/expe_oaei_2011/evaluation/referencealignments_equivalence/303-304.rdf";

		evaluateSingleAlignment(evaluatedAlignment, refalign);
		//evaluateAlignmentFolder(alignmentFolder,refalign);
		//runCompleteEvaluation();


	}

}
