package dice.nikit_thesis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class BinFileReader {

	public static void main(String[] args) {
		String pathToBinFile = "/home/nikit/Downloads/dbpedia.bin";
		try {
			printBinLines(10, pathToBinFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void printBinLines(int n, String filePathStr) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(filePathStr))) {
			String line;
			int j = 0;
			String[] info = br.readLine().split(" ");
			int vectorSize = Integer.parseInt(info[1]);
			while ((line = br.readLine()) != null && j < n) {
				final float vector[] = new float[vectorSize];
				final String[] split = line.split(" ");
				System.out.println(split[0]);
				j++;
			}
		}
	}

}
