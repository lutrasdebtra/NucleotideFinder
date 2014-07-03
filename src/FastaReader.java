import java.io.BufferedReader;
/* 
 * Reads a Fasta File and produces title and sequence ArrayLists
 * Stuart Bradley
 * 2-6-2014
 * 
 * I'm sure there's a much 
 * Fasta way of doing this
 * But puns are pretty fun
 */

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class FastaReader {
	ArrayList<String> titles = new ArrayList<String>();
	
	public ArrayList<String> FastaReader_sequences(File file) {
		BufferedReader br;
		StringBuilder sb = new StringBuilder();
		ArrayList<String> fastaSequences = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				// Checks if it's a new sequence in the file
				if (line.startsWith(">")) {
					titles.add(line);
					// If there's a previous StringBuilder, output it and restart
					if (sb.length() > 0) {
						String s = sb.toString();
						fastaSequences.add(s);
						sb = new StringBuilder();
					}
				} else {
					//If it's not a title line, append to current StringBuilder
					sb.append(line.toUpperCase());
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			return fastaSequences;
		}
		return fastaSequences;
	}
}
