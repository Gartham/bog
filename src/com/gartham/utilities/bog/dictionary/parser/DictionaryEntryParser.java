package com.gartham.utilities.bog.dictionary.parser;

import org.alixia.javalibrary.streams.CharacterStream;
import org.alixia.javalibrary.streams.PeekableCharacterStream;

import com.gartham.utilities.bog.dictionary.parser.DictionaryEntryParser.Entry;

import zeale.mouse.utils.BufferedParser;

public class DictionaryEntryParser extends BufferedParser<Entry> {

	private final DictionaryEntrySplitter sp;

	public DictionaryEntryParser(DictionaryEntrySplitter sp) {
		this.sp = sp;
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

	@Override
	protected Entry read() {
		if (sp.peek() == null)
			return null;
		PeekableCharacterStream pcs = PeekableCharacterStream
				.from(CharacterStream.from(sp.next().replace("&amp;", "&")));
		return new Entry(pcs.parseBetween("<b>", "</b> "), pcs.parseBetween("<i>", "</i>)"),
				pcs.collect(a -> true).toString().trim().replaceAll("\\r", " "));
	}
}
