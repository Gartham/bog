package com.gartham.utilities.bog.dictionary.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.alixia.javalibrary.JavaTools;
import org.alixia.javalibrary.streams.CharacterStream;

import com.gartham.utilities.bog.dictionary.parser.DictionaryEntryParser.Entry;

public class DictionaryLineParser {

	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		System.out.print("Enter the directory with the dictionary files: ");
		File dir = new File(s.nextLine());
		File[] files = dir.listFiles();
		Map<String, List<Entry>> entriesByType = new HashMap<>();
		for (File f : files)
			try {
				DictionaryEntryParser dep = new DictionaryEntryParser(new DictionaryEntrySplitter(
						CharacterStream.from(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))));
				while (dep.peek() != null)
					JavaTools.putIntoListMap(entriesByType, dep.peek().getPos(), dep.next(), ArrayList::new);
			} catch (Exception e) {
				e.printStackTrace();
			}

		entriesByType.keySet().forEach(System.out::println);

	}

}
