package com.gartham.utilities.bog.dictionary.parser;

import org.alixia.javalibrary.streams.CharacterStream;
import org.alixia.javalibrary.streams.PeekableCharacterStream;

import com.gartham.utilities.bog.dictionary.parser.DictionaryEntryParser.Entry;

import zeale.mouse.utils.BufferedParser;

public class DictionaryEntryParser extends BufferedParser<Entry> {
	
	public static void main(String[] args) {
		PeekableCharacterStream pcs = PeekableCharacterStream.from(CharacterStream.from("aabaabb"));
		System.out.println(pcs.collectTo("aabb"));
	}

	private final PeekableCharacterStream in;

	public DictionaryEntryParser(PeekableCharacterStream in) {
		this.in = in;
	}

	public DictionaryEntryParser(CharacterStream in) {
		this(PeekableCharacterStream.from(in));
	}

	public static class Entry {
		private final String word, pos, definition;

		private Entry(String word, String pos, String definition) {
			this.word = word;
			this.pos = pos;
			this.definition = definition;
		}

		public String getWord() {
			return word;
		}

		public String getPos() {
			return pos;
		}

		public String getDefinition() {
			return definition;
		}

	}

	private boolean parsedHeader;

	@Override
	protected Entry read() {

		if (!parsedHeader) {

		}

		StringBuilder sb = new StringBuilder();
		while (true) {

		}
//		return null;
	}
}
