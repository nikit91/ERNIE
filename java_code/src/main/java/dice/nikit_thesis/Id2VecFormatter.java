package dice.nikit_thesis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Id2VecFormatter {

	// input entity2id file
	public static final String VOCAB_INPUT_PATH = "/home/nikit/Workplace/Thesis/dbpabs_ernie_raw/vocab.txt";
	// input embeddings file
	public static final String EMBD_INPUT_PATH = "/home/nikit/Workplace/Thesis/KG_Embeddings/rdf2vec-dbpedia-2015-04-with-links/vectors.txt";
	// output embeddings file
	public static final String EMBD_OUTPUT_PATH = "/home/nikit/Workplace/Thesis/KG_Embeddings/rdf2vec-dbpedia-2015-04-with-links/spec_ent_vectors.txt";

	public static final Map<String, Integer> ENT_URI_VOCAB_MAP = new HashMap<String, Integer>();

	public static void main(String[] args) {
		try {
			readVocabMap(VOCAB_INPUT_PATH, ENT_URI_VOCAB_MAP);
			System.out.println("Vocab Size: "+ENT_URI_VOCAB_MAP.size());
			processEmbeddings(EMBD_INPUT_PATH, EMBD_OUTPUT_PATH);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	// read entity2id file into map
	public static void readVocabMap(String filePath, Map<String, Integer> entUriVocabMap, boolean hasCount) throws IOException {

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			if(hasCount) {
				line = br.readLine();
				System.out.println("Count read: "+line);
			}
			while ((line = br.readLine()) != null) {
				String[] lineItems = line.split("\t");
				entUriVocabMap.put(lineItems[0], Integer.parseInt(lineItems[1]));
			}
		}

	}

	// read entity2id file into map
	public static void readVocabMap(String filePath, Map<String, Integer> entUriVocabMap) throws IOException {
		readVocabMap(filePath, entUriVocabMap, false);
	}

	private static void processEmbeddings(String inputFilePath, String outputFilePath) throws IOException {

		File outFile = new File(outputFilePath);
		outFile.getParentFile().mkdirs();
		outFile.createNewFile();
		int lineCount = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
				BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));) {
			System.out.println("Processing Started!");
			String line;
			while ((line = br.readLine()) != null) {
				// for each line in embeddings file
				// split with \t
				// String[] lineItems = line.split("\t");
				String[] lineItems = line.split("\s");
				// fetch id for the first item from map
				String entUri = lineItems[0];
				if (ENT_URI_VOCAB_MAP.containsKey(entUri)) {
					Integer id = ENT_URI_VOCAB_MAP.get(entUri);
					lineItems[0] = String.valueOf(id);
					// join back using \t
					String outLine = String.join("\t", lineItems);
					// write line to output embeddings file
					bw.write(outLine);
					bw.newLine();
					// flush when each multiplier of 1000 reached
					lineCount++;
					if (lineCount % 10000 == 0) {
						System.out.print(".");
						bw.flush();
					}
				}

			}
			// flush
			bw.flush();
		}
		System.out.println("\nTotal Numbers of line written: " + lineCount);
	}

}
