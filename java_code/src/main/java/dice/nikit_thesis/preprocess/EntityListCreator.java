package dice.nikit_thesis.preprocess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import org.apache.jena.rdf.model.Resource;

public class EntityListCreator {

	public static final StringBuilder QUERY_PREFIX = new StringBuilder();
	public static final StringBuilder RES_EXTRCT_QUERY = new StringBuilder();
	public static final int BATCH_SIZE = 200;
	static {
		QUERY_PREFIX.append("PREFIX dbo: <http://dbpedia.org/ontology/> ");
		QUERY_PREFIX.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns/> ");
		QUERY_PREFIX.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ");
		QUERY_PREFIX.append("PREFIX nif: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> ");
		QUERY_PREFIX.append("PREFIX itsrdf: <http://www.w3.org/2005/11/its/rdf#> ");

		RES_EXTRCT_QUERY.append(QUERY_PREFIX);
		RES_EXTRCT_QUERY.append("SELECT distinct ?r ");
		RES_EXTRCT_QUERY.append(" WHERE { ");
		RES_EXTRCT_QUERY.append(" { [] itsrdf:taIdentRef ?r .} ");
		RES_EXTRCT_QUERY.append(" UNION ");
		RES_EXTRCT_QUERY.append(" { [] nif:sourceUrl ?r .} ");
		RES_EXTRCT_QUERY.append(" } ");
	}

	public static void main(String[] args) throws IOException {
		String folderPath = "C:\\Workplace\\Thesis\\dbp_annotated_abstracts\\ttl_files_corrected";
		String mapModelPathStr = "C:\\Workplace\\Thesis\\sameas-all-wikis_wikidata.ttl";
		// String mapModelPathStr = "C:\\Workplace\\Thesis\\page-ids_wikidata.ttl";
		String outputMapPath = "C:\\Workplace\\Thesis\\resource-map.csv";
		
		System.out.println("- Starting extraction of Unique URIs -");
		// Set<String> resourceSet = getUniqueUris(folderPath);
		Set<String> resourceSet = new HashSet<String>();
		System.out.println("- Finding links -");
		Map<String, String> resMap = findWikiDataResources(resourceSet, mapModelPathStr);
		// System.out.println(resourceSet);
		// Write the map to a file
		System.out.println("- Writing results to file -");
		writeMapToCsv(resMap, outputMapPath);
		System.out.println("- Process Finished -");
	}

	private static Set<String> getUniqueUris(String dirPathStr) {
		Set<String> uriSet = new HashSet<String>();
		// Load abstract subgraphs one by one
		File dirPath = new File(dirPathStr);
		for (File abstFile : dirPath.listFiles()) {
			System.out.println("Processing file: " + abstFile);
			// load file in jena model
			// Model model = RDFDataMgr.loadModel(abstFile.getAbsolutePath());
			Model model = ModelFactory.createDefaultModel();
			try {
				model.read(abstFile.getAbsolutePath());
				// extract all the "itsrdf:taIdentRef" resources and "nif:sourceUrl" to a set
				Query query = QueryFactory.create(RES_EXTRCT_QUERY.toString());
				try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
					ResultSet results = qexec.execSelect();
					for (; results.hasNext();) {
						QuerySolution soln = results.nextSolution();
						Resource r = soln.getResource("r"); // Get a result variable - must be a resource
						uriSet.add(r.getURI());
					}
				}
			} catch (Exception e) {
				System.out.println("Error occurred while reading file: " + abstFile + "\nError: " + e);
			}
		}

		return uriSet;
	}

	private static Map<String, String> findWikiDataResources(Set<String> uriSet, String mapModelPathStr) {
		System.out.println("Number of DBPedia resource uris received: " + uriSet.size());
		Map<String, String> resMap = new HashMap<String, String>();
		// Load sameAs subgraph for Wikidata
		File mapModelFile = new File(mapModelPathStr);
		Model model = ModelFactory.createDefaultModel();
		model.read(mapModelFile.getAbsolutePath());
		// For each unique resource is the resource set generated previously, find the
		// corresponding Wikidata uri and put in a map
		int queryCount = (uriSet.size() / BATCH_SIZE);
		if (uriSet.size() % BATCH_SIZE != 0) {
			queryCount++;
		}
		List<String> uriList = new ArrayList<String>();
		uriList.addAll(uriSet);
		for (int i = 0; i < queryCount; i++) {
			System.out.println("Executing Query :" + (i + 1));
			int fromIndex = i * BATCH_SIZE;
			int end = fromIndex + BATCH_SIZE;
			int toIndex = (end <= uriList.size()) ? end : uriList.size();
			List<String> subList = uriList.subList(fromIndex, toIndex);
			String queryStr = getResourceLinkQuery(subList);
			Query query = QueryFactory.create(queryStr);
			try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
				ResultSet results = qexec.execSelect();
				for (; results.hasNext();) {
					QuerySolution soln = results.nextSolution();
					Resource wdr = soln.getResource("wdr");
					Resource dbpr = soln.getResource("dbpr");
					resMap.put(dbpr.getURI(), wdr.getLocalName());
				}
			}
		}
		System.out.println("Number of WikiData resource uris mapped: " + uriSet.size());
		return resMap;
	}

	private static String getResourceLinkQuery(Collection<String> list) {
		StringBuilder rlQuery = new StringBuilder();
		rlQuery.append(QUERY_PREFIX);
		rlQuery.append("SELECT distinct ?dbpr ?wdr");
		rlQuery.append(" WHERE { ");
		rlQuery.append(" ?wdr <http://www.w3.org/2002/07/owl#sameAs> ?dbpr .");
		rlQuery.append(" FILTER ( ?dbpr in ").append(getInParam(list)).append(") . ");
		rlQuery.append(" } ");
		return rlQuery.toString();
	}

	private static String getInParam(Collection<String> list) {
		StringBuilder inClause = new StringBuilder();
		inClause.append(" ( ");
		for (String resourceUri : list) {
			inClause.append("<");
			inClause.append(resourceUri);
			inClause.append(">,");
		}
		inClause.deleteCharAt(inClause.length() - 1);
		inClause.append(" ) ");
		return inClause.toString();
	}

	private static void writeMapToCsv(Map<String, String> resMap, String outputFilePath) throws IOException {
		File file = new File(outputFilePath);
		try (BufferedWriter bf = new BufferedWriter(new FileWriter(file))) {
			for (Map.Entry<String, String> entry : resMap.entrySet()) {
				bf.write(entry.getKey() + " , " + entry.getValue());
				bf.newLine();
			}
			bf.flush();
		}
	}

}
