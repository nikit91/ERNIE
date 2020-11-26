package dice.nikit_thesis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import dice.nikit_thesis.model.DbpAbstract;
import dice.nikit_thesis.model.EntityMention;

public class ErnieDatasetFormatter {

	public static final Map<String, String> BERT_VOCAB_MAP = new HashMap<String, String>();
	public static final StringBuilder QUERY_PREFIX = EntityListCreator.QUERY_PREFIX;
	public static final StringBuilder ABS_EXTRCT_QUERY = new StringBuilder();
	public static final StringBuilder EM_EXTRCT_QUERY = new StringBuilder();

	public static final String OUTPUT_DIR_PATH = "/home/nikit/Workplace/Thesis/dbpabs_ernie_raw/json/";
	public static final String MODEL_DIR_PATH = "/home/nikit/Workplace/Thesis/ttl_files_corrected/";

	public static final Map<String, Integer> ENT_URI_VOCAB_MAP = new HashMap<String, Integer>();
	public static int uniqueId = 1;

	public static final String VOCAB_OUTPUT_PATH = "/home/nikit/Workplace/Thesis/dbpabs_ernie_raw/vocab.txt";

	static {

		ABS_EXTRCT_QUERY.append(QUERY_PREFIX);
		ABS_EXTRCT_QUERY.append("SELECT distinct ?r ?u ?c");
		ABS_EXTRCT_QUERY.append(" WHERE { ");
		ABS_EXTRCT_QUERY.append(" ?r a nif:Context . ");
		ABS_EXTRCT_QUERY.append(" ?r nif:isString ?c .");
		ABS_EXTRCT_QUERY.append(" ?r nif:sourceUrl ?u .");
		ABS_EXTRCT_QUERY.append(" } ");

		EM_EXTRCT_QUERY.append(QUERY_PREFIX);
		EM_EXTRCT_QUERY.append("SELECT distinct ?r ?u ?sf ?sp ?ep");
		EM_EXTRCT_QUERY.append(" WHERE { ");
		EM_EXTRCT_QUERY.append(" ?r a nif:Context . ");
		EM_EXTRCT_QUERY.append(" ?em nif:referenceContext ?r .");
		EM_EXTRCT_QUERY.append(" ?em nif:anchorOf ?sf .");
		EM_EXTRCT_QUERY.append(" ?em itsrdf:taIdentRef ?u .");
		EM_EXTRCT_QUERY.append(" ?em nif:beginIndex ?sp .");
		EM_EXTRCT_QUERY.append(" ?em nif:endIndex ?ep .");
		EM_EXTRCT_QUERY.append(" } ");
	}

	public static void main(String[] args) {
		processModelDir(MODEL_DIR_PATH);
		System.out.println("Starting Entity Vocab Writing");
		try {
			writeMaptoTsv(ENT_URI_VOCAB_MAP, VOCAB_OUTPUT_PATH);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Finished!");
	}

	// Read abstract files one by one
	private static void processModelDir(String dirPathStr) {
		File dirPath = new File(dirPathStr);
		for (File abstFile : dirPath.listFiles()) {
			System.out.println("Processing file: " + abstFile);
			// load file in jena model
			Model model = ModelFactory.createDefaultModel();
			try {
				model.read(abstFile.getAbsolutePath());
				processModel(model, abstFile.getName());
				System.out.println("Vocab Entity Map Size: "+ENT_URI_VOCAB_MAP.size());
			} catch (Exception e) {
				System.out.println("Error occurred while reading file: " + abstFile + "\nError: " + e);
			}
		}
	}

	private static void processModel(Model model, String outFileName) throws IOException {
		// Step 1: Fetch all abstracts in the file
		Map<String, DbpAbstract> absMap = new HashMap<String, DbpAbstract>();
		Query query = QueryFactory.create(ABS_EXTRCT_QUERY.toString());
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				String r = soln.getResource("r").getURI(); // Get a result variable - must be a resource
				String u = soln.getResource("u").getURI();
				String c = soln.getLiteral("c").getString();
				absMap.put(r, new DbpAbstract(c, u));
			}
		}
		//System.out.println("Number of abstracts found: " + absMap.size());
		// Step 2: Fetch all entity mentions
		query = QueryFactory.create(EM_EXTRCT_QUERY.toString());
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				String r = soln.getResource("r").getURI(); // Get a result variable - must be a resource
				String u = soln.getResource("u").getURI();
				// fetch id
				int id = getEntityId(u);
				String sf = soln.getLiteral("sf").getString();
				int sp = soln.getLiteral("sp").getInt();
				int ep = soln.getLiteral("ep").getInt();
				EntityMention mention = new EntityMention(u, id, sf, sp, ep);
				DbpAbstract abs = absMap.get(r);
				abs.addEntityMention(mention);
			}
		}
		//System.out.println("Abstracts updated with entity mentions");

		// Step 3: Write abstract json to file
		String outFilePathStr = OUTPUT_DIR_PATH + outFileName + ".json";
		File outFile = new File(outFilePathStr);
		outFile.getParentFile().mkdirs();
		outFile.createNewFile();
		Collection<DbpAbstract> absList = absMap.values();
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
		writer.writeValue(outFile, absList);
		//System.out.println("Written to file : " + outFilePathStr);
	}

	private static void writeMaptoTsv(Map<?, ?> mapToWrite, String filePathStr) throws IOException {
		File outFile = new File(filePathStr);
		outFile.getParentFile().mkdirs();
		outFile.createNewFile();
		try (FileWriter writer = new FileWriter(outFile)) {
			int lineCount = 0;
			for (Object keyObj : mapToWrite.keySet()) {
				Object valueObj = mapToWrite.get(keyObj);

				String keyStr = String.valueOf(keyObj);
				String valStr = String.valueOf(valueObj);

				String outLine = keyStr + "\t" + valStr + "\n";
				writer.write(outLine);

				lineCount++;
				if (lineCount % 1000 == 0) {
					writer.flush();
				}
			}
			writer.flush();
		}
	}

	private static int getEntityId(String entUri) {
		int i = -1;
		if (!ENT_URI_VOCAB_MAP.containsKey(entUri)) {
			ENT_URI_VOCAB_MAP.put(entUri, uniqueId++);
		}
		i = ENT_URI_VOCAB_MAP.get(entUri);
		return i;
	}

	// private static void processAbstract(DbpAbstract dbpAbstract, BufferedWriter
	// entBw, BufferedWriter tokBw) {
	// For each abstract in abstract file, choose sentences with 3 or more entities
	// For each sentence extract all entity ids
	// Map each token to entity id
	// Map each token to its id from vocab (BERT 30k)
	// Write the formatted output to the file in a new line
	// }
}
