package dice.nikit_thesis;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class FineTuneDataFormatter {
	
	public static final String DATA_DIR_PATH_STR = "/home/nikit/Workplace/Thesis/fine-tune-data/data/";
	// create a list of unique Q ids used
	public static final Set<String> UNIQUE_QID = new HashSet<String>();
	public static final ObjectMapper JSON_MAPPER = new ObjectMapper();
	
	public static void main(String[] args) {
		// Read all the json files
		try {
			readAllJsonFiles(DATA_DIR_PATH_STR);
			System.out.println("Number of unique entities found: "+UNIQUE_QID.size());
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
		//for each entry in json array
		while(jsonNodeItr.hasNext()) {
			JsonNode node = jsonNodeItr.next();
			if(!node.hasNonNull("ents")) {
				continue;
			}
			//fetch "ents" as json array
			ArrayNode entsArr = (ArrayNode) node.get("ents");
			Iterator<JsonNode> entsItr = entsArr.iterator();
			//for each array entry in ents array
			while(entsItr.hasNext()) {
				ArrayNode entItem = (ArrayNode) entsItr.next();
				if(entItem.hasNonNull(0)) {
					//read the first item of array
					String qid = entItem.get(0).asText();
					// put this item in unique Q_Id set
					UNIQUE_QID.add(qid);
				}
			}
		}
	}
	private static void processSameAs(String sameAsFilePath) {
		// Load the sameas wikidata file
		Model model = ModelFactory.createDefaultModel();
		File sameAsFile = new File(sameAsFilePath);
		model.read(sameAsFile.getAbsolutePath());
		
		// query the unique Q ids for the corresponding mapping
		// use the dbpedia url to get corresponding id from dbpedia's vocab
		// create a map of Q id to embedding id
		// fetch the line number of embedding based on embedding id
		// create a map of Q id to embedding id
		// save map as Qid-to-line-number.txt in the specific embeddings folder
	}
	
	private static void queryModel(Model model) {
		
	}
	

}
