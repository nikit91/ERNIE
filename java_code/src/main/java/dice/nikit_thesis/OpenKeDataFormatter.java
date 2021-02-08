package dice.nikit_thesis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenKeDataFormatter {

	// path to input rdf file in n-triples format
	public static final String RDF_INPUT_PATH = "/home/nikit/Workplace/Thesis/DbPedia-2015-04/dbp_kg_noliterals.nt";
	// path to out train file
	public static final String TRAIN_OUT_PATH = "/home/nikit/Workplace/Thesis/DbPedia-2015-04/openke-format/train2id.txt";
	// path to output ent2id file
	public static final String ENT_OUT_PATH = "/home/nikit/Workplace/Thesis/DbPedia-2015-04/openke-format/entity2id.txt";
	// path to output rel2id file
	public static final String REL_OUT_PATH = "/home/nikit/Workplace/Thesis/DbPedia-2015-04/openke-format/relation2id.txt";

	public static final String TRIPLE_PATTERN = "^<(.+)> <(.+)> <(.+)> \\.$";
	
	public static int entCount = 0;
	public static int relCount = 0;
	
	public static void main(String[] args) {
		try {
			generateFormattedFiles(RDF_INPUT_PATH, TRAIN_OUT_PATH, ENT_OUT_PATH, REL_OUT_PATH);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void generateFormattedFiles(String inputFile, String trainOutFile, String entOutFile, String relOutFile) throws IOException {
		System.out.println("Starting process!");
		String tempFilePath = trainOutFile + ".temp";
		int tripleCount = 0;
		
		Map<String, Integer> ent2id = new HashMap<String, Integer>();
		Map<String, Integer> rel2id = new HashMap<String, Integer>();
		Pattern p = Pattern.compile(TRIPLE_PATTERN);
		// Read the rdf file line by line
		try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
				BufferedWriter bw = new BufferedWriter(new FileWriter(tempFilePath));) {
			String line;
			// For each line
			while ((line = br.readLine()) != null) {
				// Make sure its a triple "^<(.+)> <(.+)> <(.+)> \\.$"
				Matcher m = p.matcher(line);
				if (m.find()) {
					// Capture all the values matching their groups
					String subj = m.group(1);
					String pred = m.group(2);
					String obj = m.group(3);
					// create/fetch unique id for the values
					int subjId = getEntId(subj, ent2id);
					int relId = getRelId(pred, rel2id);
					int objId = getEntId(obj, ent2id);
					// construct the output line subj_id+" "+obj_id+" "+rel_id
					String outputLine = subjId+" "+objId+" "+relId+"\n";
					// increment triple counter
					tripleCount++;
					// write the line to the temp output file "train2id.txt_temp"
					bw.write(outputLine);
					if (tripleCount % 100000 == 0) {
						if(tripleCount % 10000000 == 0) {
							System.out.println("");
						}
						System.out.print(".");
						bw.flush();
					}
				}

			}
		}
		System.out.println(tripleCount+" Triples processed. Writing output to train file.");
		try (BufferedReader br = new BufferedReader(new FileReader(tempFilePath));
				BufferedWriter bw = new BufferedWriter(new FileWriter(trainOutFile));) {
			// Write triple count as first line in train2id.txt file
			bw.write(tripleCount+"\n");
			String line;
			// Write all lines from temp file to train file
			while ((line = br.readLine()) != null) {
				bw.write(line+"\n");
			}
		}
		
		// Delete temp file
		File tempFile = new File(tempFilePath); 
		tempFile.delete();
		
		System.out.println("Train file written.\nNumber of unique entities found: "+ent2id.size()+"\nNumber of unique relations found: "+rel2id.size());
		System.out.println("Writing entitiy and relation ids.");
		// write entity and relation id maps as tsv (tab separated will do)
		ErnieDatasetFormatter.writeMaptoTsv(ent2id, entOutFile, true);
		ErnieDatasetFormatter.writeMaptoTsv(rel2id, relOutFile, true);
		System.out.println("Done!");
	}
	
	private static int getEntId(String ent, Map<String, Integer> entMap) {
		Integer id = entMap.get(ent);
		if(id == null) {
			id = entCount++;
			entMap.put(ent, id);
		}
		return id;
	}
	
	private static int getRelId(String rel, Map<String, Integer> relMap) {
		Integer id = relMap.get(rel);
		if(id == null) {
			id =  relCount++;
			relMap.put(rel, id);
		}
		return id;
	}

}
