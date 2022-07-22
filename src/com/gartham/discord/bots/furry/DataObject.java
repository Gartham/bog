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
			if (!containsKey(key))
				return "";
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
			if (!containsKey(key))
				return false;
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
			if (!containsKey(key))
				return 0;
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
			if (!containsKey(key))
				return BigInteger.ZERO;
			return new BigInteger(getString(key));
		}

		/**
		 * Increases the value by the specified amount (adds it to the current value)
		 * and returns the result. The value that is returned from this method is what
		 * the entry is set to by the completion of this method.
		 * 
		 * @param amount The amount to add to this entry's value.
		 * @return The value that this entry was increased to.
		 */
		public BigInteger increase(long amount) {
			var bi = get();
			bi = bi.add(BigInteger.valueOf(amount));
			put(bi);
			return bi;
		}
	}

}
