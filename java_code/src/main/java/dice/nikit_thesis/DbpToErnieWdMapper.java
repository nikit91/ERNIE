package dice.nikit_thesis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import dice.nikit_thesis.preprocess.ErnieDatasetFormatter;

public class DbpToErnieWdMapper {

	public static final Map<String, Integer> DBP_URI_TO_EMBD_ID_MAP = new HashMap<String, Integer>();
	public static final Map<String, Integer> WD_QID_TO_ENT_ID_MAP = new HashMap<String, Integer>();
	
	public static final Map<String, String> QID_TO_DBP_URI_MAP = new HashMap<String, String>();
	
	public static final Map<Integer, Integer> DBP_EID_TO_ERNIE_EID_MAP  = new HashMap<Integer, Integer>();
	
	// input entity2id file
	public static final String VOCAB_INPUT_PATH = "/home/nikit/Workplace/Thesis/dbpabs_ernie_raw/vocab.txt";
	// input entity2id file
	public static final String ENT2ID_INPUT_PATH = "/home/nikit/Workplace/Thesis/ernie/codebase/kg_embed/entity2id.txt";
	public static final String SAMEAS_INPUT_PATH = "/home/nikit/Downloads/en_sameas.ttl";
	
	public static final String DBP_EID_TO_ERNIE_EID_OUTPUT_PATH = "/home/nikit/Workplace/Thesis/dbpabs_ernie_raw/dbp_eid_2_wd_eid.txt";

	public static void main(String[] args) throws IOException {
		// Read entity2id.txt from Ernie's wikidata
		readEntity2Id(ENT2ID_INPUT_PATH, WD_QID_TO_ENT_ID_MAP);
		// Generate sameas map for dbp_embd_id and qid
		generateSameAsMap(SAMEAS_INPUT_PATH);
		// Read dbp to embedding id vocab map
		Id2VecFormatter.readVocabMap(Id2VecFormatter.VOCAB_INPUT_PATH, DBP_URI_TO_EMBD_ID_MAP);
		// Create a mapping between dbp_embd_id and ernie_ent_id
		genCommonValsMap(DBP_URI_TO_EMBD_ID_MAP, WD_QID_TO_ENT_ID_MAP, QID_TO_DBP_URI_MAP, DBP_EID_TO_ERNIE_EID_MAP);
		// Persist the final map
		ErnieDatasetFormatter.writeMaptoTsv(DBP_EID_TO_ERNIE_EID_MAP, DBP_EID_TO_ERNIE_EID_OUTPUT_PATH, false);

	}

	// Read entity2id.txt from Ernie's wikidata
	private static void readEntity2Id(String filePath, Map<String, Integer> outputMap) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] lineItems = line.split("\t");
				if(lineItems.length == 2) {
					outputMap.put(lineItems[0], Integer.parseInt(lineItems[1]));
				}
			}
		}
	}

	// Generate sameas map for dbp_embd_id and qid
	private static void generateSameAsMap(String sameAsFilePath) {
		// Load the sameas wikidata file
		Model model = ModelFactory.createDefaultModel();
		File sameAsFile = new File(sameAsFilePath);
		model.read(sameAsFile.getAbsolutePath());
		// query the unique Q ids for the corresponding mapping
		Iterator<String> qidItr = WD_QID_TO_ENT_ID_MAP.keySet().iterator();
		while (qidItr.hasNext()) {
			int count = 1;
			// Batch
			Set<String> curBatch = new HashSet<String>();
			while (qidItr.hasNext()) {
				if (count % FineTuneDataFormatter.BATCH_SIZE == 0) {
					break;
				}
				curBatch.add(qidItr.next());
				count++;
			}
			FineTuneDataFormatter.queryQidDbr(model, curBatch, QID_TO_DBP_URI_MAP);
		}
	}
	// Create a mapping between dbp_embd_id and ernie_ent_id
	public static <K,V> void genCommonValsMap(Map<K, V> map1, Map<K, V> map2, Map<K, K> keyMap, Map<V, V> finalMap) {
		for(K key2 : keyMap.keySet()) {
			K key1 = keyMap.get(key2);
			
			V val1 = map1.get(key1);
			V val2 = map2.get(key2);
			if(val1 != null && val2!= null) {
				finalMap.put(val1, val2);
			}
		}
	}

}
