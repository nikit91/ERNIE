package dice.nikit_thesis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class FormatDbpTransETsv {
	// path to dbp vocab
	public static final String VOCAB_MAP_PATH = "/scratch/hpc-prf-nina/nikit/temp_res/vocab.txt";
	// path to dbp entity2id
	public static final String ENT_MAP_PATH = "/scratch/hpc-prf-nina/nikit/PyTorch-BigGraph/data/dbpedia-2015-04/entity2id.txt";
	// path to input vector file
	public static final String INPUT_VEC_PATH = "/scratch/hpc-prf-nina/nikit/PyTorch-BigGraph/dbpedia1504_old_tsv/entity_embeddings_dbp1504_old.tsv";
	// path to output vector file
	public static final String OUTPUT_VEC_PATH = "/scratch/hpc-prf-nina/nikit/ERNIE/kg_embeddings/transe_pbg_dot_dbp1504/spec_ent_vectors.txt";
	
	public static void main(String[] args) {
		System.out.println("Starting!");
		try {
			generateFormatterTsv(VOCAB_MAP_PATH, ENT_MAP_PATH, INPUT_VEC_PATH, OUTPUT_VEC_PATH);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done.");
	}
	
	private static void generateFormatterTsv(String entVocabFile, String entIdFile, String inputVectorFile, String outputVectorFile) throws IOException {
		// Read vocab map
		Map<String, Integer> vocabMap = new HashMap<String, Integer>();
		Id2VecFormatter.readVocabMap(entVocabFile, vocabMap, false);
		System.out.println("Vocab map read. Entry count: "+vocabMap.size());
		// Read entity2id map
		Map<Integer, String> ent2IdMap = WikidataNTripleMaker.loadIdMap(entIdFile);
		// Open input vector and output file
		try (BufferedReader br = new BufferedReader(new FileReader(inputVectorFile));
				BufferedWriter bw = new BufferedWriter(new FileWriter(outputVectorFile));) {
			int writeCount = 0;
			int readCount = 0;
			// read first line with entity count
			String line = br.readLine();
			// For each line
			while ((line = br.readLine()) != null) {
				// for each line split with space
				String[] lineItems = line.split("\t");
				String outputLine;
				// extract subj, obj and rel ids
				Integer entId = Integer.parseInt(lineItems[0]);
				// fetch mappings
				String uri = ent2IdMap.get(entId);
				if(uri!=null && vocabMap.containsKey(uri)) {
					Integer embdId = vocabMap.get(uri);
					lineItems[0] = String.valueOf(embdId);
					// form output string
					outputLine = StringUtils.join(lineItems, '\t');
					outputLine+="\n";
					// write the line to output file
					bw.write(outputLine);
					writeCount++;
					if (writeCount % 10000 == 0) {
						if (writeCount % 1000000 == 0) {
							System.out.println("");
						}
						System.out.print(".");
						bw.flush();
					}
				}
				
				readCount++;
				if(readCount%100000 == 0) {
					System.out.println("Read "+readCount+" lines.");
				}

			}
			System.out.println("\n"+writeCount+" lines written.");
		}
	}

}
