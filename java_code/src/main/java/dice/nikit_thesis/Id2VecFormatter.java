package dice.nikit_thesis;

public class Id2VecFormatter {
	
	// input embeddings file
	public static final String EMBD_INPUT_PATH = "/home/nikit/Workplace/Thesis/dbpabs_ernie_raw/vocab.txt";
	// input entity2id file
	public static final String VOCAB_INPUT_PATH = "/home/nikit/Workplace/Thesis/dbpabs_ernie_raw/vocab.txt";
	// output embeddings file
	public static final String EMBD_OUTPUT_PATH = "/home/nikit/Workplace/Thesis/dbpabs_ernie_raw/vocab.txt";
	
	// read entity2id file into map
	// for each line in embeddings file
		// split with \t
		// fetch id for the first item from map
		// join back using \t
		// write line to output embeddings file
		// flush when each multiplier of 1000 reached
	// flush

}
