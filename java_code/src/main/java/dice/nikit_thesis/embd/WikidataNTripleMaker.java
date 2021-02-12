package dice.nikit_thesis.embd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WikidataNTripleMaker {
	// path to entity2 id
	public static final String ENT_MAP_PATH = "/home/nikit/Workplace/Thesis/ernie/codebase/kg_embed/entity2id.txt";
	// path to relation2id
	public static final String REL_MAP_PATH = "/home/nikit/Workplace/Thesis/wikidata-kg/wiki_graph/relation2id.txt";
	// path to train2id
	public static final String INPUT_TRAIN_PATH = "/home/nikit/Workplace/Thesis/wikidata-kg/wiki_graph/train2id.txt";
	// path to output file
	public static final String OUTPUT_PATH = "/home/nikit/Workplace/Thesis/wikidata-kg/wiki_graph/wikigraph.nt";
	
	
	public static void main(String[] args) {
		try {
			System.out.println("Starting!");
			generateNTriples(ENT_MAP_PATH, REL_MAP_PATH, INPUT_TRAIN_PATH, OUTPUT_PATH);
			System.out.println("Done.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void generateNTriples(String entMapInputPath, String relMapInputPath, String tripleInputPath,
			String outputFilePath) throws Exception {
		// Load id to ent map
		Map<Integer, String> entMap = loadIdMap(entMapInputPath);
		// Load id to rel map
		Map<Integer, String> relMap = loadIdMap(relMapInputPath);
		// Open train2vec and output file
		// open input and output file
		try (BufferedReader br = new BufferedReader(new FileReader(tripleInputPath));
				BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath));) {
			int writeCount = 0;
			// read first line with entity count
			String line = br.readLine();
			// For each line
			while ((line = br.readLine()) != null) {
				// for each line split with space
				String[] lineItems = line.split(" ");
				String outputLine;
				// extract subj, obj and rel ids
				Integer subjId = Integer.parseInt(lineItems[0]);
				Integer objId = Integer.parseInt(lineItems[1]);
				Integer relId = Integer.parseInt(lineItems[2]);
				// fetch mappings
				String subj = entMap.get(subjId);
				String obj = entMap.get(objId);
				String rel = relMap.get(relId);
				// form output string
				outputLine = "<"+subj+"> "+"<"+rel+"> "+"<"+obj+"> .\n";
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
		}
	}

	public static Map<Integer, String> loadIdMap(String idMapPath) throws IOException {
		Map<Integer, String> resMap = new HashMap<Integer, String>();
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
				resMap.put(id, itemId);
			}
		}
		System.out.println("Finished reading file: " + idMapPath + "\nMapping Size: " + resMap.size());
		return resMap;
	}

}
