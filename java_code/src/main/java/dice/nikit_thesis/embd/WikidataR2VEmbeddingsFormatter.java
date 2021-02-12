package dice.nikit_thesis.embd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class WikidataR2VEmbeddingsFormatter {

	// Path to entity2id file
	//public static final String ENT_MAP_PATH = "/home/nikit/Workplace/Thesis/ernie/codebase/kg_embed/entity2id.txt";
	public static final String ENT_MAP_PATH = "/scratch/hpc-prf-nina/nikit/ERNIE/kg_embed/entity2id.txt";
	// Path to input vectors file
	//public static final String INPUT_VEC_PATH = "/home/nikit/Workplace/Thesis/KG_Embeddings/rdf2vec-wikidata-ernie/vectors.txt";
	public static final String INPUT_VEC_PATH = "/scratch/hpc-prf-nina/nikit/jRDF2Vec/jars/wikidata_ernie_rdf2vec/walks/vectors.txt";
	// Path to output file
	//public static final String OUTPUT_VEC_PATH = "/home/nikit/Workplace/Thesis/KG_Embeddings/rdf2vec-wikidata-ernie/entity2vec.vec";
	public static final String OUTPUT_VEC_PATH = "/scratch/hpc-prf-nina/nikit/ERNIE/kg_embeddings/rdf2vec-wikidata-ernie-new/entity2vec.vec";
	
	public static void main(String[] args) {
		System.out.println("Starting!");
		try {
			createEntity2Vec(INPUT_VEC_PATH, ENT_MAP_PATH, OUTPUT_VEC_PATH);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done.");
	}

	private static void createEntity2Vec(String inputVecFile, String entMapPath, String outputVecFile)
			throws Exception {
		// read entity2id map
		Map<String, Integer> entMap = loadEnt2IdMap(ENT_MAP_PATH);
		// Output Vec Map
		Map<Integer, String[]> outVecMap = new HashMap<Integer, String[]>();
		// open embeddings file
		try (BufferedReader br = new BufferedReader(new FileReader(inputVecFile));
				BufferedWriter bw = new BufferedWriter(new FileWriter(outputVecFile));) {
			String line;
			int readCount = 0;
			// for each line
			while ((line = br.readLine()) != null) {
				// map embeddings to the id
				String[] items = line.split(" ");
				Integer entId = entMap.get(items[0]);
				if(entId != null) {
					outVecMap.put(entId, Arrays.copyOfRange(items, 1, items.length));
				}
				readCount++;
				if(readCount%10000 == 0) {
					System.out.println("Read "+readCount+" lines.");
				}
			}

			int writeCount = 0;
			System.out.println("Array written to output vector map of size: " + outVecMap.size());
			List<Integer> keyList = new ArrayList<Integer>(outVecMap.keySet());
			Collections.sort(keyList);
			// for each key in embeddings map keyset (sorted on integer)
			for (Integer key : keyList) {
				// write embedding to file
				String outputStr = StringUtils.join(outVecMap.get(key), '\t') + "\n";
				bw.write(outputStr);
				writeCount++;
				if (writeCount % 10000 == 0) {
					if (writeCount % 1000000 == 0) {
						System.out.println("");
					}
					System.out.print(key+" ");
					bw.flush();
				}
			}

		}

	}

	public static Map<String, Integer> loadEnt2IdMap(String idMapPath) throws IOException {
		Map<String, Integer> resMap = new HashMap<String, Integer>();
		try (BufferedReader br = new BufferedReader(new FileReader(idMapPath))) {
			// read first line with entity count
			String line = br.readLine();
			// For each line
			while ((line = br.readLine()) != null) {
				// for each line split with tab
				String[] lineItems = line.split("\t");
				// select 1st element in split
				String itemId = lineItems[0];
				Integer id = Integer.parseInt(lineItems[1]);
				resMap.put(itemId, id);
			}
		}
		System.out.println("Finished reading file: " + idMapPath + "\nMapping Size: " + resMap.size());
		return resMap;
	}

}
