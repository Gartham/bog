package com.gartham.utilities.bog.dictionary.parser;

import org.alixia.javalibrary.streams.CharacterStream;
import org.alixia.javalibrary.streams.PeekableCharacterStream;

import zeale.mouse.utils.BufferedParser;

public class DictionaryEntrySplitter extends BufferedParser<String> {

	private final PeekableCharacterStream in;

	public DictionaryEntrySplitter(PeekableCharacterStream in, boolean strip) {
		if (strip) {
			in.collectTo("<body>");// Parse out all of the header.
			String content = in.collectTo("</body>");// Parse the stuff between the <body> we just consumed above and
														// the </body>.
			in = PeekableCharacterStream.from(CharacterStream.from(content));
		}
		this.in = in;
	}

	public DictionaryEntrySplitter(CharacterStream in, boolean strip) {
		this(PeekableCharacterStream.from(in), strip);
	}

	public DictionaryEntrySplitter(PeekableCharacterStream in) {
		this(in, false);
	}

	public DictionaryEntrySplitter(CharacterStream in) {
		this(in, false);
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
	protected String read() {
		return in.peek() >= 0 ? in.parseBetween("<p>", "</p>") : null;
	}
}
