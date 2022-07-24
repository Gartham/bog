package com.gartham.utilities.bog.dictionary.parser;

import java.io.File;
import java.util.Scanner;

public class DictionaryLineParser {

	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		System.out.print("Enter the directory with the dictionary files: ");
		File dir = new File(s.nextLine());
		for (File f : dir.listFiles()) {
			
		}
	}

}
