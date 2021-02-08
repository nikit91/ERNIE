package dice.nikit_thesis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class WikidataEntityListMaker {
	// Path to entity2id
	public static final String INPUT_PATH = "/home/nikit/Workplace/Thesis/ernie/codebase/kg_embed/entity2id.txt";
	// Path to output file wikidata_ernie_entities.txt
	public static final String OUTPUT_PATH = "/home/nikit/Workplace/Thesis/ernie/codebase/kg_embed/wikidata_ernie_qids.txt";
	// Prefix
	public static final String PREFIX = "http://www.wikidata.org/entity/";

	public static void main(String[] args) {
		try {
			System.out.println("Started!");
			generateEntityList(INPUT_PATH, OUTPUT_PATH);
			System.out.println("Done!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void generateEntityList(String inputFile, String outputFile) throws IOException {
		// open input and output file
		try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
				BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));) {
			int writeCount = 0;
			// read first line with entity count
			String line = br.readLine();
			// Read entity2vec line by line
			// For each line
			while ((line = br.readLine()) != null) {
				// for each line split with tab
				String[] lineItems = line.split("\t");
				// select 1st element in split (QId)
				String qid = lineItems[0];
				// generate output line by adding prefix "http://www.wikidata.org/entity/" to
				// Qid
				//qid = PREFIX + qid +"\n";
				qid += "\n";
				// write the line to output file
				bw.write(qid);
				writeCount++;
				if (writeCount % 10000 == 0) {
					if(writeCount % 1000000 == 0) {
						System.out.println("");
					}
					System.out.print(".");
					bw.flush();
				}
				
			}
		}
	}

}
