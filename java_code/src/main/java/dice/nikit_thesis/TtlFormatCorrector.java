package dice.nikit_thesis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TtlFormatCorrector {

	public static void main(String[] args) throws IOException {
		String outputDir = "C:\\Workplace\\Thesis\\dbp_annotated_abstracts\\temp\\";
		String inputDir = "C:\\Workplace\\Thesis\\dbp_annotated_abstracts\\ttl_files";
		File inpDirPath = new File(inputDir);
		// Read the files one by one
		for(File abstFile: inpDirPath.listFiles()) {
			// Read contents into a string
			String fileContent = Files.readString(abstFile.toPath());
			// replaceAll the required string snippets
			String resContent = fileContent.replace("\"\"\"\"^^xsd", "\\\"\"\"\"^^xsd");
			resContent = resContent.replace("\\\\\"\"\"\"^^xsd", "\\\"\"\"\"^^xsd");
			// write to output directory
			byte[] strToBytes = resContent.getBytes();
			 
		    Files.write(Paths.get(outputDir+abstFile.getName()), strToBytes);
		}
		
	}

}
