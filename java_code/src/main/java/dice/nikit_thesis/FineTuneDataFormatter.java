package dice.nikit_thesis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class FineTuneDataFormatter {
	
	public static final String DATA_DIR_PATH_STR = "/home/nikit/Workplace/Thesis/fine-tune-data/data/";
	// input embeddings file
	public static final String EMBD_INPUT_PATH = "/home/nikit/Workplace/Thesis/KG_Embeddings/rdf2vec-dbpedia-2015-04-with-links/spec_ent_vectors.txt";
	public static final String SAMEAS_INPUT_PATH = "/home/nikit/Downloads/en_sameas.ttl";
	public static final String OUTPUT_MAP_PATH = "/home/nikit/Workplace/Thesis/KG_Embeddings/rdf2vec-dbpedia-2015-04-with-links/qid-line-mapping.txt";
	// create a list of unique Q ids used
	public static final Set<String> UNIQUE_QID = new HashSet<String>();
	public static final Map<String, String> QID_DBR_MAP = new HashMap<String, String>();
	public static final int BATCH_SIZE = 200;
	
	public static final Map<String, Integer> ENT_URI_VOCAB_MAP = new HashMap<String, Integer>();
	public static final Map<String, Integer> QID_EMBDID_MAP = new HashMap<String, Integer>();
	public static final Map<Integer, Integer> EMBDID_LINE_MAP = new HashMap<Integer, Integer>();
	public static final Map<String, Integer> QID_LINE_MAP = new HashMap<String, Integer>();
	public static final ObjectMapper JSON_MAPPER = new ObjectMapper();
	
	public static final StringBuilder QUERY_PREFIX = new StringBuilder();
	
	static {
		QUERY_PREFIX.append("PREFIX dbr: <http://dbpedia.org/resource/> ");
		QUERY_PREFIX.append("PREFIX wdr: <http://wikidata.dbpedia.org/resource/> ");
		QUERY_PREFIX.append("PREFIX owl: <http://www.w3.org/2002/07/owl#> ");
		
		// http://www.w3.org/2002/07/owl#sameAs
		// http://wikidata.dbpedia.org/resource/ 
		// http://dbpedia.org/resource/
	}
	
	public static void main(String[] args) {
		// Read all the json files
		try {
			readAllJsonFiles(DATA_DIR_PATH_STR);
			System.out.println("Number of unique entities found: "+UNIQUE_QID.size());
			processSameAs(SAMEAS_INPUT_PATH);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void readAllJsonFiles(String dataDirPath) throws IOException {
		File dirPath = new File(dataDirPath);
		for (File jsonFile : dirPath.listFiles()) {
			if(jsonFile.isDirectory()) {
				readAllJsonFiles(jsonFile.getAbsolutePath());
			}
			else if (jsonFile.getName().matches("((dev)|(test)|(train))\\.json$")){
				jsonFileReader(jsonFile);
			}
		}
	}
	
	
	private static void jsonFileReader(File jsonFile) throws IOException {
		//read the json file as a jsonarray
		System.out.println("reading file: "+jsonFile.getAbsolutePath());
		ArrayNode fileCont = JSON_MAPPER.readValue(jsonFile, ArrayNode.class);
		Iterator<JsonNode> jsonNodeItr = fileCont.iterator();
		int invalidQidCount = 0;
		int validQidCount = 0;
		String nodetoRead = "ents";
		String parent = jsonFile.getParent();
		if(parent.matches(".*tacred\\/?$")) {
			nodetoRead = "ann";
		}
		//for each entry in json array
		while(jsonNodeItr.hasNext()) {
			JsonNode node = jsonNodeItr.next();
			if(!node.hasNonNull(nodetoRead)) {
				continue;
			}
			//fetch "ents" as json array
			ArrayNode entsArr = (ArrayNode) node.get(nodetoRead);
			Iterator<JsonNode> entsItr = entsArr.iterator();
			//for each array entry in ents array
			while(entsItr.hasNext()) {
				ArrayNode entItem = (ArrayNode) entsItr.next();
				if(entItem.hasNonNull(0)) {
					//read the first item of array
					String qid = entItem.get(0).asText();
					// put this item in unique Q_Id set
					if(qid.matches("Q\\d+")) {
						UNIQUE_QID.add(qid);
						validQidCount++;
					}
					else
						invalidQidCount++;
				}
			}
		}
		System.out.println("Valid QID Count: "+validQidCount);
		System.out.println("Invalid QID Count: "+invalidQidCount);
	}
	private static void processSameAs(String sameAsFilePath) throws IOException {
		// Load the sameas wikidata file
		Model model = ModelFactory.createDefaultModel();
		File sameAsFile = new File(sameAsFilePath);
		model.read(sameAsFile.getAbsolutePath());
		// query the unique Q ids for the corresponding mapping
		Iterator<String> qidItr = UNIQUE_QID.iterator();
		while(qidItr.hasNext()) {
			int count = 1;
			// Batch
			Set<String> curBatch = new HashSet<String>();
			while(qidItr.hasNext()) {
				if(count%BATCH_SIZE == 0) {
					break;
				}
				curBatch.add(qidItr.next());
				count++;
			}
			queryQidDbr(model, curBatch, QID_DBR_MAP);
		}
		// read dbr to embedding id map from embeddings vocab file
		Id2VecFormatter.readVocabMap(Id2VecFormatter.VOCAB_INPUT_PATH, ENT_URI_VOCAB_MAP);
		// use the dbpedia url to get corresponding id from dbpedia's vocab
		// create a map of Q id to embedding id
		for(String qid : QID_DBR_MAP.keySet()) {
			String val = QID_DBR_MAP.get(qid);
			if(ENT_URI_VOCAB_MAP.containsKey(val)) {
				QID_EMBDID_MAP.put(qid, ENT_URI_VOCAB_MAP.get(val));
			}
		}
		// fetch the line number of embedding based on embedding id
		generateEmbdngIndxMap();
		int matchedEnts = 0;
		// create a map of Q id to embedding id
		for(String qid : QID_EMBDID_MAP.keySet()) {
			Integer embdId = QID_EMBDID_MAP.get(qid);
			int val = -1;
			if(EMBDID_LINE_MAP.containsKey(embdId)) {
				val = EMBDID_LINE_MAP.get(embdId);
				matchedEnts++;
			}
			QID_LINE_MAP.put(qid, val);
		}
		System.out.println("Final Map size: "+QID_LINE_MAP.size()+"\nMatched entites: "+matchedEnts);
		// save map as Qid-to-line-number.txt in the specific embeddings folder
		ErnieDatasetFormatter.writeMaptoTsv(QID_LINE_MAP, OUTPUT_MAP_PATH, false);
	}
	
	private static void generateEmbdngIndxMap() throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(EMBD_INPUT_PATH));) {
			String line;
			// starting from zero, since ERNIE's code adds 1 to the indexes
			int lineId = 0;
			while ((line = br.readLine()) != null) {
				// for each line in embeddings file
				// split with \t
				String[] lineItems = line.split("\t");
				// fetch id for the first item from map
				Integer embdId = Integer.valueOf(lineItems[0]);
				EMBDID_LINE_MAP.put(embdId, lineId);
				lineId++;
			}
		}
	}
	
	public static void queryQidDbr(Model model, Collection<String> qidBatch, Map<String, String> qidDbrMap) {
		StringBuilder valueArrStr = new StringBuilder(" { ");
		for(String qid : qidBatch) {
			valueArrStr.append(" wdr:").append(qid).append(" ");
		}
		valueArrStr.append(" } ");
		
		StringBuilder queryStr = new StringBuilder();
		queryStr.append(QUERY_PREFIX);
		queryStr.append(" SELECT ?s ?qid ?o WHERE { ");
		queryStr.append(" ?s owl:sameAs ?o. ");
		queryStr.append(" VALUES ?s ").append(valueArrStr);
		queryStr.append(" BIND(STRAFTER(STR(?s) , STR(wdr:)) as ?qid) ");
		queryStr.append(" } ");
		
		Query query = QueryFactory.create(queryStr.toString());
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				String qid = soln.getLiteral("qid").getString(); // Get a result variable - must be a resource
				String dbr = soln.getResource("o").getURI();
				if(qidDbrMap.containsKey(qid)) {
					System.out.println("Duplicate entry detected for key: "+qid+" value: "+dbr+" previous value: "+qidDbrMap.get(qid));
				}
				qidDbrMap.put(qid, dbr);
			}
		}
		
	}
	
	

}
