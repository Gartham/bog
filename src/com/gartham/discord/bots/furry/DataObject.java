package com.gartham.discord.bots.furry;

import java.math.BigInteger;

import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONValue;

public class DataObject extends JSONObject {
	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	private abstract class AbstractEntry<V> {
		protected final String key;

		public AbstractEntry(String key) {
			this.key = key;
		}

		public abstract void put(V value);

		public abstract V get();

	}

	public class Entry<V extends JSONValue> extends AbstractEntry<V> {
		public Entry(String key) {
			super(key);
		}

		public void put(V value) {
			DataObject.this.put(key, value);
		}

		@SuppressWarnings("unchecked")
		public V get() {
			return (V) DataObject.this.get(key);
		}
	}

	public class StringEntry extends AbstractEntry<String> {
		public StringEntry(String key) {
			super(key);
		}

		@Override
		public String get() {
			return getString(key);
		}

		@Override
		public void put(String value) {
			DataObject.this.put(key, value);
		}
	}

	public class BooleanEntry extends AbstractEntry<Boolean> {
		public BooleanEntry(String key) {
			super(key);
		}

		@Override
		public void put(Boolean value) {
			DataObject.this.put(key, value);
		}

		@Override
		public Boolean get() {
			return getBoolean(key);
		}
	}

	public class IntEntry extends AbstractEntry<Integer> {
		public IntEntry(String key) {
			super(key);
		}

		@Override
		public void put(Integer value) {
			DataObject.this.put(key, value);
		}

		@Override
		public Integer get() {
			return getInt(key);
		}
	}

	public class BigIntegerEntry extends AbstractEntry<BigInteger> {
		public BigIntegerEntry(String key) {
			super(key);
		}

		@Override
		public void put(BigInteger value) {
			DataObject.this.put(key, value.toString());
		}

		@Override
		public BigInteger get() {
			return new BigInteger(getString(key));
		}
	}

}
